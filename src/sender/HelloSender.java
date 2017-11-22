package sender;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import message.HelloMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import database.Database;
import main.Main;
import main.MuxDemuxSimple;

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
		while (!Thread.interrupted()) {
			m = new HelloMessage(Main.ID, Database.getInternalDatabase().getSequenceNumber(), Main.SEND_HELLO_INTERVAL);
			muxDemuxSimple.send(m.toEncodedString());
			try {
				//for question 2-3
				Thread.sleep(Main.SEND_HELLO_INTERVAL * 1000);

			} catch (InterruptedException ex) {
			}
//			logger.log(Level.FINE, "sent hello : {0}", m.toEncodedString());
		}
	}

}
