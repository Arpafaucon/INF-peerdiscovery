/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;
/**
 *
 * @author arpaf
 */
public class DatabaseUpdater extends Thread{
	
	private static final String TEMPLATE = "this is the database from dexter! the version is ...suspense !"
			+ "No kidding, my database version number is ...";
	private static final int UPDATE_PERIOD = 60;

	@Override
	public void run() {
		while(!Thread.interrupted()){
			try {
				Thread.sleep(UPDATE_PERIOD * 1000);
			} catch (InterruptedException ex) {
			}
			Database data = Database.getInternalDatabase();
			data.setData(TEMPLATE + (data.getSequenceNumber() + 1));
			data.incrSeqNb();
//			System.out.println("updated Databse to version " + data.getSequenceNumber());
		}
	}
	
}
