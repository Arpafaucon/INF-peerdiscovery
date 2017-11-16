package message;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 *
 * @author arpaf, AmaurX, Swann
 */
public class HelloMessage implements Message{

	
	private static final String HELLO = "HELLO"; 
	private static final int MAX_HELLO_INTERVAL = 255;
//	private static final Pattern HELLO_PATTERN = Pattern.compile("HELLO;(\\w+);")

	private final String senderId;
	private final int sequenceNumber;
	/**
	 * interval between messages, in s
	 */
	private final int helloInterval;
	private int numPeers;
	private final List<String> peers;

	public static HelloMessage parse(String s) throws MessageException{
		String slist[] = s.split(";");

		if (slist.length < 5) {
			throw new MessageException("Missing arguments in the hello string");
		}

		Pattern helloPattern = Pattern.compile("(h|H)(e|E)(l|L){2}(o|O)");
		Matcher helloMatcher = helloPattern.matcher(slist[0]);

		if (!helloMatcher.matches()) {
			throw new MessageException("Not a Hello message");
		}

		String senderId = slist[1];
		int sequenceNumber = Integer.parseInt(slist[2]);
		int helloInterval = Integer.parseInt(slist[3]);
		int numPeers = Integer.parseInt(slist[4]);
		
		if(helloInterval < 0 || helloInterval > MAX_HELLO_INTERVAL){
			throw new MessageException("Invalid hello Interval");
		}

		if (numPeers != (slist.length - 5)) {
			throw new MessageException("Wrong number of peer given...");
		}

		List<String> peers = new ArrayList<>();

		for (int i = 5; i < (5 + numPeers); i++) {
			peers.add(slist[i]);
		}
		
		return new HelloMessage(senderId, sequenceNumber, helloInterval, peers);
	}

	/*
	*	Constructor from info
	*	Initialization with no peers
	 */
	public HelloMessage(String senderIdIn, int sequenceNo, int helloIntervalIn) {
		senderId = senderIdIn;
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
		senderId = senderIdIn;
		sequenceNumber = sequenceNo;
		helloInterval = helloIntervalIn;
		numPeers = 0;
		this.peers = peers;
	}

	@Override
	public String toEncodedString() {
		String result = String.format("HELLO;%s;%s;%d;%d", 
				senderId, sequenceNumber, helloInterval, numPeers);

//		if (numPeers != peers.size()) {
//			throw new MessageException("numPeers isn't equal to peers.size().");
//		}

		StringBuilder res = new StringBuilder(result);
		for (int i = 0; i < numPeers; i++) {
			res.append(";").append(peers.get(i));
//			result += ";";
//			result += peers.get(i);
		}
		return res.toString();
	}

	public void addPeer(String peerID) throws MessageException {
		if (numPeers++ > 255) {
			throw new MessageException("Cannot add another peer : maximal number of peers reached");
		}
		peers.add(peerID);
	}

	public String toVerboseString() {
		String result = "The sender is " + senderId + "\n";
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

	public String getSenderId() {
		//necessary to identify the peer who sent the message in the peerTable when receiving
		return senderId;
	}

	public int getSequenceNumber() {
		//necessary to check the consistency of the message
		return sequenceNumber;
	}

	public int getHelloInterval() {
		//necessary for knowing at which frequency the messages are to be sent
		return helloInterval;
	}

	@Override
	public String toString() {
		return "HELLO{" + "senderId=" + senderId + ", sequenceNumber=" + sequenceNumber + ", helloInterval=" + helloInterval + ", numPeers=" + numPeers + ", peers=" + peers + '}';
	}

	@Override
	public boolean isForMe() {
		return true;
	}
	
	
	
	
}
