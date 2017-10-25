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
	int helloInterval;
	int numPeers;
	Vector<String> peers;

	static public String HELLO = "HELLO"; 


	/*
	*	Constructor from a fromatted string
	*	Initializarion with given peers
	*/
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
		helloInterval = Integer.parseInt(slist[3]);
		numPeers = Integer.parseInt(slist[4]);

		if(numPeers != (slist.length - 5))
		{
			throw new Exception("Wrong number of peer given...");
		}

		peers = new Vector<String>();

		for(int i = 5; i < (5 + numPeers); i++)
		{
			peers.add(slist[i]);
		}
	}


	/*
	*	Constructor from info
	*	Initializarion with no peers
	*/
	public HelloMessage(String senderIdIn, int sequenceNo, int helloIntervalIn)
	{
		senderId = senderIdIn;
		senquenceNumber = sequenceNo;
		helloInterval = helloIntervalIn;
		numPeers = 0;
		peers = new Vector<String>();		
	}


	
}
