package handlers;


import com.sun.istack.internal.logging.Logger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import main.MessagePacket;
import main.MuxDemuxSimple;

/**
 * Abstract core to message handlers
 * Provide utility core functions
 * @author arpaf
 */
public abstract class ThreadedMessageHandler extends Thread implements SimpleMessageHandler{

	private MuxDemuxSimple mds;
	private final BlockingQueue<MessagePacket> queue = new ArrayBlockingQueue<>(main.Main.HANDLER_CAPACITY);

	public ThreadedMessageHandler() {
		setName("Thr Message handler");
	}

	@Override
	public void handleMessage(MessagePacket msp) {
		if(queue.offer(msp) && main.Main.DEBUG_PRINT_DROPPED){
			//exception when putting element
			Logger.getLogger(ThreadedMessageHandler.class).info("dropped message");
		}
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		mds = md;
	}

	public MuxDemuxSimple getMuxDemux() {
		return mds;
	}

	@Override
	public void run() {
		while (true) {
			MessagePacket msp;
			try {
				msp = queue.take();
				processMessage(msp);
			} catch (InterruptedException ex) {
			} catch (Exception e){
				System.err.println("[ERR] unhndled exception in ThreadedMessageHandler :" + e.getMessage());
			}

		}
	}

	protected abstract void processMessage(MessagePacket msp);

}
