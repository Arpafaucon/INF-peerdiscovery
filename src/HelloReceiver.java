
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arpaf
 */
public class HelloReceiver implements SimpleMessageHandler, Runnable {

	private final static int CAPACITY = 10;
	MuxDemuxSimple mds;
	BlockingQueue<String> queue = new LinkedBlockingQueue<>(CAPACITY);

	@Override
	public void handleMessage(String m) {
		queue.add(m);
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		mds = md;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String m = queue.take();
				HelloMessage hm = new HelloMessage(m);
				System.out.println("HELLO RECEIVED]\n" + hm.toString());
			} catch (InterruptedException ex) {
			} catch (HelloMessage.HelloException ex) {
			}

		}
	}
}
