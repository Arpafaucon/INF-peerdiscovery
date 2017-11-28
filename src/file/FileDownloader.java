/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Main;
import peertable.PeerException;

/**
 * This class is the counterpart of FileSender upon request sent by an update of
 * the PeerTable, it will download all files listed in the remote database into
 * the DIRECTORY/peer_name folder
 *
 * Singleton pattern
 */
public class FileDownloader extends Thread {

	private static FileDownloader fileDownloader = null;

	private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(main.Main.HANDLER_CAPACITY);
	private Socket socket;

	public static FileDownloader getFileDownloader() {
		if (fileDownloader == null) {
			fileDownloader = new FileDownloader();
		}
		return fileDownloader;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				String peerName = queue.take();
				downloadPeerFiles(peerName);
			} catch (InterruptedException ex) {
			}
		}
	}

	private FileDownloader() {
		socket = new Socket();
	}

	/**
	 * ensure uniqueness of requests
	 *
	 * @param peerName
	 */
	public void addPeerToQueue(String peerName) {
		if (!queue.contains(peerName)) {
			queue.add(peerName);
		}
	}

	private void downloadPeerFiles(String peerName) {
		System.out.println("Downloading files of " + peerName);
		try {
			String peerBase = database.Database.getPeerBase(peerName).getData();
			InetAddress peerIP = peertable.PeerTable.getTable().getPeerAddress(peerName);
			List<String> files = splitFileBase(peerBase);
			File peerDir = new File(Main.DIRECTORY, peerName);

			for (String file : files) {
				try {
					downloadFile(peerIP, peerDir, file);
				} catch (IOException ex) {
					System.err.println("failed to dwn : " + file + peerName);
					//will retry
					addPeerToQueue(peerName);
					return;
				}
			}

		} catch (PeerException ex) {
			Logger.getLogger(FileDownloader.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void downloadFile(InetAddress peerAddress, File PeerDir, String fileName) throws IOException {
		System.out.println("Down file " + fileName);
		socket.connect(new InetSocketAddress(peerAddress, Main.SOCKET_PORT));
		//--REQUEST
		OutputStream os = socket.getOutputStream();
		String message = String.format("get %s\n", fileName);
		os.write(message.getBytes(StandardCharsets.UTF_8));
		System.out.println("message");

		File fileToDown = new File(PeerDir, fileName);
		fileToDown.getParentFile().mkdirs();

		//--RESPONSE
		PrintWriter writer = new PrintWriter(fileToDown);
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);
		String line;
		//FILE NAME
		line = reader.readLine();
		System.out.println("DWN name " + line);

		//FILE LENGTH
		line = reader.readLine();
		System.out.println("DWN length" + line);
		int contentSize = Integer.parseInt(line);

		//CONTENT
		StringBuilder b = new StringBuilder();
		int size = 0;
		while ((line = reader.readLine()) != null && size < contentSize) {
			System.out.println("DWN " + line);
			size += line.getBytes().length;
			b.append(line);
		}

		reader.close();

		writer.write(b.toString());
		writer.close();

	}

	private static List<String> splitFileBase(String fileBase) {
		return Arrays.asList(fileBase.split("\n"));
	}

}
