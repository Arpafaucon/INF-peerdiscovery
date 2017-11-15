/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * response to SYN message
 *
 * @see SynMessage
 * @author arpaf
 */
public class ListMessage {

	/**
	 * pattern <br>
	 * <code>LIST;senderID;peerID;sequence#;TotalParts;part#;data;</code>
	 */
	private static final Pattern LIST_PATTERN = Pattern.compile("LIST;(\\w+);(\\w+);(\\d+);(\\d+);(\\d+);(.*)");

	/**
	 * string of up to 16 characters, containing the characters A-Z a-z 0-9
	 * (only), and which represents the symbolic name (as in: JuansFileServer)
	 * of the sending machine.
	 */
	String senderId;
	/**
	 * the senderID of the peer, to which this message is addressed (i.e., is
	 * the peer ID of the machine sending the SYN)
	 */
	String peerId;
	/**
	 * the sequence# of the database of senderID
	 */
	int sequenceNb;
	/**
	 * an integer number which indicates how many LIST messages will be
	 * generated, in order to send the entire database
	 */
	int totalParts;
	/**
	 * indicates which, from among the TotalParts messages this message is.
	 */
	int partNb;
	/**
	 * a text string of max 255 characters, which contains (part of) the
	 * database being synchronised.
	 */
	String data;

	public ListMessage(String senderId, String peerId, int sequenceNb, int totalParts, int partNb, String data) {
		this.senderId = senderId;
		this.peerId = peerId;
		this.sequenceNb = sequenceNb;
		this.totalParts = totalParts;
		this.partNb = partNb;
		this.data = data;
	}

	public static ListMessage parse(String mes) throws MessageException {
		Matcher listMatcher = LIST_PATTERN.matcher(mes);
		if (listMatcher.find()) {
			//valid correspondance
			try {

				String senderId = listMatcher.group(1);
				String peerId = listMatcher.group(2);
				int seqNb = Integer.parseInt(listMatcher.group(3));
				int totalParts = Integer.parseInt(listMatcher.group(4));
				int partNb = Integer.parseInt(listMatcher.group(5));
				String data = listMatcher.group(6);

				//We can there do further checks
				ListMessage list = new ListMessage(senderId, peerId, seqNb, totalParts, partNb, data);
				return list;
			} catch (IndexOutOfBoundsException ex) {
				throw new MessageException("LIST: invalid format", ex);
			}
		} else {
			//invalid
			throw new MessageException("LIST: invalid format");
		}
	}

	public String toEncodedString() {
		return String.format("LIST;%s;%s;%d;%d;%d:%s",
				senderId, peerId, sequenceNb,
				totalParts, partNb, data);
	}

	@Override
	public String toString() {
		return "List{" + "senderId=" + senderId + ", peerId=" + peerId 
				+ ", sequenceNb=" + sequenceNb + ", totalParts=" + totalParts 
				+ ", partNb=" + partNb + ", data=" + data + '}';
	}

}
