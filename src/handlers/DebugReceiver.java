package handlers;

import message.HelloMessage;
import message.MessageException;
import main.MessagePacket;
import message.ListMessage;
import message.SynMessage;

/**
 * Utility message handler who prints raw network messages
 *
 * @author arpaf
 */
public class DebugReceiver extends ThreadedMessageHandler {

	@Override
	protected void processMessage(MessagePacket msp) {
		if (main.Main.DEBUG_PRINT_RAW) {
			System.out.println("[RAW]" + msp.address + ":" + msp.msg);
		}
		try {
			HelloMessage hm = HelloMessage.parse(msp.msg);
			System.out.println("[HELLO] " + hm.toString());
		} catch (MessageException ex) {

		}
		try {
			SynMessage sm = SynMessage.parse(msp.msg);
			System.out.println("[SYN] " + sm.toString());
		} catch (MessageException ex) {

		}
		try {
			ListMessage lm = ListMessage.parse(msp.msg);
			System.out.println("[LIST] " + lm.toString());
		} catch (MessageException ex) {

		}
	}

}
