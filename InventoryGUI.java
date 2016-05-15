/**
 * The GUI for the project.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/15/2016
 */

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class InventoryGUI extends JFrame
{
	private Inventory inventory;
	
	/**
	 * Constructs the GUI with the given inventory.
	 * 
	 * @param inventory The inventory to be displayed.
	 */
	public InventoryGUI(Inventory inventory)
	{
		try
		{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch(Throwable e){}
		
		this.inventory = inventory;
		JPanel panel = new JPanel();
		
		Box b = Box.createVerticalBox();
		JScrollPane scroll = new JScrollPane(b);
		for(Item i: inventory.getInventory())
		{
			b.add(new ItemGUI(i));
		}
		panel.add(scroll);
		
		add(panel);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}