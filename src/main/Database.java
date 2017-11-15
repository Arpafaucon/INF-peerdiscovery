package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Database handling class
 * Sync is done for now with basic sync keyword
 * It should be correct, but maybe under-efficient
 * @author arpaf
 */
public class Database {

	private static Database internalBase;
	private static List<Database> peerBase;

	/**
	 * get internal singleton
	 *
	 * @return
	 */
	public static synchronized Database getInternalDatabase() {
		if (internalBase == null) {
			internalBase = new Database();
		}
		return internalBase;
	}

	/**
	 * get external list singleton
	 *
	 * @return
	 */
	public static synchronized List<Database> getPeerDatabases() {
		if (peerBase == null) {
			peerBase = new ArrayList<>();
		}
		return peerBase;
	}

	/**
	 * content of the database
	 */
	private String data;

	/**
	 * database version index
	 */
	private int sequenceNumber;

	private Database(String data) {
		this.sequenceNumber = 0;
		this.data = data;
	}

	private Database() {
		this("");
	}

	
	/**
	 * export data into chunks for use in LIST messages
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

	@Override
	public String toString() {
		return "Database{" + "data=" + getData() + '}';
	}

	public synchronized String getData() {
		return data;
	}

	public synchronized void setData(String data) {
		this.data = data;
	}

	public synchronized int getSequenceNumber() {
		return sequenceNumber;
	}

	public synchronized void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

}
