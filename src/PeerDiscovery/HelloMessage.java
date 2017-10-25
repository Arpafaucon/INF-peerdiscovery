/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PeerDiscovery;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author arpaf
 */
public class HelloMessage {
	String senderID;
	int sequenceNumber;
	int HelloInterval;
	int NumPeers;
	Vector<String> peers;

	static public String HELLO = "HELLO"; 

	public HelloMessage(String s) throws Exception
	{
		String slist[] = s.split(";");

		if(slist.length < 5){
			throw new Exception("Missing arguments in the hello string");
		}

		if(!slist[0].equals(HELLO))
		{
			throw new Exception("Not a Hello message");
		}

		senderID = slist[1];
		sequenceNumber = Integer.parseInt(slist[2]);
		HelloInterval = Integer.parseInt(slist[3]);
		NumPeers = Integer.parseInt(slist[4]);

		if(NumPeers != (slist.length - 5))
		{
			throw new Exception("Wrong number of peer given...");
		}

		peers = new Vector<String>();

		for(int i = 5; i < (5 + NumPeers); i++)
		{
			peers.add(slist[i]);
		}
	}
}
