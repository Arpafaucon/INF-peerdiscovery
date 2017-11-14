/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package debug;

import peertable.PeerTable;

/**
 *
 * @author arpaf
 */
public class DebugPeerTableReader implements DebugStateMessage{

	peertable.PeerTable table;

	public DebugPeerTableReader(PeerTable table) {
		this.table = table;
	}
	
	@Override
	public String readState() {
		String res = table.toString();
		System.out.println("DPTR: " + res);
		return res;
	}
	
}
