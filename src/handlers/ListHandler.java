/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlers;

import main.MessagePacket;

/**
 *
 * @author arpaf
 */
public class ListHandler extends ThreadedMessageHandler{

	@Override
	protected void processMessage(MessagePacket msp) {
		String mes = msp.msg;
		System.out.println("mes : " + mes);
		
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
