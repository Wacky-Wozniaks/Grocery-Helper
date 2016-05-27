/**
 * Represents an operation executed in the program, such as adding an item to an inventory or changing the quantity of an item.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/27/2016
 */

import java.util.Stack;

public class Operation
{
	public static final int QUANTITY_CHANGE = 1, MIN_CHANGE = 2, MAX_CHANGE = 3, ADDED = 4, REMOVED = 5; //The different operations
	private static Stack<Operation> undo = new Stack<Operation>(), redo = new Stack<Operation>(); //Stacks of the last commands the program has made
	private static boolean enabled = false; //Whether current actions are being added to the stack so that adding items when the program starts isn't counted
	
	private Item item;
	private int operation; //One of the constants from above
	private int changed; //Stores any values involved in quantity or limit changes
	
	/**
	 * Constructs an operation with all of its parameters.
	 * 
	 * @param item The item changed.
	 * @param operation What was done to the item.
	 * @param changed The old value of quantity or a limit.
	 */
	public Operation(Item item, int operation, int changed)
	{
		this.item = item;
		this.operation = operation;
		this.changed = changed;
	}
	
	/**
	 * Constructs an operation without a changed value, such as for adding an item.
	 * 
	 * @param item The item changed.
	 * @param operation What was done to the item.
	 */
	public Operation(Item item, int operation)
	{
		this(item, operation, 0);
	}
	
	/**
	 * Reverses the operation to undo it.
	 */
	private void undo()
	{
		enabled = false; //So that the actions taken to undo won't be added counted as something to undo.
		if(operation == QUANTITY_CHANGE)
		{
			int oldVal = item.getQuantity();
			item.setQuantity(changed);
			changed = oldVal;
		}
		else if(operation == MIN_CHANGE)
		{
			int oldVal = item.getMin();
			item.setMin(changed);
			changed = oldVal;
		}
		else if(operation == MAX_CHANGE)
		{
			int oldVal = item.getMax();
			item.setMax(changed);
			changed = oldVal;
		}
		else if(operation == ADDED) MasterInventory.remove(item);
		else if(operation == REMOVED) MasterInventory.add(item);
		enabled = true;
	}
	
	/**
	 * Performs the operation to redo it.
	 */
	private void redo()
	{
		enabled = false; //So that the actions taken to redo won't be added counted as something to undo.
		if(operation == QUANTITY_CHANGE)
		{
			int oldVal = item.getQuantity();
			item.setQuantity(changed);
			changed = oldVal;
		}
		else if(operation == MIN_CHANGE)
		{
			int oldVal = item.getMin();
			item.setMin(changed);
			changed = oldVal;
		}
		else if(operation == MAX_CHANGE)
		{
			int oldVal = item.getMax();
			item.setMax(changed);
			changed = oldVal;
		}
		else if(operation == ADDED) MasterInventory.add(item);
		else if(operation == REMOVED) MasterInventory.remove(item);
		enabled = true;
	}
	
	/**
	 * Returns whether there is anything stored in the undo stack.
	 * 
	 * @return Whether the undo stack is not empty.
	 */
	public static boolean canUndo()
	{
		return !undo.isEmpty();
	}
	
	/**
	 * Returns whether there is anything stored in the redo stack.
	 * 
	 * @return Whether the redo stack is not empty.
	 */
	public static boolean canRedo()
	{
		return !redo.isEmpty();
	}
	
	/**
	 * Calls undo for the last operation on the stack and puts it on the redo stack.
	 */
	public static void undoLast()
	{
		Operation o = undo.pop();
		o.undo();
		redo.push(o);
		GUI.updateUnRedo();
	}
	
	/**
	 * Calls redo for the last operation on the stack and puts it on the undo stack.
	 */
	public static void redoLast()
	{
		Operation o = redo.pop();
		o.redo();
		undo.push(o);
		GUI.updateUnRedo();
	}
	
	/**
	 * Adds the operation to the undo stack unless the stack is currently disabled.
	 * 
	 * @param operation The last performed operation.
	 */
	public static void addToUndo(Operation operation)
	{
		if(enabled)
		{
			undo.push(operation);
			redo.clear(); //Once a new chain of action happens, redo is cleared
		}
		GUI.updateUnRedo();
	}
	
	/**
	 * Sets whether operations will be added to the stack.
	 * 
	 * @param e Whether the stack will be enabled or disabled.
	 */
	public static void setEnabled(boolean e)
	{
		enabled = e;
	}
}