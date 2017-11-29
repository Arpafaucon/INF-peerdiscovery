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
		setName("File Downloader");
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
				} catch (IOException | NumberFormatException ex) {
					System.err.println("failed to dwn : " + file + peerName);
					System.err.println(ex.getMessage());
					//will retry
					addPeerToQueue(peerName);
					return;
				}
			}
			System.err.println("Download success!");

			peertable.PeerTable.getTable().updateOfflineVersion(peerName);

		} catch (PeerException ex) {
			System.err.println("Tried to reach a non-existent peer" + ex.getMessage());
		}
	}

	private void downloadFile(InetAddress peerAddress, File PeerDir, String fileName) throws IOException {
		Socket socket = new Socket(peerAddress, Main.SOCKET_PORT);
		try {
			PrintWriter writer;
			InputStream is;
			InputStreamReader isr;
			BufferedReader reader;
			OutputStream os;

			System.out.println("Down file " + fileName);
			//--REQUEST
			os = socket.getOutputStream();
			String message = String.format("get %s\n", fileName);
			os.write(message.getBytes(StandardCharsets.UTF_8));
			System.out.println("message");

			File fileToDown = new File(PeerDir, fileName);
			fileToDown.getParentFile().mkdirs();

			//--RESPONSE
			writer = new PrintWriter(fileToDown);
			is = socket.getInputStream();
			isr = new InputStreamReader(is);
			reader = new BufferedReader(isr);
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

			is.close();
			os.close();
			isr.close();

			writer.write(b.toString());
			writer.close();
		} finally {
			socket.close();
		}
	}

	private static List<String> splitFileBase(String fileBase) {
		return Arrays.asList(fileBase.split("\n"));
	}

}
