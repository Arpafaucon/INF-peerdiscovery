package main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import message.HelloMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arpaf
 */
public class HelloSender implements Runnable {

	private static final Logger logger = Logger.getLogger(HelloSender.class.getName());
	MuxDemuxSimple muxDemuxSimple;
	HelloMessage m;

	public HelloSender(MuxDemuxSimple muxDemuxSimple) {
		this.muxDemuxSimple = muxDemuxSimple;
	}

	@Override
	public void run() {
		while (true) {
			try {
				//for question 2-3
				m = new HelloMessage(Main.ID, Database.getInternalDatabase().getSequenceNumber(), Main.SEND_HELLO_INTERVAL);
				Thread.sleep(Main.SEND_HELLO_INTERVAL * 1000);

			} catch (InterruptedException ex) {
			}
			muxDemuxSimple.send(m.toString());
			logger.log(Level.FINE, "sent hello : {0}", m.toString());
		}
	}

}
