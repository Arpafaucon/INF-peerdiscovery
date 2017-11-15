/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package debug;

import main.Database;


/**
 *
 * @author arpaf
 */
public class DebugDatabaseReader implements DebugStateMessage{

	private Database database;

	/**
	 * Creating a initialized database
	 * @param database 
	 */
	public DebugDatabaseReader(Database database) {
		this.database = database;
	}
	
	@Override
	public String readState() {
		return String.format("== Internal ==%n%s%n"
				+ "== External ==%n%s%n", 
				Database.getInternalDatabase().toString(), 
				Database.getPeerDatabases().toString());
	}
	
}
