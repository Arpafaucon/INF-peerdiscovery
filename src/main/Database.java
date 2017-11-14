package main;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author arpaf
 */
public class Database {

	private static Database internalBase;
	private static List<Database> peerBase;

	private String data;
	
	private Database(String data){
		this.data = data;
	}
	
	private Database(){
		this("");
	}

	public List<String> getSplitData(int chunkSize) {
		List<String> ret = new ArrayList<>((data.length() + chunkSize - 1) / chunkSize);

		for (int start = 0; start < data.length(); start += chunkSize) {
			ret.add(data.substring(start, Math.min(data.length(), start + chunkSize)));
		}
		return ret;
	}

	@Override
	public String toString() {
		return "Database{" + "data=" + data + '}';
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * get internal singleton
	 * @return 
	 */
	public static Database getInternalDatabase(){
		if(internalBase == null){
			internalBase = new Database();
		}
		return internalBase;
	}
	
	/**
	 * get external list singleton
	 * @return 
	 */
	public static List<Database> getPeerDatabases(){
		if(peerBase == null){
			peerBase = new ArrayList<>();
		}
		return peerBase;
	}
	
	

}
