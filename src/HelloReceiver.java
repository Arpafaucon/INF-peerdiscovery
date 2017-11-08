import java.util.logging.Level;

/**
 *
 * @author arpaf
 */
public class HelloReceiver implements SimpleMessageHandler {

	MuxDemuxSimple mds;

	@Override
	public void handleMessage(String m) {
		try {
			HelloMessage hm = new HelloMessage(m);
			System.out.println("HELLO RECEIVED]\n" + hm.toString());
		} catch (HelloMessage.HelloException ex) {
		}
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		mds = md;
	}
}
