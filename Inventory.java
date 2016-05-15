/**
 * Represents the inventory in a binary search tree
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/15/2016
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Scanner;
import java.util.TreeSet;

public class Inventory extends Observable {
	
	private static final String EXTENSION = ".inventory";
	
	private TreeSet<Item> inventory;
	private String name;
	private String fileName;
	private File file;
	
	/**
	 * Represents an inventory of items initialized with the given items.
	 * 
	 * @param name The name of the inventory
	 * @param items The items to be added to the inventory
	 */
	public Inventory(String name, Collection<Item> items) {
		inventory = new TreeSet<Item>(items);
		this.name = name;
		fileName = name + EXTENSION;
		file = new File(fileName);
	}
	
	/**
	 * Represents an inventory of items.
	 * 
	 * @param name The name of the inventory
	 */
	public Inventory(String name) {
		inventory = new TreeSet<Item>();
		this.name = name;
		fileName = name + EXTENSION;
		file = new File(fileName);
	}
	
	/**
	 * Creates a grocery list based on what items are below the minimum threshold
	 * 
	 * @return The grocery list
	 */
	public LinkedList<Item> getGroceryList() {
		LinkedList<Item> list = new LinkedList<Item>();
		for(Item i: inventory)
			if(i.moreNeeded()) list.add(i);
		return list;
	}
	
	/**
	 * Returns the name of the inventory
	 * 
	 * @return the name of the inventory
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the inventory
	 * 
	 * @param name The new name of the inventory
	 */
	public void setName(String name) {
		this.name = name;
		fileName = name + EXTENSION;
		File newFile = new File(fileName);
		if(file.exists()) file.renameTo(newFile);
		file = newFile;
	}
	
	/**
	 * Exports this inventory to a file
	 * 
	 * @throws IOException if the file cannot be created
	 */
	public void exportInventory() throws IOException {
		if(!file.exists()) file.createNewFile();
		PrintWriter writer = new PrintWriter(file);
		for(Item i: inventory) writer.println(i.getName() + "," + i.getMin() + "," + i.getMax() + "," + i.getQuantity() + "," 
					+ i.getCode());
		writer.close();
	}
	
	/**
	 * Imports this inventory to a file
	 * 
	 * @throws FileNotFoundException if there is no file to import
	 */
	public void importInventory() throws FileNotFoundException {
		if(!file.exists()) throw new FileNotFoundException();
		Scanner scan = new Scanner(file);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String[] item = line.split(",");
			inventory.add(new Item(item[0], Integer.parseInt(item[1]), Integer.parseInt(item[2]), Integer.parseInt(item[3]),
					Integer.parseInt(item[4])));
		}
		scan.close();
	}
	
	/**
	 * Returns the TreeSet of the inventory.
	 * 
	 * @return The inventory represented as a TreeSet.
	 */
	public TreeSet<Item> getInventory()
	{
		return inventory;
	}
	
	/**
	 * Adds the item to the inventory.
	 * 
	 * @param item The item to be added.
	 */
	public void add(Item item)
	{
		inventory.add(item);
	}
	
}
