
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Utility message handler who prints raw network messages
 * @author arpaf
 */
public class DebugReceiver implements SimpleMessageHandler{
	MuxDemuxSimple mds;
	
	@Override
	public void handleMessage(String m) {
		System.out.println("[RAW]" + m);
		try {
			HelloMessage hm = new HelloMessage(m);
			System.out.println("[HELLO]" + hm.toString());
		} catch (HelloMessage.HelloException ex) {
			
		}
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		mds = md;
	}
	
}
