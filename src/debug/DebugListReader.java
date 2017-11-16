/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package debug;

import handlers.ListHandler;

/**
 *
 * @author arpaf
 */
public class DebugListReader implements DebugStateMessage{
	ListHandler lh;

	public DebugListReader(ListHandler lh) {
		this.lh = lh;
	}

	
	@Override
	public String readState() {
		return lh.toString();
	}
	
}
