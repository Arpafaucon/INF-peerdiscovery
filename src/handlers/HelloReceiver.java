package handlers;

import message.MessageException;
import message.HelloMessage;
import main.MessagePacket;
import peertable.PeerTable;

/**
 * Handles received hello Messages display them, and update the peertable
 * accordingly
 *
 * @author arpaf
 */
public class HelloReceiver extends ThreadedMessageHandler {

	PeerTable peerTable;

	/**
	 *
	 * @param peerTable
	 */
	public HelloReceiver(PeerTable peerTable) {
		this.peerTable = peerTable;
	}

	@Override
	protected void processMessage(MessagePacket msp) {
		try {
			HelloMessage hm = HelloMessage.parse(msp.msg);
			System.out.println("HELLO RECEIVED]\n" + hm.toString());
			peerTable.updatePeer(hm, msp.address, msp.time);
		} catch (MessageException ex) {
		}
	}

}
