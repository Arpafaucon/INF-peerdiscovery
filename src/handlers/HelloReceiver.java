package handlers;


import hello.HelloException;
import hello.HelloMessage;
import main.MessagePacket;
import peertable.PeerTable;



/**
 * Handles received hello Messages
 * display them, and update the peertable accordingly
 * @author arpaf
 */
public class HelloReceiver extends ThreadedMessageHandler implements SimpleMessageHandler {

	PeerTable peerTable;

	public HelloReceiver(PeerTable peerTable) {
		this.peerTable = peerTable;
	}
	
	@Override
	void processMessage(MessagePacket msp) {
		try {
			HelloMessage hm = new HelloMessage(msp.msg);
			System.out.println("HELLO RECEIVED]\n" + hm.toString());
			peerTable.updatePeer(hm, msp.address, msp.time);
		} catch (HelloException ex) {
		}
	}


}
