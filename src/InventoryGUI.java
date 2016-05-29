/**
 * The GUI for an inventory.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/27/2016
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.Box.Filler;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class InventoryGUI extends JPanel implements Observer
{
	private static final int SCROLL_PANEL_HEIGHT = 300;
	
	private Inventory inventory;
	private LinkedList<Item> groceries;
	private JPanel panel;
	private Box b; //A box which contains all items
	private JScrollPane scroll;
	
	/**
	 * Constructs the GUI with the given inventory.
	 * 
	 * @param inventory The inventory to be displayed.
	 */
	public InventoryGUI(Inventory inv)
	{
		/*
		 * If the user's computer is a Mac, then use the default Mac LookAndFeel.
		 * Otherwise, use the SeaGlass LookAndFeel, if it can be accessed properly.
		 * Otherwise, use the computer's default LookAndFeel.
		 *
		try
		{
			if(!System.getProperty("os.name").contains("Mac")) {
				UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
			}
		}
		catch(Throwable e){}
		*/
		this.inventory = inv;
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		
		b = Box.createVerticalBox();
		scroll = new JScrollPane(b, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scroll, c);
		
		JButton list = new JButton("Create Grocery List");
		list.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				groceries = inventory.getGroceryList();
				if(groceries.size() == 0) {
					JOptionPane.showMessageDialog(panel, "No groceries need to be purchased.", "No List Created", JOptionPane.PLAIN_MESSAGE);
					return;
				}
				new GroceryListGUI(inventory);
			}
		});
		c.gridy++;
		panel.add(list, c);		
		add(panel);
	}
	
	/**
	 * Updates based on the inventory's changes
	 * 
	 * @param arg0 The observable object.
	 * @param arg1 An argument passed to the notifyObservers method.
	 */
	public void update(Observable arg0, Object arg1)
	{
		if(arg1 != null)
		{
			if(inventory.contains((Item) arg1))
			{
				b.setVisible(false);
				removeScrollSpace();
				b.add(((Item) arg1).getGUI());
				addScrollSpace();
				b.setVisible(true);
			}
			else
			{
				b.setVisible(false);
				removeScrollSpace();
				b.remove(((Item) arg1).getGUI());
				addScrollSpace();
				b.setVisible(true);
			}
		}
	}
	
	/**
	 * If there is a RigidSpace in the ScrollPane, it removes it.
	 */
	public void removeScrollSpace() {
		if(b.getComponent(b.getComponentCount() - 1) instanceof Filler) //if there is a rigid box
			b.remove(b.getComponentCount() - 1); //removes the rigid box
	}
	
	/**
	 * If a RigidSpace in the ScrollPane, it adds it.
	 */
	public void addScrollSpace() {
		if(SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight() > 0) //if there is area left over adds a rigid box
			b.add(Box.createRigidArea(new Dimension((int) b.getPreferredSize().getWidth(), (int) (SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight()))));
	}
	
	/**
	 * If there is area left over adds a rigid box to the ScrollPane
	 */
	public void addBox()
	{
		if(SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight() > 0) //
			b.add(Box.createRigidArea(new Dimension((int) b.getPreferredSize().getWidth(), (int) (SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight()))));
	}
	
	/**
	 * Re-adds all items to the box as they may have been removed to be displayed in another inventory.
	 */
	public void resetBox()
	{
		scroll.setPreferredSize(new Dimension(GUI.WIDTH, SCROLL_PANEL_HEIGHT));
		if(b.getComponentCount() != 0) removeScrollSpace(); //Removes the rigid box if there is one.
		for(Item i: inventory.getInventory())
		{
			b.add(i.getGUI());
		}
		addScrollSpace();
	}
}