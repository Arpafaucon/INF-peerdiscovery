/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertable;

import java.net.InetAddress;

public class PeerRecord {

	String peerID;
	InetAddress peerIPAddress;
	int peerSeqNum;
	/**
	 * peer expiration time, in ms
	 */
	long expirationTime;
	PeerState peerState;

	public PeerRecord(String peerID, InetAddress peerIPAddress, int peerSeqNum, long expirationTime, PeerState peerState) {
		this.peerID = peerID;
		this.peerIPAddress = peerIPAddress;
		this.peerSeqNum = peerSeqNum;
		this.expirationTime = expirationTime;
		this.peerState = peerState;
	}
	
	@Override
	public String toString() {
		return String.format("Record{ id=%16s | ip=%16s | #seq=%3d | exp=%d | s=%s}", 
				peerID, peerIPAddress, peerSeqNum, expirationTime, peerState);
//		return "PeerRecord{" + "peerID=" + peerID + ", peerIPAddress=" + peerIPAddress + ", peerSeqNum=" + peerSeqNum + ", expirationTime=" + expirationTime + ", peerState=" + peerState + '}';
	}

}
