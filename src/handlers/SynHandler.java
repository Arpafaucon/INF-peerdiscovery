/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlers;

import database.Database;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.Main;
import main.MessagePacket;
import message.ListMessage;
import message.MessageException;
import message.SynMessage;

/**
 * Syn handler
 *
 * This threads listens to incoming messages waiting for SYN If one arrives, it
 * first checks if any trabsmission is pending if so, and if the seq number is
 * not higher, the new one is ignored<br>
 * else, it transmits the whole database as LIST messages
 *
 * @author arpaf
 */
public class SynHandler extends ThreadedMessageHandler {

	/**
	 * tracks sender status towards given peers for the moment, a very naive
	 * implementation responds to all requests if they are legitimate (no checks
	 * of previous ones);
	 */
	private final Map<String, Integer> sequenceNb = new HashMap<>();
	
	public SynHandler() {
		setName("Syn Handler");
	}
	
	@Override
	protected void processMessage(MessagePacket msp) {
		try {
			SynMessage sm = SynMessage.parse(msp.msg);
			//legitimacy checks
			if (shouldAnswer(sm)) {
				//answering
//				System.out.println("\t[VSYN] " + sm.getSenderId());
				List<String> data = Database.getInternalDatabase().getSplitData();
				for (int i = 0; i < data.size(); i++) {
					ListMessage lm = new ListMessage(Main.ID, sm.getSenderId(),
							Database.getInternalDatabase().getSequenceNumber(),
							data.size(), i, data.get(i));
					getMuxDemux().send(lm.toEncodedString());
//					Logger.getLogger(SynHandler.class).log(Level.INFO, "list sent to " + sm.getSenderId());
				}
			} else if (sm.isForMe()) {
				//Wrong databse requested...
				//maybe a little hello to tell the wold we've updated our databases
			}
			
		} catch (MessageException ex) {
			//not a SYN message - silently dropping
		}
	}
	
	static boolean shouldAnswer(SynMessage sm) {
		return (sm.isForMe()
				&& sm.getSequenceNb() == Database.getInternalDatabase().getSequenceNumber());
	}
	
	@Override
	public String toString() {
		return "SynHandler{" + "sequenceNb=\n" + sequenceNb + '}';
	}
	
}
