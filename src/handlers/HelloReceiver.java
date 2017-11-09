package handlers;


import hello.HelloException;
import hello.HelloMessage;



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
		} catch (HelloException ex) {
		}
	}

}
