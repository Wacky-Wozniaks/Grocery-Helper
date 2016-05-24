/**
 * The GUI for an inventory.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/24/2016
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
				removeScrollSpace();
				addScrollSpace();
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
				//Creates an add frame, and if there is something in the search bar, enters that to be displayed as the name of the new item.
				if(!bar.getText().equals(GHOST_TEXT)) new AddFrame(inventory, bar.getText());
				else new AddFrame(inventory, "");
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
		addScrollSpace();
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
	
}