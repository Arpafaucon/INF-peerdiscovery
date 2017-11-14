package message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author arpaf
 */
public class SynMessage {

	/**
	 * format : SYN;senderID;peerID;sequence#;...
	 */
	private static final Pattern SYN_PATTERN = Pattern.compile("SYN;(\\w+);(\\w+);(\\d+);(.*)");

	/**
	 * a string of up to 16 characters, containing the characters A-Z a-z 0-9
	 * (only), and which represents the symbolic name (as in: JuansFileServer)
	 * of the sending machine
	 */
	private final String senderId;
	/**
	 * senderID of the peer, to which this message is addressed
	 */
	private final String peerId;
	/**
	 * sequence# received in a HELLO message generated from the machine
	 * identified by peerID
	 */
	private final int sequenceNb;
	/**
	 * ignored for now
	 */
	private String trailing;

	public SynMessage(String senderId, String peerId, int sequenceNb) {
		this.senderId = senderId;
		this.peerId = peerId;
		this.sequenceNb = sequenceNb;
	}

	public static SynMessage parse(String message) throws MessageException {
		Matcher synMatcher = SYN_PATTERN.matcher(message);
		if (synMatcher.find()) {
			//valid correspondance
			try {

				String senderId = synMatcher.group(1);
				String peerId = synMatcher.group(2);
				int seqNb = Integer.parseInt(synMatcher.group(3));
				String trailing = synMatcher.group(4);
				//We can there do further checks
				SynMessage syn = new SynMessage(senderId, peerId, seqNb);
				return syn;
			} catch (IndexOutOfBoundsException ex) {
				throw new MessageException("SYN: invalid format", ex);
			}
		} else {
			//invalid
			throw new MessageException("SYN: invalid format");
		}
	}

	public String toEncodedString() {
		return String.format("SYN;%s;%s;%d;", senderId, peerId, sequenceNb);
	}

	@Override
	public String toString() {
		return "SynMessage{" + "senderId=" + senderId + ", peerId=" + peerId 
				+ ", sequenceNb=" + sequenceNb + ", trailing=" + trailing + '}';
	}
	
	

}
