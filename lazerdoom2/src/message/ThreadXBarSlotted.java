package message;

public class ThreadXBarSlotted<In, Out> extends ThreadXBar<In, Out> { 
	
	public Signal2<Object, In> executeSignal = new Signal2<Object, In>();
	
	public ThreadXBarSlotted() {
		super();
	}
	
	public ThreadXBarSlotted(int prealloc) {
		super(prealloc);
	}

	/**
	 * post result of calc
	 * @param c -- return the first argument from the signal as token! 
	 * @param out -- result
	 */
	
	public void post(Object c ,Out out) {
		Container<In,Out> container = (message.ThreadXBar<In,Out>.Container<In, Out>) c;
		container.out = out;
		this.getRecvQueue().add(container);
	}
	
	@Override
	protected Out execute(In in) {
		/* nothing to do here */
		return null;
	}
	
	public void process() {
		Container<In, Out> sendContainer = null;
		
		if((sendContainer = this.getSendQueue().poll()) != null) {
			this.executeSignal.emit(sendContainer, sendContainer.in);
		}
	}

}
