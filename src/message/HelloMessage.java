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
public class HelloMessage implements Message {

	private static final String HELLO = "HELLO";
	private static final int MAX_HELLO_INTERVAL = 255;
	private static final int MAX_PEER_NAME_LENGTH = 16;
	/**
	 * Pattern is
	 * HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN
	 */
	private static final Pattern HELLO_PATTERN = 
			Pattern.compile("(?i)HELLO(?-i);(\\w+);(\\d+);(\\d+);(\\d+)((?:;\\w+)*)");
	private static final Pattern PEER_PATTERN = Pattern.compile("[\\w]{1,16}");

	public static HelloMessage parse(String s) throws MessageException {
		Matcher hMatcher = HELLO_PATTERN.matcher(s);
		if (!hMatcher.find()) {
			throw new MessageException("global format of the HELLO message is invalid");
		}
		//valid message . going on
		String peer = hMatcher.group(1);
		if (peer.length() > MAX_PEER_NAME_LENGTH) {
			throw new MessageException("Peer name too long");
		}

		int sequenceNb = Integer.parseInt(hMatcher.group(2));
		if (sequenceNb < 0) {
			throw new MessageException("Invalid sequence number");
		}

		int interval = Integer.parseInt(hMatcher.group(3));
		if (interval <= 0 || interval > MAX_HELLO_INTERVAL) {
			throw new MessageException("Invalid hello Interval");
		}

		int peerNb = Integer.parseInt(hMatcher.group(4));
		/**
		 * A ;-separated list of peers starting with a ;
		 */
		String peerList = hMatcher.group(5);
		String slist[] = peerList.split(";");
		List<String> peers = new ArrayList<>();
		for(String peerid: slist){
			if(PEER_PATTERN.matcher(peerid).matches()){
				peers.add(peerid);
			} else {
//				System.out.println("rejected peer name " + peerid);
			}
		}

		if (peers.size() != peerNb) {
			System.out.println("WARN: inconsistent peer nb");
		}
		
		HelloMessage hm = new HelloMessage(peer, sequenceNb, interval, peers);
//		System.out.println(s);
//		System.out.println(hm.toString());
		return hm;
	}
	/**
	 * The peer this message is from When we send the hello message, it's us
	 * (main.ID)
	 */
	public final String senderId;
	/**
	 * Version of the database
	 */
	public final int sequenceNumber;
	/**
	 * interval between messages, in s
	 */
	public final int helloInterval;
	/**
	 * Number of known peers Extracted from the PeerTable when we send
	 */
	public int numPeers;
	/**
	 * List of peers known by the sender
	 */
	public final List<String> peers;


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
		numPeers = peers.size();
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
		String header = String.format("The sender is %s\n"
				+ "SeqNb = %d\n"
				+ "HelloInterval = %d\n",
				senderId, sequenceNumber, helloInterval);
		StringBuilder builder = new StringBuilder(header);

		if (numPeers != peers.size()) {
			System.out.println("numPeers isn't equal to peers.size().");
		}

		if (numPeers > 0) {
			builder.append("The ").append(numPeers).append(" peers are :\n");
			builder.append(peers.get(0));
			for (int i = 1; i < peers.size(); i++) {
				builder.append(", ").append(peers.get(i));
			}
			builder.append(".\n");
		} else {
			builder.append("There are no peers\n");
		}
		return builder.toString();
	}

	@Override
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
