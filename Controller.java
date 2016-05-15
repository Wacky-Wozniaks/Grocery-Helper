/**
 * The main class for the project.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/14/2016
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Controller
{
	
	private static final String INVENTORY_FILE_LOC = "inventories.ilist";
	private static ArrayList<Inventory> inventories;
	
	/**
	 * Runs the program.
	 * 
	 * @throws FileNotFoundException If there is a problem in the import/export
	 */
	public static void main(String[] args) throws IOException
	{
		inventories = importInventories();
		
		try
		{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch(Throwable e){}
		Item i = new Item("Apple", 5, 10, 7);
		JPanel panel = InventoryGUI.createItemPanel(i);
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		/*
		 * Sets so that whenever the user quits the program the following code is run.
		 * This code exports all the inventories then the list of inventories so that they
		 * can be imported for the next use of the program.
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				for(Inventory i: inventories) {
					try {
						i.exportInventory();
					} catch (IOException e) {}
				}
				try {
					exportInventories(inventories);
				} catch (IOException e) {}
			}
		});
	}
	
	/**
	 * Imports the list of inventory names from last use of program
	 * 
	 * @return The list of inventory names
	 * @throws FileNotFoundException If there is a problem locating the File
	 */
	private static ArrayList<Inventory> importInventories() throws FileNotFoundException {
		ArrayList<Inventory> list = new ArrayList<Inventory>();
		File file = new File(INVENTORY_FILE_LOC);
		
		if(!file.exists()) return list;
		
		Scanner scan = new Scanner(file);
		while(scan.hasNextLine()) {
			Inventory temp = new Inventory(scan.nextLine());
			temp.importInventory();
			list.add(temp);
		}
		scan.close();
		return list;
	}
	
	/**
	 * Exports the names of the inventories to a file for later use
	 * 
	 * @param list The list of inventories.
	 * @throws IOException If there is a problem in export
	 */
	private static void exportInventories(List<Inventory> list) throws IOException {
		File file = new File(INVENTORY_FILE_LOC);
		
		if(!file.exists()) file.createNewFile();
		PrintWriter writer = new PrintWriter(file);
		for(Inventory i: list) writer.println(i.getName());
		writer.close();
	}
	
}