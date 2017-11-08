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
	int expirationTime;
	PeerState peerState;

	public PeerRecord(String peerID, InetAddress peerIPAddress, int peerSeqNum, int expirationTime, PeerState peerState) {
		this.peerID = peerID;
		this.peerIPAddress = peerIPAddress;
		this.peerSeqNum = peerSeqNum;
		this.expirationTime = expirationTime;
		this.peerState = peerState;
	}

	@Override
	public String toString() {
		return "PeerRecord{" + "peerID=" + peerID + ", peerIPAddress=" + peerIPAddress + ", peerSeqNum=" + peerSeqNum + ", expirationTime=" + expirationTime + ", peerState=" + peerState + '}';
	}

}
