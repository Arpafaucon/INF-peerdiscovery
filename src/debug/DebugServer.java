package debug;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple HTTP/1.1 server.
 * @author gregoire.roussel
 */
public class DebugServer {

	static final boolean PRINT_DEBUG = !true;
	static final boolean PRINT_ERROR = true;

	static final int POOL_SIZE = 10;
	
	static final int ERROR_INVALID_ARGS = 1;
	
	private static Map<String, DebugStateMessage> intents;

	/**
	 * Inner Handler class.
	 * Does all the dirty work. Entrance point is handleConnection
	 */
	private static class ConnectionHandler {

		static final int STATUS_OK = 200;
		static final int STATUS_BAD_REQUEST = 400;
		static final int STATUS_NOT_FOUND = 404;
		static final int STATUS_INTERNAL_ERROR = 500;

		static final String HEADLINE_HEADER = "^GET (/[\\d\\w.-_/]*) HTTP/1.1$";
		static final Pattern HEADLINE_PATTERN = Pattern.compile(HEADLINE_HEADER);
		static final String HOST_HEADER = "^Host *: *(.*)$";
		static final Pattern HOST_PATTERN = Pattern.compile(HOST_HEADER, Pattern.MULTILINE);
		static final String VALID_PATH = "/([\\w.-_]*)";
		static final Pattern VALID_PATH_PATTERN = Pattern.compile(VALID_PATH);

		/**
		 * Main method.
		 * Handles a client connection from end to end.
		 * @param s client Socket
		 */
		static void handleConnection(Socket s) {
			try {
				print("---- handling new connection ----");
				BufferedReader stream = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String line = stream.readLine();
				final Matcher headlineMatcher = HEADLINE_PATTERN.matcher(line);
				print(line);
				if (!headlineMatcher.find()) {
					error("invalid request - HTTP method or version unsupported");
					writeMessage(s, STATUS_BAD_REQUEST, "Bad Request", "Invalid Request Headline", true);
					return;
				}

				final String path = headlineMatcher.group(1);
//				int version = Integer.parseInt(headlineMatcher.group(2));
//				print("valid request with " + path);

				// getting rest of headers
				String headers = "";
				while ((line = stream.readLine()) != null && !line.equals("")) {
					headers += line + "\n";
				}
				//checking if Host header is well formed
				final Matcher hostMatcher = HOST_PATTERN.matcher(headers);
//			print(headers);
				if (!hostMatcher.find()) {
					error("Malformed Host header");
					writeMessage(s, STATUS_BAD_REQUEST, "Invalid Host", "Invalid Host", true);
					return;
				}

				String host = hostMatcher.group(1);
//				print("valid host : " + host);
				//We are here in a "valid" request
				//checking if path is valid
				final Matcher pathvaliMatcher = VALID_PATH_PATTERN.matcher(path);
				if (!pathvaliMatcher.find()) {
					error("Non-acceptable path" + path);
					writeMessage(s, STATUS_NOT_FOUND, "Invalid path", "The file was not found - path is invalid", true);
					return;

				}
				String requestedIntent = pathvaliMatcher.group(1);
				//path is validated
				//does it correspond to a real intent ?
				if(!(intents.containsKey(requestedIntent) || "".equals(requestedIntent))){
					error("intent not found: " + requestedIntent);
					writeMessage(s, STATUS_NOT_FOUND, "Not Found", "The intent was not found", true);
					return;
				}
				
				//it does!
				String intentString = readState(requestedIntent);
				print("sending intent \"" + requestedIntent + "\"");
				writeMessage(s, STATUS_OK, "Found", intentString, true);

			} catch (IOException ex) {

			} catch (NullPointerException ex) {
				error("NullPointer: " + ex.getMessage());
				writeMessage(s, STATUS_INTERNAL_ERROR, "Null Pointer error",
						"An internal error occured : \r\n " + ex.getMessage(), true);
			} finally {
				closeSocket(s);
			}
		}
		
		/**
		 * reads requested state by asking the interface
		 * @param req ID of the interface to ask
		 * @return 
		 */
		static String readState(String req){
			if("".equals(req)){
				//nothing asked: lets print available interface
				return "list of intents:\n" + intents.keySet().toString();
			}
			return intents.get(req).readState();
		}

		/**
		 * writes a message to Socket
		 *
		 * @param s         the Socket
		 * @param status    status number xxx
		 * @param headline  short phrase describing the status (optionnal)
		 * @param mes       payload
		 * @param isClosing should add <code>Connection: close</code>
		 */
		private static void writeMessage(Socket s, int status, String headline, String mes, boolean isClosing) {
			try (OutputStream os = s.getOutputStream()) {
				final String closeHeader = "Connection: close\r\n";
				String message = String.format("HTTP/1.1 %d %s\r\n"
						+ "%s"
						+ "Content-Length: %d\r\n"
						+ "\r\n"
						+ "%s"
						+ "\r\n"
						+ "\r\n", status, headline, isClosing ? closeHeader : "", mes.getBytes().length, mes);
				os.write(message.getBytes());
			} catch (IOException ex) {
				error("Couldn't write message");
			}
		}

		/**
		 * Clean closing of the socket
		 * @param s 
		 */
		private static void closeSocket(Socket s) {
			try {
//			s.shutdownInput();
//			s.shutdownOutput();
				s.close();
				print("Closed connection");
			} catch (IOException ex) {
				error("Cannot close socket" + ex.getMessage());
			}
		}
	}
	/**
	 * Starts a multithreaded server
	 *
	 * @param port     port to listen to
	 * @param states	the intent states to bind
	 */
	public static void threadedServer(int port, Map<String, DebugStateMessage> states) {
		final AtomicInteger workerCount = new AtomicInteger(0);
		intents = states;
//		int workersCount = 0;
		boolean workerAvailable;
		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Threaded Debug server started on port " + port + " with intents:  " + intents.keySet().toString());
			boolean done = false;
//			ConnectionHandler handler = new ConnectionHandler();
			while (!done) {
				workerAvailable = (workerCount.get() < POOL_SIZE);
				if (workerAvailable) {
//					print("worker available");
//					print("workers left : " + (poolSize - workerCount.get()));
					workerCount.incrementAndGet();
					Socket s = ss.accept();
					new Thread(() -> {
						ConnectionHandler.handleConnection(s);
						workerCount.decrementAndGet();
					}).start();
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
					}
				}
			}
		} catch (IOException ex) {
			error("Server exited due to I/O error");
		}
	}

	/**
	 * simple log output
	 *
	 * @param s
	 */
	static void print(String s) {
		if (PRINT_DEBUG) {
			System.out.println(s);
		}
	}

	/**
	 * simple error output
	 *
	 * @param s
	 */
	static void error(String s) {
		if (PRINT_ERROR) {
			System.err.println("[ERR]" + s);
		}
	}

}
