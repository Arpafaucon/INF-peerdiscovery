package peertable;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		
	}

	@Override
	public String toString() {
		String tableStr = "";
		//I admit that it's a bit overkill to just print all elements...
		// but the result is the same and that makes me practice !
		tableStr = table.stream()
				.map((pr) -> pr.toString() + "\n")
				.reduce(tableStr, String::concat);
		return "PeerTable:" + tableStr;
	}

}
