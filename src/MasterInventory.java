/**
 * An inventory which contains all other inventories.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 5/26/2016
 */

import java.util.ArrayList;

public class MasterInventory
{
	private static Inventory master = new Inventory("Master");
	
	/**
	 * Adds all the other inventories to the master inventories.
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
	 */
	public static boolean add(Item item)
	{
		if(!master.add(item)) return false;
		item.getInventory().add(item);
		return true;
	}
	
	/**
	 * Removes the item from the inventory and the specific inventory.
	 */
	public static void remove(Item item)
	{
		master.remove(item);
		item.getInventory().remove(item);
	}
}