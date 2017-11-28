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
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MessagePacket;
import message.ListMessage;
import message.MessageException;
import message.SynMessage;
import peertable.PeerException;
import peertable.PeerState;

/**
 * Works in cooperation with SynSender to sync databases
 *
 * @author arpaf
 */
public class ListHandler extends ThreadedMessageHandler implements debug.DebuggableComponent{

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
			System.out.println("\t\tLIST got message from " + peerId + " -- " + lm.toString());
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
				if (seqNb < 0 || lm.partNb >= totalParts) {
					return;
				}
				//LEGIT MESSAGE
				messages.add(lm);
				state = BundleState.RECEIVING;

			}

		}

		@Override
		public String toString() {
			return "ListBundle{" + "creationTimestamp=" + creationTimestamp
					+ ", seqNb=" + seqNb + ", peerId=" + peerId
					+ ", totalParts=" + totalParts + ", state="
					+ state + "\nassembledMessage=" + assembledMessage + '}';
		}

		public String stringOfMessages() {
			return messages.stream().map(m -> "\t" + m.toString() + "\n")
					.reduce("", String::concat);
		}

		/**
		 * checks if all messages are present if so, builds the data message
		 *
		 * @return true if assembling succeeded
		 */
		private boolean tryAssembling() {
//			System.out.println("\t\t LIST try assembling ");
			StringBuilder data = new StringBuilder();
			for (int i = 0; i < totalParts; i++) {
				ListMessage lm = scanForNb(i);
				if (lm != null) {
					data.append(lm.data);
//					System.out.println("\t\t found " + i);
				} else {
					return false;
				}
			}
			assembledMessage = data.toString();
			System.out.println("ASSEMBLED DATABASE of " + peerId + assembledMessage);
			Database peerBase = database.Database.getPeerBase(peerId);
			peerBase.setData(assembledMessage);
			peerBase.setSequenceNumber(seqNb);
			System.out.println("database" + peerBase);
			
			state = BundleState.COMPLETED;
			try {
				peertable.PeerTable.getTable().updatePeerState(peerId,
						PeerState.SYNCHRONISED);
			} catch (PeerException ex) {
				System.err.println("WARNING : ListHandler tried to update non-existent peer");
			}
			return true;
		}

		private ListMessage scanForNb(int num) {
			System.out.println("scanning " + num + " mes" + messages.size());
			for (ListMessage lm : messages) {
				System.out.println("reviewing " + lm.partNb + " == " + lm.toEncodedString());
				if (lm.partNb == num) {
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
			if (lm.isForMe()) {
				ListBundle bundle = table.get(lm.senderId);
				if (bundle != null) {
					bundle.add(lm);
					bundle.tryAssembling();
				}
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
	 * - FAILED versions are overwritten -
	 *
	 * @param synMessage
	 */
	public void createBundle(SynMessage synMessage) {
		cleanTable();
		ListBundle lb = table.get(synMessage.getPeerId());
		if (lb == null
				|| lb.seqNb < synMessage.getSequenceNb()
				|| lb.state == BundleState.FAILED) {
			//no, outdated or failed bundle
			//we overwrite it
			table.put(synMessage.getPeerId(),
					new ListBundle(synMessage.getPeerId(), synMessage.getSequenceNb()));
		}
	}

	/**
	 * purge the table
	 *
	 * Delete COMPLETED and FAILED entries
	 */
	private void cleanTable() {
		table.forEachEntry(NORM_PRIORITY, (entry) -> {
			if (entry.getValue().state == BundleState.COMPLETED
					|| entry.getValue().state == BundleState.FAILED) {
				table.remove(entry.getKey());
			}
		});
//		logger.fine("cleaned ListHandler table");
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("ListHandler\n");
		table.entrySet().stream().map((e) -> {
			b.append("-----\n").append(e.getValue().toString()).append("->\n");
			return e;
		}).forEachOrdered((e) -> {
			b.append(e.getValue().stringOfMessages());
		});
		return b.toString();
//		String res = table.toString();
//		return "ListHandler : \n" + res;
	}

	@Override
	public String readState() {
		return toString();
	}
	
	

}
