package handlers;

import main.MuxDemuxSimple;
import java.util.concurrent.SynchronousQueue;
import main.MessagePacket;

/**
 * @deprecated 
 * just pinging back helloMessages...
 * @author arpaf
 */
@Deprecated
public class HelloHandler implements SimpleMessageHandler, Runnable {

	private final SynchronousQueue<String> incoming = new SynchronousQueue<>();
	private MuxDemuxSimple myMuxDemux = null;

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		myMuxDemux = md;
	}

	@Override
	public void handleMessage(MessagePacket msp) {
		try {
			incoming.put(msp.msg);
		} catch (InterruptedException ex) {
		}
	}

	
	@Override
	public void run() {
		while (true) {
			try {
				String msg = incoming.take();
				myMuxDemux.send(msg);
			} catch (InterruptedException e) {
			}
		}
	}

}
