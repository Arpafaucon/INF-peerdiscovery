package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Database handling class Sync is done for now with basic sync keyword It
 * should be correct, but maybe under-efficient
 *
 * @author arpaf
 */
public class Database {

	private static Database internalBase;
	private static Map<String, Database> peerBase;

	/**
	 * get internal singleton
	 *
	 * @return
	 */
	public static synchronized Database getInternalDatabase() {
		if (internalBase == null) {
			internalBase = new Database("", 0, main.Main.ID);
		}
		return internalBase;
	}

	/**
	 * get external list singleton
	 *
	 * @return
	 */
	private static synchronized Map<String, Database> getPeerDatabases() {
		if (peerBase == null) {
			peerBase = new ConcurrentHashMap<>();
		}
		return peerBase;
	}

	public static synchronized String getExternalSummary() {
		StringBuilder b = new StringBuilder();
		peerBase.forEach((peer, base) -> {
			b.append(peer).append("=> ").append(base.toString()).append("\n");
		});
		return b.toString();
	}

	/**
	 * get specific peer Database Fixed bug : the updated database wasn't stored
	 * into memory
	 *
	 * @param peerId
	 * @return non-null base
	 */
	public static synchronized Database getPeerBase(String peerId) {
		Database base = getPeerDatabases().get(peerId);
		if (base == null) {
			//peer not yet created
			base = new Database(peerId);
			getPeerDatabases().put(peerId, base);
		}
		return base;
	}

	public synchronized static String getSummary() {
		return String.format("== Internal ==%n%s%n"
				+ "== External == (%d)%n%s%n",
				Database.getInternalDatabase().toString(),
				Database.getPeerDatabases().size(),
				Database.getExternalSummary());
	}

	/**
	 * content of the database
	 */
	private String data;

	/**
	 * database version index
	 */
	private int sequenceNumber;

	/**
	 * Peer ID corresponding to the database
	 */
	private final String peerId;

	private Database(String data, int sequenceNumber, String peerId) {
		this.data = data;
		this.sequenceNumber = sequenceNumber;
		this.peerId = peerId;
	}

	private Database(String peerId) {
		this("", -1, peerId);
	}

	/**
	 * export data into chunks for use in LIST messages 
	 * for TD1 & TD2 only
	 *
	 * @param chunkSize the size of chunks (under 255 is recommended)
	 * @return list of chunks
	 */
	public synchronized List<String> getSplitData(int chunkSize) {
		List<String> ret = new ArrayList<>((data.length() + chunkSize - 1) / chunkSize);

		for (int start = 0; start < data.length(); start += chunkSize) {
			ret.add(data.substring(start, Math.min(data.length(), start + chunkSize)));
		}
		return ret;
	}

	/**
	 * export data into chunks for use in LIST messages 
	 * 
	 * TD3 : <code>\n</code> splits also a
	 * message (different file names in different messages)
	 *
	 * @return list of chunks
	 */
	public synchronized List<String> getSplitData() {
		String[] filenames = data.split("\n");
		System.out.println("filenames split" + Arrays.toString(filenames));
		List<String> ret = new ArrayList<>();
		for (String file : filenames) {
			ret.add(file);
//			for (int start = 0; start < file.length(); start += Main.CHUNK_SIZE) {
//				ret.add(file.substring(start, Math.min(file.length(), start + Main.CHUNK_SIZE)));
//			}
		}
		return ret;
	}

	@Override
	public String toString() {
		return "DB (" + getSequenceNumber() + "){" + "data=\n" + getData() + '}';
	}

	public synchronized String getData() {
		return data;
	}

	public synchronized void setData(String data) {
		this.data = data;
	}

	public synchronized void incrSeqNb() {
		this.sequenceNumber++;
	}

	public synchronized int getSequenceNumber() {
		return sequenceNumber;
	}

	public synchronized void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public static debug.DebuggableComponent getDebuggableComponent() {
		return () -> getSummary();
	}

}
