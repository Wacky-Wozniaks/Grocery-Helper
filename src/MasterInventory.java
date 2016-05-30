/**
 * A singleton class containing the inventory which contains all other inventories.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 5/27/2016
 */

import java.util.ArrayList;

public class MasterInventory
{
	public static final String NAME = "Master";
	private static Inventory master = new Inventory(NAME);
	
	/**
	 * Returns the master inventory.
	 * 
	 * @return The master inventory.
	 */
	public static Inventory getInventory()
	{
		return master;
	}
	
	/**
	 * Adds all the other inventories to the master inventory.
	 * 
	 * @param inventories The list of all the other inventories.
	 */
	public static void addInventories(ArrayList<Inventory> inventories)
	{
		for(Inventory inventory: inventories)
		{
			master.addAll(inventory.getInventory());
		}
	}
	
	/**
	 * Adds the item to this inventory and the specific inventory.
	 * 
	 * @param item The item to add.
	 * @return Whether the item was added successfully.
	 */
	public static boolean add(Item item)
	{
		if(!master.add(item)) return false;
		item.getInventory().add(item);
		return true;
	}
	
	/**
	 * Removes the item from the inventory and the specific inventory.
	 * 
	 * @param item The item to remove.
	 */
	public static void remove(Item item)
	{
		master.remove(item);
		item.getInventory().remove(item);
	}
	
	/**
	 * Removes all the items in the given inventory.
	 * 
	 * @param inventory THe inventory to remove all the items from.
	 */
	public static void remove(Inventory inventory)
	{
		for(Item item: inventory.getInventory())
		{
			master.remove(item);
		}
	}
}