package message;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import message.MessageException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 *
 * @author arpaf, AmaurX, Swann
 */
public class HelloMessage {

	
	private static final String HELLO = "HELLO"; 

	private String senderID;
	private int sequenceNumber;
	/**
	 * interval between messages, in s
	 */
	private int helloInterval;
	private int numPeers;
	private List<String> peers;



	/*
	*	Constructor from a fromatted string
	*	Initialization with given message
	 */
	public HelloMessage(String s) throws MessageException {
		String slist[] = s.split(";");

		if (slist.length < 5) {
			throw new MessageException("Missing arguments in the hello string");
		}

		Pattern helloPattern = Pattern.compile("(h|H)(e|E)(l|L){2}(o|O)");
		Matcher helloMatcher = helloPattern.matcher(slist[0]);

		if (!helloMatcher.matches()) {
			throw new MessageException("Not a Hello message");
		}

		senderID = slist[1];
		sequenceNumber = Integer.parseInt(slist[2]);
		helloInterval = Integer.parseInt(slist[3]);
		numPeers = Integer.parseInt(slist[4]);

		if (numPeers != (slist.length - 5)) {
			throw new MessageException("Wrong number of peer given...");
		}

		peers = new ArrayList<>();

		for (int i = 5; i < (5 + numPeers); i++) {
			peers.add(slist[i]);
		}
	}


	/*
	*	Constructor from info
	*	Initialization with no peers
	 */
	public HelloMessage(String senderIdIn, int sequenceNo, int helloIntervalIn) {
		senderID = senderIdIn;
		sequenceNumber = sequenceNo;
		helloInterval = helloIntervalIn;
		numPeers = 0;
		peers = new ArrayList<>();
	}
	
	/*
	*	Constructor from info
	*	Initialization with peers
	 */
	public HelloMessage(String senderIdIn, int sequenceNo, int helloIntervalIn, List<String> peers) {
		senderID = senderIdIn;
		sequenceNumber = sequenceNo;
		helloInterval = helloIntervalIn;
		numPeers = 0;
		this.peers = peers;
	}

	public String getHelloMessageAsEncodedString() throws MessageException {
		String result = HELLO;
		result += ";";
		result += senderID;
		result += ";";
		result += sequenceNumber;
		result += ";";
		result += helloInterval;
		result += ";";
		result += numPeers;

		if (numPeers != peers.size()) {
			throw new MessageException("numPeers isn't equal to peers.size().");
		}

		for (int i = 0; i < numPeers; i++) {
			result += ";";
			result += peers.get(i);
		}

		return result;
	}

	public void addPeer(String peerID) throws MessageException {
		if (numPeers++ > 255) {
			throw new MessageException("Cannot add another peer : maximal number of peers reached");
		}
		peers.add(peerID);
	}

	@Override
	public String toString() {
		String result = "The sender is " + senderID + "\n";
		result += "Senquence number is " + sequenceNumber;
		result += " and HelloInterval is " + helloInterval + "\n";

		if (numPeers != peers.size()) {
			System.out.println("numPeers isn't equal to peers.size().");
		}

		if (numPeers > 0) {
			result += "The " + numPeers + " peers are :\n";
			result += peers.get(0);
			for (int i = 1; i < peers.size(); i++) {
				result += ", " + peers.get(i);
			}
			result += ".\n";
		} else {
			result += "There are no peers\n";
		}
		return result;
	}

	public String getSenderID() {
		//necessary to identify the peer who sent the message in the peerTable when receiving
		return senderID;
	}

	public int getSequenceNumber() {
		//necessary to check the consistency of the message
		return sequenceNumber;
	}

	public int getHelloInterval() {
		//necessary for knowing at which frequency the messages are to be sent
		return helloInterval;
	}
}
