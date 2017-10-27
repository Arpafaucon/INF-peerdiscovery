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


	public String getHelloMessageAsEncodedString() throws Exception
	{
		String result = HELLO;
		result += ";";
		result += senderID;
		result += ";";
		result += senquenceNumber;
		result += ";";
		result += helloInterval;
		result += ";";
		result += numPeers;

		if(numPeers != peers.size())
		{
			throw new Exception("numPeers isn't equal to peers.size().");
		}

		for(int i = 0; i < numPeers; i++)
		{
			result += ";";
			result += peers.elementAt(i);
		}

		return result;
	}
	
	public void addPeer(String peerID) throws Exception 
	{
		if (numPeers++ > 255)
		{
			throw new Exception("Cannot add another peer : maximal number of peers reached"); 
		}
		peers.add(peerID);
		numPeers++;
	}
	
	public String toString()
	{
		return this.getHelloMessageAsEncodedString();
	}
	
	public String getSenderID() 
	{
		//necessary to identify the peer who sent the message in the peerTable when receiving
		return senderID ;
	}
	
	public int getSequenceNumber() 
	{
		//necessary to check the consistency of the message
		return sequenceNumber ;
	}
	
	public int getHelloInterval() 
	{
		//necessary for knowing at which frequency the messages are to be sent
		return helloInterval ;
	}
}
