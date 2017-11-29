/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.Main;


/**
 * basic file server over TCP
 * Answers to requests of format
 * <code>get filename</code>
 * @author arpaf
 */
public class FileServer {

	static final boolean PRINT_DEBUG = !true;
	static final boolean PRINT_ERROR = true;
	
	private static final Charset UTF8 = StandardCharsets.UTF_8;

	static final int POOL_SIZE = 10;

	static final int ERROR_INVALID_ARGS = 1;

	/**
	 * Inner Handler class. Does all the dirty work. Entrance point is
	 * handleConnection
	 */
	private static class ConnectionHandler {

//		private static final int STATUS_OK = 200;
//		private static final int STATUS_BAD_REQUEST = 400;
//		private static final int STATUS_NOT_FOUND = 404;
//		private static final int STATUS_INTERNAL_ERROR = 500;
		
		private static final Pattern GET_PATTERN = Pattern.compile("get ([./\\w]+)");

//		private static final String HEADLINE_HEADER = "^GET (/[\\d\\w.-_/]*) HTTP/1.1$";
//		private static final Pattern HEADLINE_PATTERN = Pattern.compile(HEADLINE_HEADER);
//		private static final String HOST_HEADER = "^Host *: *(.*)$";
//		private static final Pattern HOST_PATTERN = Pattern.compile(HOST_HEADER, Pattern.MULTILINE);
//		private static final String VALID_PATH = "/([\\w.-_]*)";
//		private static final Pattern VALID_PATH_PATTERN = Pattern.compile(VALID_PATH);

		/**
		 * Main method. Handles a client connection from end to end.
		 *
		 * @param s client Socket
		 */
		static void handleConnection(Socket s) {
			try {
				print("---- handling new connection ----");
				BufferedReader stream = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String line = stream.readLine();
				final Matcher headlineMatcher = GET_PATTERN.matcher(line);
				print(line);
				if (!headlineMatcher.find()) {
					error("invalid request - headline invalid");
//					writeMessage(s, STATUS_BAD_REQUEST, "Bad Request", "Invalid Request Headline", true);
					return;
				}

				final String path = headlineMatcher.group(1);
//				int version = Integer.parseInt(headlineMatcher.group(2));
//				print("valid request with " + path);

				File requestedFile = new File(Main.F_FOLDER, path);
				if(!requestedFile.exists()){
					error("invalid request - file does not exist : " + path);
					return;
				}
				
				print("<< SND_FILE >>");
				print(path + " -> " + s.getRemoteSocketAddress().toString());
				writeMessage(s, requestedFile.getAbsolutePath(), requestedFile.getName());


			} catch (IOException ex) {

			} catch (NullPointerException ex) {
				error("NullPointer: " + ex.getMessage());
			} finally {
				closeSocket(s);
			}
		}

		/**
		 * writes a message to Socket
		 *
		 * @param s the Socket
		 * @param status status number xxx
		 * @param headline short phrase describing the status (optionnal)
		 * @param mes payload
		 * @param isClosing should add <code>Connection: close</code>
		 */
		private static void writeMessage(Socket s, String file, String fileName) {
			try (OutputStream os = s.getOutputStream()) {
				String fileData = readFile(file, UTF8);
				String message = String.format("%s%n"
						+ "%d%n"
						+ "%s", fileName, fileData.getBytes().length, fileData);
				os.write(message.getBytes());
			} catch (IOException ex) {
				error("Couldn't write message");
			}
		}

		/**
		 * Clean closing of the socket
		 *
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
	 * @param port port to listen to
	 */
	public static void threadedServer(int port) {
		final AtomicInteger workerCount = new AtomicInteger(0);
//		int workersCount = 0;
		boolean workerAvailable;
		try {
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Threaded File server started on port " + port);
			boolean done = false;
//			ConnectionHandler handler = new ConnectionHandler();
			while (!done && !Thread.interrupted()) {
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
			error("File Server exited due to I/O error");
		}
	}

	/**
	 * simple log output
	 *
	 * @param s
	 */
	private static void print(String s) {
		if (PRINT_DEBUG) {
			System.out.println(s);
		}
	}

	/**
	 * simple error output
	 *
	 * @param s
	 */
	private static void error(String s) {
		if (PRINT_ERROR) {
			System.err.println("[ERR]" + s);
		}
	}

	private static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	/**
	 * Disabled constructor : this class should be used as static
	 */
	private FileServer(){};

}
