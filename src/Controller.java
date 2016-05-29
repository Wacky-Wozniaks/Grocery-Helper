/**
 * The main class for the project.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/28/2016
 */

import java.io.FileNotFoundException;
import java.io.IOException;

public class Controller
{
	/**
	 * Runs the program.
	 * 
	 * @throws FileNotFoundException If there is a problem in the import/export
	 */
	public static void main(String[] args) throws IOException
	{
		Inventories.importInventories();
		Operation.setEnabled(true); //Operations will now be added to the undo stack
		new GUI();
		/*
		 * Sets so that whenever the user quits the program the following code is run.
		 * This code exports all the inventories then the list of inventories so that they
		 * can be imported for the next use of the program.
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				for(Inventory i: Inventories.getList()) {
					try {
						i.exportInventory();
					} catch (IOException e) {}
				}
				try {
					Inventories.exportInventories();
				} catch (IOException e) {}
			}
		});
	}
	
	
}