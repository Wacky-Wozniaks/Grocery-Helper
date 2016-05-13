/*
 * Represents an item to be stored in the inventory.
 */

public class Item
{
	private int quantity, min, max, code;
	private String name;
	
	/*
	 * Creates an item given all of its instance variables.
	 */
	public Item(String name, int min, int max, int quantity, int code)
	{
		this.name = name;
		this.min = min;
		this.max = max;
		this.quantity = quantity;
		this.code = code;
	}
	
	/*
	 * Creates an item given all of its instance variables besides its code.
	 */
	public Item(String name, int min, int max, int quantity)
	{
		this(name, min, max, quantity, -1);
	}
	
	/*
	 * Creates an item given all of its instance variables besides its code and quantity.
	 */
	public Item(String name, int min, int max)
	{
		this(name, min, max, 0, -1);
	}

	/*
	 * Returns the minimum amount of the item wanted.
	 */
	public int getMin()
	{
		return min;
	}

	/*
	 * Sets the minimum amount of the item wanted.
	 */
	public void setMin(int min)
	{
		this.min = min;
	}

	/*
	 * Returns the maximum amount of the item wanted.
	 */
	public int getMax()
	{
		return max;
	}

	/*
	 * Sets the maximum amount of the item wanted.
	 */
	public void setMax(int max)
	{
		this.max = max;
	}

	/*
	 * Returns the item's code.
	 */
	public int getCode()
	{
		return code;
	}
	
	/*
	 * Sets the item's code.
	 */
	public void setCode(int code)
	{
		this.code = code;
	}

	/*
	 * Returns the name of the item.
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * Sets the name of the item.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/*
	 * Returns the current quantity of the item.
	 */
	public int getQuantity()
	{
		return quantity;
	}
	
	/*
	 * Updates the quantity of the item by the amount given.
	 */
	public void updateQuantity(int change)
	{
		quantity += change;
	}
	
	/*
	 * Returns true if more of the item is needed.
	 */
	public boolean moreNeeded()
	{
		return quantity < min;
	}
	
	/*
	 * Returns the amount that should be purchased of the item given that moreNeeded returns true.
	 */
	public int amountToBuy()
	{
		return max - quantity;
	}
}