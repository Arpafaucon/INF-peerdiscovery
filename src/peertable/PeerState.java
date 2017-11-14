package peertable;

/**
 *
 * @author arpaf
 */
public enum PeerState {
	/**
	 * initial state of a fresh discovered peer
	 */
	HEARD,
	/**
	 * sequence numbers gotten from the peer where inconsistent
	 */
	INCONSISTENT,
	/**
	 * peerSeqNumbers are consistent
	 */
	SYNCHRONISED,
	/**
	 * peer entry has timed out
	 * unused for now; entry is directly removed
	 */
	DYING;
}
