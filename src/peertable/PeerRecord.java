/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertable;

import java.net.InetAddress;

public class PeerRecord {

	public String peerID;
	public InetAddress peerIPAddress;
	public int peerSeqNum;
	/**
	 * peer expiration time, in ms
	 */
	public long expirationTime;
	public PeerState peerState;
	public int helloInterval;
	
	public long lastSeen;
	public int offlineVersion = -1;

	public PeerRecord(String peerID, InetAddress peerIPAddress, int peerSeqNum, long expirationTime, PeerState peerState, int helloInterval) {
		this.peerID = peerID;
		this.peerIPAddress = peerIPAddress;
		this.peerSeqNum = peerSeqNum;
		this.expirationTime = expirationTime;
		this.peerState = peerState;
		this.helloInterval = helloInterval;
	}
	
	@Override
	public String toString() {
		return String.format("id=%16s { ip=%16s | #seq=%3d | ov=%3d |  exp=%d | hi=%d | s=%s}", 
				peerID, peerIPAddress, peerSeqNum, offlineVersion, expirationTime, helloInterval, peerState);
//		return "PeerRecord{" + "peerID=" + peerID + ", peerIPAddress=" + peerIPAddress + ", peerSeqNum=" + peerSeqNum + ", expirationTime=" + expirationTime + ", peerState=" + peerState + '}';
	}

}
