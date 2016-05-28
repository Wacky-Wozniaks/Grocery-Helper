/**
 * A frame that allows the addition of new items to the inventory.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/27/2016
 */

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddFrame extends JFrame
{
	public final String[] VALUES = {"Quantity", "Minimum Limit", "Maximum Limit"};
	private JTextField name;
	private JTextField[] intVals;
	private JPanel addPanel;
	private Inventory inventory;
	
	/**
	 * Creates the frame.
	 * 
	 * @param i The inventory to add the item to.
	 * @param text What will automatically be entered as the name of the item entered-- the text from the search bar.
	 */
	public AddFrame(Inventory i, String text)
	{
		super("Add Item");
		inventory = i;
		
		//The panel contains textfields for name, quantity, and min and max limits.
		addPanel = new JPanel(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.gridy = 0;
		g.gridx = 0;
		g.anchor = GridBagConstraints.WEST;
		addPanel.add(new JLabel(" Name"), g);
		g.gridx++;
		name = new JTextField(text, 10);
		addPanel.add(name, g);
		
		intVals = new JTextField[VALUES.length];
		for(int index = 0; index < VALUES.length; index++)
		{
			g.gridx = 0;
			g.gridy++;
			addPanel.add(new JLabel(" " + VALUES[index] + " "), g);
			g.gridx++;
			intVals[index] = new JTextField(10);
			addPanel.add(intVals[index], g);
		}
		
		//Once the button is pressed, it will add the item to the inventory
		JButton add = new JButton("Add");
		final JFrame thisFrame = this; //I need this so I can dispose it at the end of the action listener
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				//Tests that the name is not an empty string.
				String n = name.getText();
				if(n.trim().equals(""))
				{
					JOptionPane.showMessageDialog(addPanel, "Invalid item name.", "", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				
				//Tests that each input is a number
				int[] values = new int[intVals.length];
				for(int index = 0; index < intVals.length; index++)
				{
					try
					{
						values[index] = Integer.parseInt(intVals[index].getText());
					}
					catch(Throwable e)
					{
						JOptionPane.showMessageDialog(addPanel, VALUES[index] + " must be an integer.", "", JOptionPane.ERROR_MESSAGE, null);
						return;
					}
				}
				
				//Constructs the item and adds it to the inventory
				Item i = new Item(n, values[1], values[2], values[0], inventory);
					
				//Inform the user if the maximum quantity is set to smaller than the minimum quantity
				if (values[2] < values[1]) {
					JOptionPane.showMessageDialog(addPanel, VALUES[2] + " must be greater than or equal to " + VALUES[1] + ".", "", JOptionPane.ERROR_MESSAGE, null);
					return;
				}
				
				//If the item already exists in the inventory, gives the option to merge the two values. 
				//If user chose to merge, the update method will add it to the GUI
				if(!MasterInventory.add(i))
				{
					if (inventory.get(n) == null) { //If the item is in another inventory, not in the currently selected one
						Inventory invenContained = MasterInventory.getInventory().get(n).getInventory(); //Find the inventory the Item is contained in
						int merge = JOptionPane.showConfirmDialog(addPanel, "<html>" + n + " already exists in the inventory " + invenContained +
								" with quantity " + invenContained.get(n).getQuantity() + ".<br>" + "Would you like to merge the items into that inventory?",
								"Item Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if(merge == JOptionPane.YES_OPTION) {
							invenContained.merge(i);
							invenContained.get(n).getGUI().updateQuantity();	
						}
						else {
							return;
						}
						
					}
					else { //If item is in the currently selected inventory
						int merge = JOptionPane.showConfirmDialog(addPanel, "<html>" + n + " already exists in this inventory with quantity " + 
								inventory.get(n).getQuantity() + ".<br>" + "Would you like to merge the items?", "Item Already Exists", 
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if(merge == JOptionPane.YES_OPTION)
						{
							inventory.merge(i);
							inventory.get(n).getGUI().updateQuantity();
							
						}
						else return;
					}
				}
				thisFrame.dispose();
			}
		});
	
		this.setResizable(false);
		
		g.gridy++;
		g.gridx = 0;
		addPanel.add(add, g);
		add(addPanel);
		pack();
		setVisible(true);
	}
}