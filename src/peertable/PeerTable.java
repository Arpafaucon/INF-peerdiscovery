package peertable;

import message.HelloMessage;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PeerTable class tracks state of all peers, and periodically update them
 * according to messages or timeouts
 * 
 * Sync is done with synchronized blocks at function level, and using a sync hashmap
 * @author arpaf
 */
public class PeerTable {

//	private final List<PeerRecord> table;
	private final ConcurrentHashMap<String, PeerRecord> peerTable;
	private static PeerTable table;
//	private Lock tableLock;

	public static synchronized PeerTable getTable() {
		if (table == null) {
			table = new PeerTable();
		}
		return table;
	}

	private PeerTable() {
//		table = Collections.synchronizedList(new ArrayList<PeerRecord>());
		peerTable = new ConcurrentHashMap();
	}

	/**
	 * remove obsolete entries
	 */
	private synchronized void cleanTable() {
		peerTable.forEach((peerId, record) -> {
			if (record.expirationTime > System.currentTimeMillis() / 1000) {
				peerTable.remove(peerId);
			}
		});
//		table.stream()
//				//selecting old entries
//				.filter((peerRecord) -> (peerRecord.expirationTime > System.currentTimeMillis() / 1000))
//				//deleting them
//				.forEach((peerRecord) -> {
//					table.remove(peerRecord);
//				});
	}

	public synchronized void updatePeer(HelloMessage hm, InetAddress address, long time) {
		cleanTable();
		String peerId = hm.getSenderID();
		if (peerId.equals(main.Main.ID)) {
			return;
		}
		PeerRecord pr = peerTable.get(peerId);
		if (pr != null) {
			//peer already registered
			pr.expirationTime = time + hm.getHelloInterval();
			if (hm.getSequenceNumber() != pr.peerSeqNum) {
				pr.peerState = PeerState.INCONSISTENT;
			}
			//updating address & other data
			pr.peerIPAddress = address;
		} else {
			//registering peer
			//!helloInterval is in s
			pr = new PeerRecord(peerId, address, -1, hm.getHelloInterval() * 1000 + time, PeerState.HEARD);
			peerTable.put(peerId, pr);
		}
	}

	@Override
	public synchronized String toString() {
		String tableStr = "";
		//I admit that it's a bit overkill to use streams
		//in order only to print all elements...
		// but the result is the same and that makes me practice !
		tableStr = peerTable.entrySet().stream()
				.map((record) -> record.getValue().toString())
				.reduce(tableStr, String::concat);
		return "PeerTable:\n" + tableStr;
	}

	public synchronized List<String> getPeerIdList() {
//		List<String> res = new ArrayList<>();
//		table.stream()
//				.forEach((PeerRecord p) -> {
//					res.add(p.peerID);
//				});
//		return res;
		return new ArrayList<>(peerTable.keySet());
	}

}
