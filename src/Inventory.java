/**
 * Represents the inventory in a binary search tree
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/20/2016
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Scanner;
import java.util.TreeMap;

public class Inventory extends Observable {
	
	private static final String EXTENSION = ".inventory";
	
	private TreeMap<String, Item> inventory;
	private String name;
	private String fileName;
	private File file;
	private InventoryGUI gui;
	
	/**
	 * Represents an inventory of items initialized with the given items.
	 * 
	 * @param name The name of the inventory
	 * @param items The items to be added to the inventory
	 */
	public Inventory(String name, Collection<Item> items) {
		
		inventory = new TreeMap<String, Item>();
		for(Item i: items)
		{
			inventory.put(i.getName().toLowerCase(), i);
		}
		this.name = name;
		fileName = name + EXTENSION;
		file = new File(fileName);
		//gui = new InventoryGUI(this);
		//addObserver(gui);
	}
	
	/**
	 * Represents an inventory of items.
	 * 
	 * @param name The name of the inventory
	 */
	public Inventory(String name) {
		inventory = new TreeMap<String, Item>();
		this.name = name;
		fileName = name + EXTENSION;
		file = new File(fileName);
		//gui = new InventoryGUI(this);
		//addObserver(gui);
	}
	
	/**
	 * Creates a grocery list based on what items are below the minimum threshold
	 * 
	 * @return The grocery list
	 */
	public LinkedList<Item> getGroceryList() {
		LinkedList<Item> list = new LinkedList<Item>();
		for(Item i: inventory.values())
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
		for(Item i: inventory.values()) writer.println(i.getName() + "," + i.getMin() + "," + i.getMax() + "," + i.getQuantity() + "," 
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
			add(new Item(item[0], Integer.parseInt(item[1]), Integer.parseInt(item[2]), Integer.parseInt(item[3]), Integer.parseInt(item[4]), this));
		}
		scan.close();
	}
	
	/**
	 * Returns the TreeSet of the inventory.
	 * 
	 * @return The inventory represented as a TreeSet.
	 */
	public Collection<Item> getInventory()
	{
		return inventory.values();
	}
	
	/**
	 * Adds the item to the inventory.
	 * 
	 * @param item The item to be added.
	 */
	public boolean add(Item item)
	{
		if(inventory.containsKey(item.getName().toLowerCase())) return false;
		inventory.put(item.toString().toLowerCase(), item);
		setChanged();
		notifyObservers(item);
		return true;
	}
	
	/**
	 * Returns the item associated with the name in the map, or null if it does not exist.
	 * 
	 * @param name The name of the object to find.
	 * @return The object in the tree with the given name, or null if it does not exist.
	 */
	public Item get(String name)
	{
		return inventory.get(name.toLowerCase());
	}
	
	/**
	 * Merges the item with the instance of the item that already exists in the inventory. Adds the quantities and sets min and max to the new values.
	 * 
	 * @param item The item to merge into the inventory.
	 */
	public void merge(Item item)
	{
		Item i = inventory.get(item.getName().toLowerCase());
		i.updateQuantity(item.getQuantity());
		i.setMax(item.getMax());
		i.setMin(item.getMin());
	}
	
	/**
	 * Returns the GUI for this inventory.
	 * 
	 * @return The InventoryGUI for this inventory.
	 */
	public InventoryGUI getGUI()
	{
		if(gui == null)
		{
			gui = new InventoryGUI(this);
			addObserver(gui);
		}
		return gui;
	}
	
	/**
	 * Removes an item from the inventory.
	 * 
	 * @param item The item to remove.
	 */
	public void remove(Item item)
	{
		inventory.remove(item.getName().toLowerCase());
		setChanged();
		notifyObservers(item);
	}
	
	/**
	 * Returns whether the inventory contains that item.
	 * 
	 * @param item The item to check for.
	 * @return Whether the inventory contains the item.
	 */
	public boolean contains(Item item)
	{
		return inventory.containsValue(item);
	}
}
