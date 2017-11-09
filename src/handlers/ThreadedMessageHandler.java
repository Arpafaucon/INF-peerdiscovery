package handlers;


import main.MuxDemuxSimple;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author arpaf
 */
public abstract class ThreadedMessageHandler extends Thread implements SimpleMessageHandler {

	private final static int CAPACITY = 10;
	public MuxDemuxSimple mds;
	private BlockingQueue<String> queue = new ArrayBlockingQueue<>(CAPACITY);

	@Override
	public void handleMessage(String m) {
		queue.add(m);
	}

	@Override
	public void setMuxDemux(MuxDemuxSimple md) {
		mds = md;
	}

	@Override
	public void run() {
		while (true) {
			String m;
			try {
				m = queue.take();
				processMessage(m);
			} catch (InterruptedException ex) {
			} catch (Exception e){
				System.err.println("[ERR] unhndled exception in ThreadedMessageHandler :" + e.getMessage());
			}

		}
	}

	abstract void processMessage(String msg);

}
