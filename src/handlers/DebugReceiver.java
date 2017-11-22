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
		boolean handled = false;

		try {
			HelloMessage hm = HelloMessage.parse(msp.msg);
			if (hm.isForMe()) {
				System.out.println("[HELLO] " + hm.toString());
			}
			handled = true;
		} catch (MessageException ex) {

		}
		try {
			SynMessage sm = SynMessage.parse(msp.msg);
			if (sm.isForMe()) {
				System.out.println("[SYN] " + sm.toString());
			}
			handled = true;
		} catch (MessageException ex) {

		}
		try {
			ListMessage lm = ListMessage.parse(msp.msg);
			if (lm.isForMe()) {
				System.out.println("[LIST] " + lm.toString());
			}
			handled = true;
		} catch (MessageException ex) {

		}
		if (main.Main.DEBUG_PRINT_RAW && !handled) {
			System.out.println("[RAW]" + msp.address + ":" + msp.msg);
		}

	}

}
