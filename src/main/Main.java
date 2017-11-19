package main;

import database.Database;
import database.DatabaseUpdater;
import debug.DebugDatabaseReader;
import debug.DebugListReader;
import debug.DebugPeerTableReader;
import debug.DebugServer;
import debug.DebugStateMessage;
import handlers.DebugReceiver;
import handlers.HelloHandler;
import handlers.ListHandler;
import handlers.SimpleMessageHandler;
import handlers.SynHandler;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import peertable.PeerTable;
import sender.HelloSender;

public class Main {

	public static final String ID = "dexter";
	public static final int SEND_HELLO_INTERVAL = 40;
	public static final int CHUNK_SIZE = 255;
	public static final int SOCKET_PORT = 4242;
	public static final int SYN_UPDATE = 4;
	
	public static final int HANDLER_CAPACITY = 20;
	
	public static final int DEBUG_PORT = 4243;
	public static final boolean DEBUG_PRINT_RAW = true;
	public static final boolean DEBUG_PRINT_DROPPED = false;

	private static final Level LOG_LEVEL = Level.FINE;
	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		// -- Configuring log --
		initLogs();

		try {
			PeerTable peerTable = PeerTable.getTable();

			DatagramSocket socket = new DatagramSocket(SOCKET_PORT);
			
			Database.getInternalDatabase().setData("This is the initialisation database fom dexter !");

//			SimpleMessageHandler[] handlers = new SimpleMessageHandler[3];
			List<SimpleMessageHandler> handlers = new ArrayList<>();
			HelloHandler helloReceiver = new HelloHandler(peerTable);
			handlers.add(helloReceiver);
			helloReceiver.start();
			DebugReceiver debugReceiver = new DebugReceiver();
			handlers.add(debugReceiver);
			debugReceiver.start();
			SynHandler synHandler = new SynHandler();
			handlers.add(synHandler);
			synHandler.start();
			ListHandler listHandler = new ListHandler();
			handlers.add(listHandler);
			listHandler.start();
			

			//handlers[2] = new LSAHandler();
			MuxDemuxSimple muxDemuxSimple = new MuxDemuxSimple(handlers, socket);
//			new Thread(handlers[1]).start();
			new Thread(muxDemuxSimple).start();
			HelloSender hs = new HelloSender(muxDemuxSimple);
			new Thread(hs).start();
			
			DatabaseUpdater databaseUpdater = new DatabaseUpdater();
			databaseUpdater.start();

			//--DEBUG--
			final Map<String, DebugStateMessage> intents = new HashMap<>();
			intents.put("peer", new DebugPeerTableReader(peerTable));
			intents.put("data", new DebugDatabaseReader(Database.getInternalDatabase()));
			intents.put("list", new DebugListReader(listHandler));
			DebugServer.threadedServer(DEBUG_PORT, intents);

		} catch (SocketException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static void initLogs() {
//		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
//		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
		System.setProperty("java.util.logging.SimpleFormatter.format", 
				"%1$tH:%1$tM:%1$tS %2$s%n%4$s: %5$s%n");
		
		System.setProperty("java.util.logging.ConsoleHandler.level","FINER");
		Logger rootLog = Logger.getLogger("");
		rootLog.setLevel(LOG_LEVEL);
		rootLog.getHandlers()[0].setLevel(LOG_LEVEL); // Default console handler
		rootLog.info("log attemps");
	}

}
