/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 * This Exception signals an error during startup. The program can't function
 * properly
 *
 */
public class SetupException extends Exception {

	public SetupException() {
	}

	public SetupException(String message) {
		super(message);
	}

	public SetupException(String message, Throwable cause) {
		super(message, cause);
	}

	public SetupException(Throwable cause) {
		super(cause);
	}

	public SetupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
