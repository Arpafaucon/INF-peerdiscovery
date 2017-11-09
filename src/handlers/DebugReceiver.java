/**
 * Utility message handler who prints raw network messages
 *
 * @author arpaf
 */
public class DebugReceiver extends ThreadedMessageHandler implements SimpleMessageHandler {

	@Override
	void processMessage(String m) {
		System.out.println("[RAW]" + m);
		try {
			HelloMessage hm = new HelloMessage(m);
			System.out.println("[HELLO]" + hm.toString());
		} catch (HelloMessage.HelloException ex) {

		}
	}

}
