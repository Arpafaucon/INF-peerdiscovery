/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertable;

import java.util.List;

public class PeerTableUpdater extends Thread {

	private final List<PeerRecord> table;
	private final int PERIOD;
	private boolean done = false;


	public PeerTableUpdater(List<PeerRecord> table, int PERIOD) {
		this.table = table;
		this.PERIOD = PERIOD;
		setName("PeerTable Updater");
	}

	@Override
	public void run() {
		while (!done) {
			if (this.isInterrupted()) {
				done = true;
				break;
			} else {
				try {
					Thread.sleep(PERIOD);
				} catch (InterruptedException ex) {
				}
				//removing expired entry
				
			}

		}
	}

}
