
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;


/**
 * PeerTable class
 * tracks state of all peers, and periodically update them according to messages
 * or timeouts
 * @author arpaf
 */
public class PeerTable {
	public enum PeerState{
		HEARD,
		INCONSISTENT,
		SYNCHRONISED,
		DYING;
	}
	
	public static class PeerRecord{
		String peerID;
		InetAddress peerIPAddress;
		int peerSeqNum;
		int expirationTime;
		PeerState peerState;
	}
	
	public class PeerTableUpdater extends Thread{
		private final int PERIOD;
		private boolean done = false;

		public PeerTableUpdater(int PERIOD) {
			this.PERIOD = PERIOD;
		}
		
		@Override
		public void run() {
			while(!done){
				if(this.isInterrupted()){
					done = true;
				}
			}
		}
		
		
	}
	
	private List<PeerRecord> table;
	private Lock tableLock;

	public PeerTable() {
		table = new ArrayList<>();
	}
	
	
	
	
}
