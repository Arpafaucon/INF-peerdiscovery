package handlers;

import main.MuxDemuxSimple;



public interface SimpleMessageHandler {
	public void handleMessage(String m);
	
	public void setMuxDemux(MuxDemuxSimple md);
}
