/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sender;

import handlers.ListHandler;
import main.Main;
import main.MuxDemuxSimple;
import message.SynMessage;
import peertable.PeerTable;

/**
 * class actively ensuring peer synchronisation
 *
 * the thread periodically checks all peer states to start a peer sync process
 * if needed
 *
 * @author arpaf
 */
public class SynSender extends Thread {

	MuxDemuxSimple mds;
	ListHandler listHandler;

	public SynSender(MuxDemuxSimple mds, ListHandler listHandler) {
		setName("SynSender");
		this.mds = mds;
		this.listHandler = listHandler;
	}

	/**
	 * Launch a ssynchronisation attempt with all peers 
	 */
	private void synPeers() {
		if (Main.ID != null) {
			//this weird condition avoids sending SYN messages with null ID when
			//the server is shutting down
			PeerTable.getTable().getUnsyncPeers().forEach((record) -> {
				SynMessage sm = new SynMessage(Main.ID, record.peerID, record.peerSeqNum);
				mds.send(sm.toEncodedString());
				listHandler.createBundle(sm);
			});
		}
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				//for question 2-3
				synPeers();
				Thread.sleep(Main.SYN_UPDATE * 1000);

			} catch (InterruptedException ex) {
			}
		}
	}

}
