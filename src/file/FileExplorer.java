/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import main.Main;

/**
 * File Crawler
 * the FileExplorer crawls the MY_FOLDER folder and updates the database
 * @author arpaf
 */
public class FileExplorer extends Thread {

	private String previousFileString = "";

	@Override
	public void run() {
		while (!isInterrupted()) {
			updateFileBase();
			try {
				sleep(Main.FILE_UPDATE_PERIOD);
			} catch (InterruptedException ex) {
			}
		}
	}

	public FileExplorer() {
		setName("File Explorer");
	}

	
	/**
	 * Update file base Scans the files in the DIRECTORY folder and list them
	 * into the internal database
	 */
	private void updateFileBase() {
//		System.out.println("/***********\n"
//				+ "UPDATING file list\n"
//				+ "************/");
//		File folder = new File(Main.DIRECTORY);
		File[] listOfFiles = Main.F_FOLDER.listFiles();
		List<String> fileNames = new ArrayList<>();
		StringBuilder b = new StringBuilder();
		for (File listOfFile : listOfFiles) {
			if (listOfFile.isFile()) {
				String name = listOfFile.getName();
//				System.out.println("File " + name);
				fileNames.add(name);
				b.append(name).append("\n");
			} else if (listOfFile.isDirectory()) {
//				System.out.println("Directory " + listOfFile.getName());
			}
		}
		String fileString = b.toString();
		if (!b.toString().equals(previousFileString)) {
			//file set has changed
			//we update our database
			database.Database.getInternalDatabase().setData(b.toString());
			database.Database.getInternalDatabase().incrSeqNb();
			previousFileString = fileString;
		}

	}

	/**
	 * List all files in the DIRECTORY Debug helper : return a intent usable for
	 * Debug Server
	 *
	 * @return
	 */
	public static debug.DebuggableComponent getTreeViewer() {
		return () -> {
			try {
				String res = Files.find(Paths.get(Main.DIRECTORY),
						Integer.MAX_VALUE,
						(filePath, fileAttr) -> {
							return true;
						})
						.map((path) -> path.toFile())
						.map((file) -> {
							if (file.isDirectory()) {
								return "DIR : " + file.getName() + "\n";
							} else {
								return file.getName() + "\n";
							}
						})
						.reduce("Listed in BFS: \n=========\n\n", String::concat);
				return res;
			} catch (IOException ex) {
				return "error";
			}

		};
	}

}
