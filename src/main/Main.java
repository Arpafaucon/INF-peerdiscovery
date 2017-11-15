package main;

import debug.DebugDatabaseReader;
import debug.DebugPeerTableReader;
import debug.DebugServer;
import debug.DebugStateMessage;
import handlers.DebugReceiver;
import handlers.HelloReceiver;
import handlers.SimpleMessageHandler;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import peertable.PeerTable;

public class Main {

	public static final String ID = "dexter";
	public static final int SEND_HELLO_INTERVAL = 2;
	public static final int CHUNK_SIZE = 255;
	public static final int SOCKET_PORT = 4242;
	public static final int DEBUG_PORT = 4243;

	private static final Level LOG_LEVEL = Level.FINE;
	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		// -- Configuring log --
		initLogs();

		try {
			PeerTable peerTable = PeerTable.getTable();

			DatagramSocket socket = new DatagramSocket(SOCKET_PORT);

//			SimpleMessageHandler[] handlers = new SimpleMessageHandler[3];
			List<SimpleMessageHandler> handlers = new ArrayList<>();
			HelloReceiver helloReceiver = new HelloReceiver(peerTable);
			DebugReceiver debugReceiver = new DebugReceiver();
			handlers.add(helloReceiver);
			handlers.add(debugReceiver);

			//handlers[2] = new LSAHandler();
			MuxDemuxSimple muxDemuxSimple = new MuxDemuxSimple(handlers, socket);
//			new Thread(handlers[1]).start();
			new Thread(muxDemuxSimple).start();
			HelloSender hs = new HelloSender(muxDemuxSimple);
//			new Thread(hs).start();

			//--DEBUG--
			final Map<String, DebugStateMessage> intents = new HashMap<>();
			intents.put("peer", new DebugPeerTableReader(peerTable));
			intents.put("data", new DebugDatabaseReader(Database.getInternalDatabase()));
			DebugServer.threadedServer(DEBUG_PORT, intents);

		} catch (SocketException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static void initLogs() {
		Logger rootLog = Logger.getLogger("");
		rootLog.setLevel(LOG_LEVEL);
		rootLog.getHandlers()[0].setLevel(LOG_LEVEL); // Default console handler
	}

}
