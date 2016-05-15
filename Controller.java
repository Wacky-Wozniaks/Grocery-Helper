/**
 * The main class for the project.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/14/2016
 */

public class Controller
{
	/**
	 * Runs the program.
	 */
	public static void main(String[] args)
	{
		Inventory i = new Inventory("name");
		i.add(new Item("Apple", 3, 8, 5));
		i.add(new Item("Banana", 4, 7, 2));
		i.add(new Item("Carrot", 4, 8, 5));
		
		InventoryGUI g = new InventoryGUI(i);
	}
}