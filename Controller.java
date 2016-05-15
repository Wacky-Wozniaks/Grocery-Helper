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
import java.io.UnsupportedEncodingException;
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
	
	/**
	 * Runs the program.
	 * @throws FileNotFoundException If there is a problem in the import/export
	 */
	public static void main(String[] args) throws FileNotFoundException
	{
		ArrayList<String> inventoryNames = importInventories();
		
		
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
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					preferences.export();
				} catch (FileNotFoundException | UnsupportedEncodingException e) {}
			}
		});
	}
	
	/**
	 * Imports the list of inventory names from last use of program
	 * 
	 * @return The list of inventory names
	 * @throws FileNotFoundException If there is a problem locating the File
	 */
	private static ArrayList<String> importInventories() throws FileNotFoundException {
		ArrayList<String> list = new ArrayList<String>();
		File file = new File(INVENTORY_FILE_LOC);
		
		if(!file.exists()) return list;
		
		Scanner scan = new Scanner(file);
		while(scan.hasNextLine()) 
			list.add(scan.nextLine());
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