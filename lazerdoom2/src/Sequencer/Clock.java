package Sequencer;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Clock implements ClockInterface, Runnable {
	private Sequencer sequencer;
	private ScheduledThreadPoolExecutor stpe;
	
	Clock(long interval, Sequencer sequencer) {
		this.sequencer = sequencer;
		
		this.interval = interval;
		tmpInt = this.interval;
		
		this.stpe = new ScheduledThreadPoolExecutor(1);
	}
	
	private static long nanoInMs = 1000000;
		
	public static double beatMeasureToMs(int beat, int measure, double bpm) {
		return (240000*((double)beat/(double)measure)/bpm);
	}
	
	public long oneBarToTicks(long nanoInterval, double bpm) {
		return (long) ((beatMeasureToMs(1, 1, bpm)*nanoInterval)/nanoInMs);
	} 
	
	long interval;
	long tmpInt;
	long currentNanos = 0;
	long startNanos = System.nanoTime();
	long deltaAcc = 0;
	long latency = 0;
	
	long latencyAcc = 0;
	long latencyAvg = 0;
	long msRuns = 1000;
	long runs = 1;
	long cycles = 0;
	double deltaAvg = 0;
	long oldTime;
	
	@Override
	public void run() {
			startNanos = System.nanoTime();
			deltaAcc = (startNanos - currentNanos);
			cycles = 0;
			while(deltaAcc < tmpInt) {
				startNanos = System.nanoTime();
				deltaAcc = (startNanos - currentNanos);
				cycles++;
			}
			currentNanos = startNanos;
			latency = deltaAcc-this.interval;
			
			latencyAcc = latencyAcc+ latency;
			
			runs++;
			
			if((runs%msRuns)== 0) {
				System.out.println(System.currentTimeMillis()-oldTime);
				oldTime = System.currentTimeMillis();
				
				latencyAvg = latencyAcc/msRuns;
				if(latencyAvg + this.interval > this.interval) {
					tmpInt = this.interval-latencyAvg;
				} else {
					tmpInt = this.interval;
				}
				latencyAcc = 0;
			}
			sequencer.eval(runs);
	}

	@Override
	public void setInterval(long interval) {
		this.interval = interval;
		this.stpe.remove(this);
		this.stpe.scheduleAtFixedRate(this, interval, interval, TimeUnit.NANOSECONDS);
	}

	@Override
	public void setSequencer(Sequencer sequencer) {
		this.sequencer = sequencer;
	}
	
	@Override
	public void start() {
		stpe.scheduleAtFixedRate(this, 1000000000, interval, TimeUnit.NANOSECONDS);
	}

}
