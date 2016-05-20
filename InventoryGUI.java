/**
 * The GUI for an inventory.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/18/2016
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.Box.Filler;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class InventoryGUI extends JPanel implements Observer
{
	private static final String GHOST_TEXT = "Search Inventory...";
	private static final Color GHOST_COLOR = Color.LIGHT_GRAY;
	private static final int SCROLL_PANEL_HEIGHT = 300;
	
	private Inventory inventory;
	private JTextField bar;
	private LinkedList<Item> groceries;
	private JPanel panel;
	private Box b; //A box which contains all items
	
	/**
	 * Constructs the GUI with the given inventory.
	 * 
	 * @param inventory The inventory to be displayed.
	 */
	public InventoryGUI(Inventory inv)
	{
		try
		{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch(Throwable e){}
		
		this.inventory = inv;
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		
		bar = new JTextField(20);
		//searches the inventory as the keys are pressed
		bar.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				String lowercase = bar.getText().toLowerCase();
				for(Item i: inventory.getInventory()) {
					if(i.getName().toLowerCase().contains(lowercase)) i.getGUI().setVisible(true);
					else i.getGUI().setVisible(false);
				}
				changeBox();
			}
		});
		
		//Adds and removes instructions for searching based on whether the textfield has focus
		bar.setForeground(GHOST_COLOR);
		bar.setText(GHOST_TEXT);
		bar.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if(bar.getForeground().equals(GHOST_COLOR) && bar.getText().equals(GHOST_TEXT)) {
					bar.setForeground(Color.BLACK);
					bar.setText("");
				}
			}
			
			public void focusLost(FocusEvent e) {
				if(bar.getText().trim().equals("")) {
					bar.setForeground(GHOST_COLOR);
					bar.setText(GHOST_TEXT);
				}
			}
		});		
		
		//Adds items to the inventory 
		JButton add = new JButton("Add Item");
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				new AddFrame();
			}
		});
		
		JPanel upperBar = new JPanel();
		upperBar.add(bar);
		upperBar.add(add);
		panel.add(upperBar, c);
		
		c.gridy++;
		b = Box.createVerticalBox();
		for(Item i: inventory.getInventory())
		{
			b.add(i.getGUI());
		}
		if(SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight() > 0) //if there is area left over
			b.add(Box.createRigidArea(new Dimension((int) b.getPreferredSize().getWidth(), (int) (SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight()))));
		JScrollPane scroll = new JScrollPane(b, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(upperBar.getPreferredSize().width, SCROLL_PANEL_HEIGHT));
		panel.add(scroll, c);
		
		JButton list = new JButton("Create Grocery List");
		list.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				groceries = inventory.getGroceryList();
				if(groceries.size() == 0) {
					JOptionPane.showMessageDialog(panel, "No groceries need to be purchased.", "No List Created", JOptionPane.PLAIN_MESSAGE);
					return;
				}
				new GroceryListGUI(groceries);
			}
		});
		c.gridy++;
		panel.add(list, c);
		
		add(panel);
		this.requestFocus(); //makes the frame get the focus
	}
	
	/**
	 * A private inner class for the frame that allows the addition of a new item to the inventory
	 */
	private class AddFrame extends JFrame
	{
		public final String[] VALUES = {"Quantity", "Minimum Limit", "Maximum Limit"};
		private JTextField name;
		private JTextField[] intVals;
		private JPanel addPanel;
		
		/**
		 * Creates the frame.
		 */
		public AddFrame()
		{
			super("Add Item");
			//The panel contains textfields for name, quantity, and min and max limits.
			addPanel = new JPanel(new GridBagLayout());
			GridBagConstraints g = new GridBagConstraints();
			g.gridy = 0;
			g.gridx = 0;
			g.anchor = GridBagConstraints.WEST;
			addPanel.add(new JLabel(" Name"), g);
			g.gridx++;
			name = new JTextField(10);
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
					if(inventory.add(i))
					{
						if(b.getComponent(b.getComponentCount() - 1) instanceof Filler) //if there is a rigid box
							b.remove(b.getComponentCount() - 1); //removes the rigid box
						b.add(i.getGUI());
						if(SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight() > 0) //if there is area left over adds a rigid box
							b.add(Box.createRigidArea(new Dimension((int) b.getPreferredSize().getWidth(), (int) (SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight()))));
					}
					else
					{
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
				b.add(((Item) arg1).getGUI());
				changeBox();
				b.setVisible(true);
			}
			else b.remove(((Item) arg1).getGUI());
		}
	}
	
	/**
	 * Changes the box with the item options.
	 */
	private void changeBox()
	{
		//if there is a rigid box
		if(b.getComponent(b.getComponentCount() - 1) instanceof Filler) 	
			b.remove(b.getComponentCount() - 1); //removes the rigid box
		if(SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight() > 0) //if there is area left over adds a rigid box
			b.add(Box.createRigidArea(new Dimension((int) b.getPreferredSize().getWidth(), (int) (SCROLL_PANEL_HEIGHT - b.getPreferredSize().getHeight()))));

	}
}