/**
 * Represents an item to be stored in the inventory.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/24/2016
 */

public class Item implements Comparable<Item>
{
	private int quantity, min, max, code;
	private String name;
	private ItemGUI gui;
	private Inventory inventory;
	
	/**
	 * Creates an item given all of its instance variables.
	 * 
	 * @param name The name of the item
	 * @param min The minimum acceptable balance of items
	 * @param max The maximum acceptable balance of items
	 * @param quantity The number of items currently in inventory
	 * @param code The item code; -1 if no code
	 */
	public Item(String name, int min, int max, int quantity, int code, Inventory inventory)
	{
		this.name = name;
		this.min = min;
		this.max = max;
		this.quantity = quantity;
		this.code = code;
		this.inventory = inventory;
		gui = new ItemGUI(this);
	}
	
	/**
	 * Creates an item given all of its instance variables besides its code which will be -1.
	 * 
	 * @param name The name of the item
	 * @param min The minimum acceptable balance of items
	 * @param max The maximum acceptable balance of items
	 * @param quantity The number of items currently in inventory
	 */
	public Item(String name, int min, int max, int quantity, Inventory inventory)
	{
		this(name, min, max, quantity, -1, inventory);
	}
	
	/**
	 * Creates an item given all of its instance variables besides its code and quantity.
	 * 
	 * @param name The name of the item
	 * @param min The minimum acceptable balance of items
	 * @param max The maximum acceptable balance of items
	 */
	public Item(String name, int min, int max, Inventory inventory)
	{
		this(name, min, max, 0, -1, inventory);
	}

	/**
	 * Returns the minimum amount of the item wanted.
	 * 
	 * @return The minimum acceptable balance of items
	 */
	public int getMin()
	{
		return min;
	}

	/**
	 * Sets the minimum amount of the item wanted.
	 * 
	 * @param min The minimum acceptable balance of items
	 */
	public void setMin(int min)
	{
		Operation.addToUndo(new Operation(this, Operation.MIN_CHANGE, this.min));
		this.min = min;
		gui.updateQuantity();
	}

	/**
	 * Returns the maximum amount of the item wanted.
	 * 
	 * @return The maximum acceptable balance of items
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 * Sets the maximum amount of the item wanted.
	 * 
	 * @param max The maximum acceptable balance of items
	 */
	public void setMax(int max)
	{
		Operation.addToUndo(new Operation(this, Operation.MAX_CHANGE, this.max));
		this.max = max;
		gui.updateQuantity();
	}

	/**
	 * Returns the item's code.
	 * 
	 * @return The item code; -1 if no code
	 */
	public int getCode()
	{
		return code;
	}
	
	/**
	 * Sets the item's code.
	 * 
	 * @param code The item code; -1 if no code
	 */
	public void setCode(int code)
	{
		this.code = code;
	}

	/**
	 * Returns the name of the item.
	 * 
	 * @return The name of the item
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the item.
	 * 
	 * @param name The name of the item
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the current quantity of the item.
	 * 
	 * @return The quantity of the item in inventory
	 */
	public int getQuantity()
	{
		return quantity;
	}
	
	/**
	 * Sets the quantity of the item by the amount given.
	 * 
	 * @param quantity The quantity of the item in inventory
	 */
	public void setQuantity(int quantity)
	{
		Operation.addToUndo(new Operation(this, Operation.QUANTITY_CHANGE, this.quantity));
		this.quantity = quantity;
		gui.updateQuantity();
	}
	
	/**
	 * Updates the quantity of the item by the amount given.
	 * 
	 * @param change The change in quantity of the item in inventory
	 */
	public void updateQuantity(int change)
	{
		Operation.addToUndo(new Operation(this, Operation.QUANTITY_CHANGE, this.quantity));
		quantity += change;
	}
	
	/**
	 * Returns the inventory that the Item is a part of
	 * 
	 * @return the inventory that the Item is a part of
	 */
	public Inventory getInventory() {
		return inventory;
	}
	
	/**
	 * sets the inventory
	 * 
	 * @param inventory The inventory to add this item to
	 */
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	/**
	 * Returns true if more of the item is needed.
	 * 
	 * @return Whether more of the item is needed in inventory
	 */
	public boolean moreNeeded()
	{
		return quantity < min;
	}
	
	/**
	 * Returns the amount that should be purchased of the item given that moreNeeded returns true.
	 * 
	 * @return The amount that should be purchased of the item given that moreNeeded returns true
	 */
	public int amountToBuy()
	{
		return max - quantity;
	}
	
	/**
	 * Compares this to Item other
	 * 
	 * @param other The Item to be compared to
	 * @return The compared number
	 */
	public int compareTo(Item other)
	{
		return name.compareTo(other.getName());
	}
	
	/**
	 * Returns whether this is equal to other
	 * 
	 * @param other The Item to be compared to
	 * @return Whether this is equivalent to other
	 * @throws IllegalArgumentException Other is not of type Item
	 */
	public boolean equals(Object other) {
		if(other instanceof Item) return this.compareTo((Item) other) == 0;
		else throw new IllegalArgumentException();
	}
	
	/**
	 * Returns the String of this
	 * 
	 * @return the String form of this
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * Returns the GUI associated with the item.
	 * 
	 * @return The GUI for the item.
	 */
	public ItemGUI getGUI()
	{
		return gui;
	}
}