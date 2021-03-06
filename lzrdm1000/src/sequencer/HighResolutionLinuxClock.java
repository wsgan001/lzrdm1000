package sequencer;

import sequencer.nativecode.linuxclock.*;

import com.frinika.priority.Priority;

public class HighResolutionLinuxClock implements ClockInterface, Runnable{

	private SequencerInterface sequencer;
	
	private long clockStart;
	
	public HighResolutionLinuxClock(SequencerInterface sequencer) {
		this.sequencer = sequencer;
	}
	
	//FIXME: fix path
	static {
		System.load(System.getProperty("user.dir")+"/src/sequencer/nativecode/linuxclock/linuxclock.so");
	}
	
	private int numberOfLatencyMeasurements = 10;
	private int numberOfMeanLatencyMeasurements = 10;
	
	private int currentLatencyMeasurement = 0;
	private int currentMeanLatencyMeasurement = 0; 
	private long interval = 0;
	
	private long currentTick = 0;
	
	private long[] latencyMeasurements = new long[numberOfLatencyMeasurements];
	private long[] meanLatencyMeasurements = new long[numberOfMeanLatencyMeasurements];
	
	private void updateLatency(long currentMeasurement) {
		long currentLatency = currentMeasurement - interval;
		
		latencyMeasurements[currentLatencyMeasurement] = currentLatency;
		
		if(currentLatencyMeasurement == 0) {
			long meanLatency = 0;
			
			for(long val: latencyMeasurements) {
				meanLatency += val;
			}
			
			meanLatency = meanLatency/numberOfLatencyMeasurements;
			
			meanLatencyMeasurements[currentMeanLatencyMeasurement] = meanLatency;
			
			meanLatency = 0;
			//if(currentMeanLatencyMeasurement == 0) {
				for(long val: meanLatencyMeasurements) {
					meanLatency += val;
				}
				
				meanLatency = meanLatency/numberOfMeanLatencyMeasurements;

				linuxclock.set_ticktime_nanos((int)(interval-meanLatency));

			//}	
			
			currentMeanLatencyMeasurement = (currentMeanLatencyMeasurement+1) % numberOfMeanLatencyMeasurements;
			
		}
		
		currentLatencyMeasurement = (currentLatencyMeasurement+1)%numberOfLatencyMeasurements;
		//System.out.println(currentLatencyMeasurement+" "+currentLatency);
	}
	
	@Override
	public void setInterval(long intervalNanos) {
		this.interval = intervalNanos;
		linuxclock.set_ticktime_nanos((int)intervalNanos);
	}

	@Override
	public void setSequencer(SequencerInterface sequencer) {
		this.sequencer = sequencer;
	}

	@Override
	public void start() {
		Thread clockThread = new Thread(this);
		clockThread.setName("HPClock-rt");
		clockThread.setPriority(Thread.MAX_PRIORITY);
		clockThread.start();
	}

	private int priorityRequested = 80;
	private int priority = 0;
	
	private boolean run = true;
	
	
	public void stopClock() {
		run = false;
	}
	@Override
	public void run() {
		
		while(run) {
			clockStart = System.nanoTime();
			linuxclock.next_tick();
			sequencer.processTick(currentTick++);
			if (priorityRequested != priority) {
				System.err.println(" Clock priority requested "
						+ priorityRequested);
				Priority.setPriorityRR(priorityRequested);
				priority = priorityRequested;
			}
			
			this.updateLatency(System.nanoTime()-clockStart);
		}
	}
}
