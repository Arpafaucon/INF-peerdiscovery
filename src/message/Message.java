/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

/**
 *
 * @author arpaf
 */
public interface Message {
	/**
	 * is the message for me ?
	 * @return an indication on whether to handle the message
	 */
	public boolean isForMe();
	
	public String toEncodedString();
	
	public String getSenderId();
	
}