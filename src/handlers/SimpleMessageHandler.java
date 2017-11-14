package handlers;

import main.MessagePacket;
import main.MuxDemuxSimple;



public interface SimpleMessageHandler {
	public void handleMessage(MessagePacket msp);
	
	public void setMuxDemux(MuxDemuxSimple md);
}
