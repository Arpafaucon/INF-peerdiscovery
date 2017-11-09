package main;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import hello.HelloMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arpaf
 */
public class HelloSender implements Runnable {

	MuxDemuxSimple muxDemuxSimple;
	HelloMessage m;

	public HelloSender(MuxDemuxSimple muxDemuxSimple) {
		this.muxDemuxSimple = muxDemuxSimple;
	}
	
	
	@Override
	public void run() {
		m = new HelloMessage("dexter", 0, 100);
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
			}
			muxDemuxSimple.send(m.toString());
		}
	}

}
