/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peertable;

/**
 *
 * @author arpaf
 */
public class PeerException extends Exception{

	public PeerException() {
	}

	public PeerException(String message) {
		super(message);
	}

	public PeerException(String message, Throwable cause) {
		super(message, cause);
	}

	public PeerException(Throwable cause) {
		super(cause);
	}

	public PeerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
