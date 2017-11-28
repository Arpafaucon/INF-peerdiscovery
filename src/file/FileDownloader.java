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
import main.MessagePacket;
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
	
	public FileDownloader getFileDownloader(){
		if(fileDownloader == null){
			fileDownloader = new FileDownloader();
		}
		return fileDownloader;
	}

	@Override
	public void run() {

	}
	
	
	private FileDownloader(){}

	private void downloadPeerFiles(String peerName) {
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
				}
			}

		} catch (PeerException ex) {
			Logger.getLogger(FileDownloader.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void downloadFile(InetAddress peerAddress, File PeerDir, String fileName) throws IOException {
		socket.connect(new InetSocketAddress(peerAddress, Main.SOCKET_PORT));
		//--REQUEST
		try ( 
				OutputStream os = socket.getOutputStream()) {
			String message = String.format("Get %s\n", fileName);
			os.write(message.getBytes(StandardCharsets.UTF_8));
		}

		File file = new File(PeerDir, fileName);
		file.mkdirs();
		
		//--RESPONSE
		try (PrintWriter writer = new PrintWriter(file)) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			String line;
			//FILE NAME
			line = reader.readLine();
			System.out.println("DWN name " + line);
			
			//FILE LENGTH
			line = reader.readLine();
			System.out.println("DWN length" + line);
			int contentSize = Integer.MAX_VALUE;
			
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
		}

	}

	private static List<String> splitFileBase(String fileBase) {
		return Arrays.asList(fileBase.split("\n"));
	}

}
