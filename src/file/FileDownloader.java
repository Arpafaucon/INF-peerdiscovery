/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.File;
import java.io.IOException;
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
import main.MessagePacket;
import peertable.PeerException;

/**
 * This class is the counterpart of FileSender upon request sent by an update of
 * the PeerTable, it will download all files listed in the remote database into
 * the DIRECTORY/peer_name folder
 *
 */
public class FileDownloader extends Thread {

	private final BlockingQueue<MessagePacket> queue = new ArrayBlockingQueue<>(main.Main.HANDLER_CAPACITY);
	private Socket socket;

	@Override
	public void run() {

	}

	private void downloadPeerFiles(String peerName) {
		try {
			String peerBase = database.Database.getPeerBase(peerName).getData();
			InetAddress peerIP = peertable.PeerTable.getTable().getPeerAddress(peerName);
			List<String> files = splitFileBase(peerBase);

			for (String file : files) {
			}

		} catch (PeerException ex) {
			Logger.getLogger(FileDownloader.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void downloadFile(InetAddress peerAddress, File PeerDir, String fileName) throws IOException {
		socket.connect(new InetSocketAddress(peerAddress, Main.SOCKET_PORT));
		OutputStream os = socket.getOutputStream();
		String message = String.format("Get %s\n", fileName);
		os.write(message.getBytes(StandardCharsets.UTF_8));
		os.close();
		File file = new File(PeerDir, fileName);
		PrintWriter writer = new PrintWriter(file); 
	}
	

	private static List<String> splitFileBase(String fileBase) {
		return Arrays.asList(fileBase.split("\n"));
	}

}
