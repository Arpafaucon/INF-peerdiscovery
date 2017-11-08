package peertable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * PeerTable class tracks state of all peers, and periodically update them
 * according to messages or timeouts
 *
 * @author arpaf
 */
public class PeerTable {

	private final List<PeerRecord> table;
	private Lock tableLock;
	PeerTableUpdater peerTableUpdate;

	public PeerTable() {
		table = Collections.synchronizedList(new ArrayList<PeerRecord>());
		peerTableUpdate = new PeerTableUpdater(table, 1);
		//TODO : start in another function ?
		peerTableUpdate.start();
	}
	

	@Override
	public String toString() {
		String tableStr = "";
		//I admit that it's a bit overkill to use streams
		//in order only to print all elements...
		// but the result is the same and that makes me practice !
		tableStr = table.stream()
				.map((pr) -> pr.toString() + "\n")
				.reduce(tableStr, String::concat);
		return "PeerTable:" + tableStr;
	}

	public List<String> getPeerIdList() {
		List<String> res = new ArrayList<>();
		table.stream()
				.forEach((PeerRecord p) -> {
					res.add(p.peerID);
				});
		return res;
	}

}
