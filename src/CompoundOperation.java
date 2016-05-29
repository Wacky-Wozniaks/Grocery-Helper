/**
 * Represents an operation that is itself a list of operations (such as adding all items from a grocery list).
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 5/28/2016
 */

import java.util.LinkedList;

public class CompoundOperation extends Operation
{
	private LinkedList<Operation> operations;
	
	/**
	 * Constructs the compound operation.
	 */
	public CompoundOperation()
	{
		operations = new LinkedList<Operation>();
	}
	
	/**
	 * Adds the operation to the list of operations.
	 * 
	 * @param operation The new operation.
	 */
	public void add(Operation operation)
	{
		operations.add(operation);
	}
	
	/**
	 * Undoes all operations in the compound operation.
	 */
	protected void undo()
	{
		for(Operation o: operations)
		{
			o.undo();
		}
	}
	
	/**
	 * Redoes all operations in the compound operation.
	 */
	protected void redo()
	{
		for(Operation o: operations)
		{
			o.redo();
		}
	}
}