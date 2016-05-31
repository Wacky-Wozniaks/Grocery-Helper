/**
 * A singleton class containing the ArrayList of all inventories besides the master.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/28/2016
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class Inventories
{
	private static ArrayList<Inventory> inventories;
	private static String inventoriesFileLoc = System.getProperty("user.home");	
	private static final String FILENAME = "inventories.ilist";
	private static Properties props;
	private static String PROPERTY_NAME = "inventories";
	
	/**
	 * Returns the ArrayList of inventories.
	 * 
	 * @return The list of inventories.
	 */
	public static ArrayList<Inventory> getList()
	{
		return inventories;
	}
	
	/**
	 * Adds an inventory to the list.
	 * 
	 * @param i The inventory to be added.
	 */
	public static void addInventory(Inventory i)
	{
		inventories.add(i);
		GUI.addInventory(i);
	}
	
	/**
	 * Removes the inventory from the list.
	 * 
	 * @param i The inventory to remove.
	 */
	public static void removeInventory(Inventory i)
	{
		inventories.remove(i);
		MasterInventory.remove(i);
		GUI.removeInventory(i);
	}
	
	/**
	 * Imports the list of inventory names from last use of program
	 * 
	 * @return The list of inventory names
	 * @throws FileNotFoundException If there is a problem locating the File
	 */
	public static void importInventories() throws FileNotFoundException {
		inventories = new ArrayList<Inventory>();
		updateFilePath();
		
		props = new Properties();
		InputStream in;
		
		inventoriesFileLoc += FILENAME;
		System.out.println(inventoriesFileLoc);
		
		try {
			in = new FileInputStream(inventoriesFileLoc);
		}
		catch (FileNotFoundException e) {
			
			
			File file = new File(inventoriesFileLoc);
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
				FileOutputStream output = new FileOutputStream(file);
				props.setProperty(PROPERTY_NAME, "");
				props.store(output, "");
				output.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			in = new FileInputStream(inventoriesFileLoc);
		}
		
		System.out.println(in);
		try {
			/*if (in == null) {
				in.close();
				return; //If nothing to import, don't do anything
			}*/
			props.load(in);
			in.close();
		} catch (IOException e) {
			//Something went wrong...
			e.printStackTrace();
		}

		String allInventories = props.getProperty(PROPERTY_NAME);
		System.out.println("allinven: " + allInventories);
		if (allInventories == null || allInventories.equals("")) {
			//MasterInventory.addInventories(inventories); //Add a blank ArrayList
			System.out.println("returning in invens");
			return;
		}
		//System.out.println("props size: " + props.size());
		System.out.println(allInventories);
		String[] inventoriesArr = allInventories.split(",");
		System.out.println("inventoriesArr: " + Arrays.toString(inventoriesArr));
		
		for (String inventory: inventoriesArr) {
			Inventory temp = new Inventory(inventory);
			temp.importInventory();
			inventories.add(temp);
		}
		
		MasterInventory.addInventories(inventories);
	}
	
	/**
	 * Exports the names of the inventories to a file for later use
	 * 
	 * @throws IOException If there is a problem in export
	 */
	public static void exportInventories() throws IOException {
		FileOutputStream output = new FileOutputStream(inventoriesFileLoc);
		
		String inventoryNames = "";
		for(Inventory i: inventories) {
			System.out.println(i.getName());
			inventoryNames += i.getName() + ",";
		}
		if (inventoryNames.equals("")) {
			output.close();
			return; //No inventories created --> don't export anything
		}
		inventoryNames = inventoryNames.substring(0, inventoryNames.length() - 1); //Remove last comma
		props.setProperty(PROPERTY_NAME, inventoryNames);
		props.store(output, "--Saved Inventories--");
		output.close();
	}
	
	/**
	 * Updates the file path for the location of the properties file depending
	 * on the operating system.
	 */
	private static void updateFilePath() {
		if (System.getProperty("os.name").contains("Mac")) {
			inventoriesFileLoc += File.separator + "Library" + File.separator + "WackyWozniaks" + File.separator;
		}
		else {
			inventoriesFileLoc += File.separator + "WackyWozniaks" + File.separator;
		}
		File file = new File(inventoriesFileLoc);
		file.mkdirs();
	}
}