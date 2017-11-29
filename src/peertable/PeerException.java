package peertable;

/**
 * Raised for issues involving a peer
 * More usual : peer not found
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
