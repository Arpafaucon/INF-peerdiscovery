package main;

import handlers.SimpleMessageHandler;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MuxDemuxSimple implements Runnable {

	private static final Charset CONVERTER = StandardCharsets.UTF_8;
	private static InetAddress BROADCAST;

	private final DatagramSocket mySocket;
	private BufferedReader in;
	private final List<SimpleMessageHandler> myMessageHandlers;
	private final ArrayBlockingQueue<String> outgoing = new ArrayBlockingQueue<>(10);

	private Runnable senderDaemon = new Runnable() {
		@Override
		public void run() {
			while (!Thread.interrupted()) {
				try {
					String mes = outgoing.take();
					byte[] msgToSend = mes.getBytes();
					DatagramPacket p = new DatagramPacket(msgToSend, 0, msgToSend.length, BROADCAST, main.Main.SOCKET_PORT);
					mySocket.send(p);
					System.out.println("\t[SEND] " + mes);
				} catch (InterruptedException | IOException ex) {
					Logger.getLogger(MuxDemuxSimple.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	};
//	private SynchronousQueue<String> ouCtgoing = new SynchronousQueue<String>();

	public MuxDemuxSimple(List<SimpleMessageHandler> handlers, DatagramSocket s) {
		mySocket = s;
		try {
			mySocket.setBroadcast(true);
			BROADCAST = InetAddress.getByName("255.255.255.255");
		} catch (SocketException | UnknownHostException ex) {
			Logger.getLogger(MuxDemuxSimple.class.getName()).log(Level.SEVERE, null, ex);
		}
		myMessageHandlers = handlers;
	}

	@Override
	public void run() {
		byte[] buf = new byte[8192];
		//starting receive daemon
		Thread senThread = new Thread(senderDaemon);
		senThread.start();
		//linking all handlers to this muxDemux
		myMessageHandlers.stream().forEach((myMessageHandler) -> {
			myMessageHandler.setMuxDemux(this);
		});
		//waiting for & handling incoming messages
		while (!Thread.interrupted()) {
			try {
				// -- RECEIVING MESSAGE --
				DatagramPacket messageRecu = new DatagramPacket(buf, 8192);
				mySocket.receive(messageRecu);
				String message = CONVERTER.decode(
						ByteBuffer.wrap(buf, 0, messageRecu.getLength()))
						.toString();
				MessagePacket p = new MessagePacket(message,
						System.currentTimeMillis(),
						messageRecu.getAddress());
				myMessageHandlers.stream().forEach((myMessageHandler) -> {
					myMessageHandler.handleMessage(p);
				});
			} catch (IOException ex) {
				Logger.getLogger(MuxDemuxSimple.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		Logger.getLogger(MuxDemuxSimple.class.getName()).log(Level.INFO, null, "Message receiver interrupted. Stopping...");
		senThread.interrupt();
		mySocket.close();
	}

	public void send(String s) {
		outgoing.add(s);
	}

}
