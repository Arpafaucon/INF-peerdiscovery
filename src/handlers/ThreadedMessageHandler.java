package handlers;


import main.MuxDemuxSimple;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageHandler;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MessagePacket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author arpaf
 */
public abstract class ThreadedMessageHandler implements SimpleMessageHandler, Runnable {

	private final static int CAPACITY = 10;
	private MuxDemuxSimple mds;
	private BlockingQueue<MessagePacket> queue = new ArrayBlockingQueue<>(CAPACITY);

	@Override
	public void handleMessage(MessagePacket msp) {
		queue.offer(msp);
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		mds = md;
	}

	public MuxDemuxSimple getMuxDemux() {
		return mds;
	}

	@Override
	public void run() {
		while (true) {
			MessagePacket msp;
			try {
				msp = queue.take();
				processMessage(msp);
			} catch (InterruptedException ex) {
			} catch (Exception e){
				System.err.println("[ERR] unhndled exception in ThreadedMessageHandler :" + e.getMessage());
			}

		}
	}

	abstract void processMessage(MessagePacket msp);

}
