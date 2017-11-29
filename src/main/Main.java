package main;

import database.Database;
import debug.DebugServer;
import file.FileServer;
import handlers.DebugReceiver;
import handlers.HelloHandler;
import handlers.ListHandler;
import handlers.SimpleMessageHandler;
import handlers.SynHandler;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import peertable.PeerTable;
import sender.HelloSender;
import sender.SynSender;
import debug.DebuggableComponent;
import file.FileDownloader;
import file.FileExplorer;

public class Main {

	public static String ID = "dexter";
	/**
	 * Shared database folder Directory is the root database folder : it
	 * contains all peer folders
	 */
	public static final String DIRECTORY = "sharedFolder/";
	/**
	 * Personal shared folder it is expected to be a direct child of DIRECTORY
	 * So shared files paths should be like
	 * projectRoot/DIRECTORY/MY_FOLDER/filename
	 */
	public static final String MY_FOLDER = "my_folder/";

	public static final File F_DIRECTORY = new File(DIRECTORY);
	public static final File F_FOLDER = new File(DIRECTORY, MY_FOLDER);

	/**
	 * Hello boradcast period
	 */
	public static final int SEND_HELLO_INTERVAL = 5;
	/**
	 * max size of database chunk sent to the network
	 */
	public static final int CHUNK_SIZE = 255;
	public static final int SOCKET_PORT = 4242;
	public static final int FILE_SERVER_PORT = 4242;
	public static final int SYN_UPDATE = 10;
	public static final int FILE_UPDATE_PERIOD = 10_000;

	public static final int HANDLER_CAPACITY = 20;

	public static final int DEBUG_PORT = 4243;
	public static final boolean DEBUG_PRINT_RAW = true;
	public static final boolean DEBUG_PRINT_DROPPED = false;
	public static final boolean DEBUG_RANDOM_ID = true;

	private static final Level LOG_LEVEL = Level.FINE;
	private static final Logger logger = Logger.getLogger(Main.class.getName());

	private static PeerTable peerTable;

	private static ListHandler listHandler;
	private static DebugReceiver debugReceiver;
	private static HelloHandler helloHandler;
	private static SynHandler synHandler;
	private static HelloSender helloSender;
	private static SynSender synSender;

	private static DatagramSocket socket;

	private static FileExplorer fileExplorer;

	/**
	 * Launch File Server
	 *
	 * Checks if the directory structure is consistent then lauches required
	 * classes
	 *
	 * @throws main.SetupException if folder structure is inconsistent
	 */
	public static void setupFileSync() throws SetupException {
		if (!(F_DIRECTORY.exists() && F_DIRECTORY.isDirectory()
				&& F_FOLDER.exists() && F_FOLDER.isDirectory())) {
			throw new SetupException("FATAL : inconsistent directory structure");
		}
		System.out.println("Check. User.dir" + System.getProperty("user.dir"));
		fileExplorer = new FileExplorer();
		fileExplorer.start();
		FileDownloader.getFileDownloader().start();
		new Thread(() -> {
			FileServer.threadedServer(FILE_SERVER_PORT);
		}, "File Server").start();

	}

	/**
	 * Setup the different Handlers for the application register a custom
	 * shutdown hook to exit (more) gracefully
	 *
	 * @return list of all handlers (to be given to MuxDemux)
	 */
	private static List<SimpleMessageHandler> initIO(PeerTable peerTable, DatagramSocket socket) {
		List<SimpleMessageHandler> handlers = new ArrayList<>();
		//INITIALISATION OF ALL HANDLERS
		helloHandler = new HelloHandler(peerTable);
		handlers.add(helloHandler);
		helloHandler.start();

		debugReceiver = new DebugReceiver();
		handlers.add(debugReceiver);
		debugReceiver.start();

		synHandler = new SynHandler();
		handlers.add(synHandler);
		synHandler.start();

		listHandler = new ListHandler();
		handlers.add(listHandler);
		listHandler.start();

		//IO GATEWAY
		MuxDemuxSimple muxDemuxSimple = new MuxDemuxSimple(handlers, socket);
		new Thread(muxDemuxSimple, "MuxDemux").start();

		//SENDERS
		helloSender = new HelloSender(muxDemuxSimple);
		new Thread(helloSender, "Hello Sender").start();

		synSender = new SynSender(muxDemuxSimple, listHandler);
		synSender.start();

		//Setup a hook to
		//try to close gracefully
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				debugReceiver.interrupt();
				synHandler.interrupt();
				listHandler.interrupt();
				synSender.interrupt();
			}
		});

		return handlers;
	}

	/**
	 * if activated : randomize ID to allow multiple clients on same computer
	 */
	private static void initID() {
		if (DEBUG_RANDOM_ID) {
			Random arnd = new Random();
			ID = ID + arnd.nextInt(1000);
		}
		System.out.println("ID : " + ID);
	}

	/**
	 * main routine
	 * @param args unused
	 */
	public static void main(String[] args) {
		try {
			// -- Configuring log --

			initLogs();

			initID();

			peerTable = PeerTable.getTable();

			Database.getInternalDatabase().setData("");
//			DatabaseUpdater databaseUpdater = new DatabaseUpdater();
//			databaseUpdater.start();

			initSocket();

			List<SimpleMessageHandler> handlers = initIO(peerTable, socket);

			//FILE SERVER
			setupFileSync();

			//DEBUG
			// starts a simple HTTP server that calls readState() on given class
			// this allows us to get the real-time state of components
			final Map<String, DebuggableComponent> intents = new HashMap<>();
			intents.put("peer", peerTable);
			intents.put("data", Database.getDebuggableComponent());
			intents.put("list", listHandler);
			intents.put("file", FileExplorer.getTreeViewer());

			new Thread(() -> {
				DebugServer.threadedServer(DEBUG_PORT, intents);
			}, "DebugServer").start();

		} catch (SetupException ex) {
			System.err.println(ex.getMessage() + "\nExiting.");
		}
	}

	private static void initLogs() {
//		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
//		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%1$tH:%1$tM:%1$tS %2$s%n%4$s: %5$s%n");

		System.setProperty("java.util.logging.ConsoleHandler.level", "FINER");
		Logger rootLog = Logger.getLogger("");
		rootLog.setLevel(LOG_LEVEL);
		rootLog.getHandlers()[0].setLevel(LOG_LEVEL); // Default console handler
		rootLog.info("log attemps");
	}

	/**
	 * Setup the UDP Socket used for the discovery protocol Using a mutlicast
	 * socket allows the program to "share" the port so I can test it against
	 * itself
	 *
	 * @return true if all went well
	 */
	private static void initSocket() throws SetupException {
		MulticastSocket lsocket;
		try {
			lsocket = new MulticastSocket(SOCKET_PORT);
			InetAddress group = InetAddress.getByName("224.0.0.3");
			lsocket.joinGroup(group);
			socket = lsocket;
		} catch (IOException ex) {
			throw new SetupException("FATAL : impossible to allocate socket.", ex);
		}
	}

}
