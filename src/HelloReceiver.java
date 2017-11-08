/**
 *
 * @author arpaf
 */
public class HelloReceiver extends ThreadedMessageHandler implements SimpleMessageHandler {

	@Override
	void processMessage(String msg) {
		try {
			HelloMessage hm = new HelloMessage(msg);
			System.out.println("HELLO RECEIVED]\n" + hm.toString());
		} catch (HelloMessage.HelloException ex) {
		}
	}

}
