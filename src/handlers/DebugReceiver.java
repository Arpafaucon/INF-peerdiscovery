package handlers;


import hello.HelloMessage;
import hello.HelloException;
import main.MessagePacket;

/**
 * Utility message handler who prints raw network messages
 *
 * @author arpaf
 */
public class DebugReceiver extends ThreadedMessageHandler 
		implements SimpleMessageHandler {

	@Override
	void processMessage(MessagePacket msp) {
		System.out.println("[RAW]" + msp.address + ":" + msp.msg);
		try {
			HelloMessage hm = new HelloMessage(msp.msg);
			System.out.println("[HELLO]" + hm.toString());
		} catch (HelloException ex) {

		}
	}

}
