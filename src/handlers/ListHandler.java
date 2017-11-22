/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlers;

import database.Database;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import main.MessagePacket;
import message.ListMessage;
import message.MessageException;
import message.SynMessage;

/**
 * Works in cooperation with SynSender to sync databases
 *
 * @author arpaf
 */
public class ListHandler extends ThreadedMessageHandler {
	
	static final Logger logger = Logger.getLogger(ListHandler.class.getName());

	private enum BundleState {
		/**
		 * initialized but no answer for now
		 */
		WAITING,
		/**
		 * got at least one message : peer is answering
		 */
		RECEIVING,
		/**
		 * message was completed and the database was updated the Bundle is
		 * ready to be destroyed
		 */
		COMPLETED,
		/**
		 * something caused the bundling to fail must wait until new SYN message
		 * and reset cause we wont get all packets
		 */
		FAILED;
	}

	private static class ListBundle {

		private final List<ListMessage> messages = new ArrayList<>();
		public final long creationTimestamp;
		public final int seqNb;
		public final String peerId;
		private int totalParts = -1;
		private BundleState state = BundleState.WAITING;
		private String assembledMessage = null;

		public ListBundle(String peerId, int seqNb) {
			this.peerId = peerId;
			this.seqNb = seqNb;
			creationTimestamp = System.currentTimeMillis();
		}

		public void add(ListMessage lm) {
			if (lm.isForMe()
					&& peerId.equals(lm.getSenderId())
					&& !(state == BundleState.FAILED)) {
				//message need further inspection
				//appropriate seqNb?
				if (lm.sequenceNb != seqNb) {
					state = BundleState.FAILED;
					return;
				}
				//coherent total parts nb ?
				if (totalParts == -1) {
					totalParts = lm.totalParts;
				}
				if (totalParts != lm.totalParts) {
					state = BundleState.FAILED;
					return;
				}
				//valid message ?
				//non-failing error
				if (seqNb < 0 || seqNb >= totalParts) {
					return;
				}
				//LEGIT MESSAGE
				messages.add(lm);

			}

		}

		@Override
		public String toString() {
			return "ListBundle{" + "creationTimestamp=" + creationTimestamp + ", seqNb=" + seqNb + ", peerId=" + peerId + ", totalParts=" + totalParts + ", state=" + state + " messages=\n" + messages + "\nassembledMessage=" + assembledMessage + '}';
		}

		/**
		 * checks if all messages are present if so, builds the data message
		 *
		 * @return true if assembling succeeded
		 */
		private boolean tryAssembling() {
			StringBuilder data = new StringBuilder();
			for (int i = 0; i < totalParts; i++) {
				ListMessage lm = scanForNb(i);
				if (lm != null) {
					data.append(lm.data);
				} else {
					return false;
				}
			}
			assembledMessage = data.toString();
			Database peerBase = database.Database.getPeerBase(peerId);
			peerBase.setData(assembledMessage);
			peerBase.setSequenceNumber(seqNb);
			state = BundleState.COMPLETED;
			return true;
		}

		private ListMessage scanForNb(int num) {
			for (ListMessage lm : messages) {
				if (lm.sequenceNb == num) {
					return lm;
				}
			}
			return null;
		}

	}

	private static final ConcurrentHashMap<String, ListBundle> table = new ConcurrentHashMap<>();

	@Override
	protected void processMessage(MessagePacket msp) {
		String mes = msp.msg;
//		System.out.println("mes : " + mes);
		try {
			ListMessage lm = ListMessage.parse(msp.msg);
			ListBundle bundle = table.get(lm.peerId);
			if (bundle != null) {
				bundle.add(lm);
				bundle.tryAssembling();
			}

		} catch (MessageException ex) {
			//message just isn't a list : no need to panic
		}

	}

	/**
	 * instanciating bundle to handle list messages
	 *
	 * update Policy: <br>
	 * - only one version per peer: we delete the older one <br>
	 * - FAILED versions are overwritten
	 * - 
	 *
	 * @param synMessage
	 */
	public void createBundle(SynMessage synMessage) {
		cleanTable();
		ListBundle lb = table.get(synMessage.getPeerId());
		if(lb == null || lb.seqNb < synMessage.getSequenceNb() || lb.state == BundleState.FAILED){
			//no, outdated or failed bundle
			//we overwrite it
			table.put(synMessage.getPeerId(), new ListBundle(synMessage.getPeerId(), synMessage.getSequenceNb()));
		}
	}
	
	/**
	 * purge the table
	 * 
	 * Delete COMPLETED and FAILED entries
	 */
	private void cleanTable(){
		table.forEachEntry(NORM_PRIORITY, (entry)->{
			if(entry.getValue().state == BundleState.COMPLETED || entry.getValue().state == BundleState.FAILED){
				table.remove(entry.getKey());
			}
		});
//		logger.fine("cleaned ListHandler table");
	}

	@Override
	public String toString() {
		String res = table.toString();
		return "ListHandler : \n" + res;
	}
	
	
	

}
