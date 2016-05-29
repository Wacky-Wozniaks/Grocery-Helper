/**
 * The main GUI for the project.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/28/2016
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GUI extends JFrame
{
	public static GUI gui; //This is a terrible implementation which I will change later.
	public static int WIDTH;
	private static final String GHOST_TEXT = "Search Inventory...";
	private static final Color GHOST_COLOR = Color.LIGHT_GRAY;
	
	final static JMenuItem undo = new JMenuItem("Undo"); //Undoes the last operation
	final static JMenuItem redo = new JMenuItem("Redo"); //Redoes the last undone operation
	
	private Inventory selected;
	private JPanel display, options;
	private JTextField bar;
	
	/**
	 * Constructs the GUI for the given inventories.
	 */
	public GUI()
	{
		/*
		 * If the user's computer is a Mac, then use the default Mac LookAndFeel
		 * Otherwise, if the Nimbus LookAndFeel is installed, use that.
		 * Otherwise, use the computer's default LookAndFeel.
		 * 
		 * Nimbus is not directly imported for potential compatibility issues between JRE 1.6 and 1.7
		 * See: https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html
		 */
		try
		{
			if(!System.getProperty("os.name").contains("Mac")) {
				for (LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) { 
						UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			}
		}
		catch(Throwable e){}
		
		addMenu();
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		options = new JPanel();
		panel.add(options, c);
		c.gridy++;
		
		bar = new JTextField(20);
		//searches the inventory as the keys are pressed
		bar.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				String lowercase = bar.getText().toLowerCase();
				for(Item i: selected.getInventory()) {
					if(i.getName().toLowerCase().contains(lowercase)) i.getGUI().setVisible(true);
					else i.getGUI().setVisible(false);
				}
				selected.getGUI().removeScrollSpace();
				selected.getGUI().addScrollSpace();
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
		
		//Keyboard shortcut (CTRL-F or CMD-F) for searching
		KeyStroke searchShortcut = KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		bar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(searchShortcut, "search");
		bar.getActionMap().put("search", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				bar.requestFocusInWindow(); //Make the search field have the focus
			}
		});
		
		//Adds items to the inventory 
		JButton add = new JButton("Add Item");
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				newItem();
			}
		});
		
		JPanel upperBar = new JPanel();
		upperBar.add(bar);
		upperBar.add(add);
		panel.add(upperBar, c);
		c.gridy++;
		WIDTH = upperBar.getPreferredSize().width;
		
		display = new JPanel();
		panel.add(display, c);
		
		options.add(new InventoryButton(MasterInventory.getInventory()));
		if(Inventories.getList() != null && Inventories.getList().size() != 0)
		{
			for(Inventory inventory: Inventories.getList())
			{
				options.add(new InventoryButton(inventory));
			}
		}
		updateSelected(MasterInventory.getInventory());
		options.getComponent(0).setBackground(Color.LIGHT_GRAY);
		
		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		this.setResizable(false);
		requestFocus(); //makes the frame get the focus
		gui = this;
	}
	
	/**
	 * Checks whether or not the Undo and Redo buttons should be enabled and toggles them accordingly.	
	 */
	public static void updateUnRedo() {
		if (Operation.canUndo()) {
			undo.setEnabled(true);
		}
		if (Operation.canRedo()) {
			redo.setEnabled(true);
		}
	}
	
	/**
	 * Replaces the inventory in the central panel with the new inventory.
	 * 
	 * @param i The inventory to display.
	 */
	private void updateSelected(Inventory i)
	{
		for(Component tab: options.getComponents()) {
			if (!i.equals(((InventoryButton) tab).getInventory())) { //Set which inventory is selected
				((InventoryButton) tab).setSelected(false);
			}
			else {
				((InventoryButton) tab).setSelected(true);
			}
			
			if (!((InventoryButton) tab).getSelected() && !i.equals(((InventoryButton) tab).getInventory())) {
				tab.setBackground(Color.WHITE);
			}
			else {
				tab.setBackground(Color.LIGHT_GRAY);
			}
		}
		
		display.setVisible(false);
		selected = i;
		display.removeAll();
		i.getGUI().resetBox();
		display.add(i.getGUI());
		display.setVisible(true);
	}
	
	private void addMenu() {
		JMenuBar menu = new JMenuBar();
		
		JMenu file = new JMenu("File");
		JMenu newMenu = new JMenu("New");
		
		JMenuItem inventory = new JMenuItem("Inventory..."); //Allows another item to be added to the inventory
		inventory.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				Object name = JOptionPane.showInputDialog(display, "Enter the name of the new inventory.", "Add Inventory", JOptionPane.PLAIN_MESSAGE);
				if(name == null) return;
				Inventory i = new Inventory((String) name);
				

				while ((name!= null && !((String)name).trim().equals("")) && (Inventories.getList().contains(i) || name.equals(MasterInventory.NAME))) {
					name = JOptionPane.showInputDialog(display, name + " is already an inventory. Enter a different"
							+ " name", "Add Inventory", JOptionPane.PLAIN_MESSAGE);
					i.setName((String) name);
				}
				
				if(name == null || ((String)name).trim().equals("")) return; //If the user chose to cancel after entering an invalid name, treats an empty
						//string as the same as cancel
				Inventories.addInventory(i);
			}
		});
		inventory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		newMenu.add(inventory);
		
		JMenuItem item = new JMenuItem("Item..."); //Allows the addition of new items to the selected inventory
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				newItem();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		newMenu.add(item);
		file.add(newMenu);
		
		JMenu export = new JMenu("Export Grocery List"); //Exports the grocery list
		
		JMenuItem exportToFile = new JMenuItem("File..."); //exports list to file
		exportToFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected.exportListToTextFile();
			}
		});
		export.add(exportToFile);
		
		JMenuItem exportToEmail = new JMenuItem("Email..."); //exports list to email
		exportToEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected.exportListToEmail();
			}
		});
		export.add(exportToEmail);
		
		file.add(export);
		
		JMenuItem print = new JMenuItem("Print");
		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected.printList();
			}
		});
		print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		file.add(print);
		
		menu.add(file);
		
		JMenu edit = new JMenu("Edit");
		
		undo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(Operation.canUndo()) Operation.undoLast();
				if(!Operation.canUndo()) { //If ran out of things to undo, disable the button
					undo.setEnabled(false);
				}
			}
		}); 
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		undo.setEnabled(false);
		edit.add(undo);
		
		redo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(Operation.canRedo()) Operation.redoLast();
				if(!Operation.canRedo()) { //If ran out of things to redo, disable the button
					redo.setEnabled(false);
				}
			}
		});
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		redo.setEnabled(false);
		edit.add(redo);
		menu.add(edit);
		
		JMenu inventoryMenu = new JMenu("Inventory"); //the inventory menu
		
		JMenuItem inventory2 = new JMenuItem("New...");
		inventory2.addActionListener(inventory.getActionListeners()[0]);
		inventory2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		inventoryMenu.add(inventory2);
		
		JMenuItem removeInventory = new JMenuItem("Remove");
		removeInventory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(selected.getName().equals(MasterInventory.NAME))
				{
					JOptionPane.showMessageDialog(display, "The master inventory cannot be removed.", 
						"Invalid Removal", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Inventories.removeInventory(selected);
			}
		});
		removeInventory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		inventoryMenu.add(removeInventory);
		
		menu.add(inventoryMenu);
		
		this.setJMenuBar(menu);
	}
	
	/**
	 * Creates a new add frame after testing to ensure that the selected inventory is not the master inventory.
	 */
	private void newItem()
	{
		if(selected.getName().equals(MasterInventory.NAME))
		{
			if(Inventories.getList().size() == 0) //There must be an inventory besides the master inventory to add items
			{
				JOptionPane.showMessageDialog(display, "<html>There are no inventories to add items to.<br>"
						+ "Please create an inventory before adding items</html>", "No Inventories", JOptionPane.ERROR_MESSAGE, null);
				return;
			}
			else
			{
				Object o = JOptionPane.showInputDialog(display, "Select an inventory to add the item to.", "Choose Inventory", JOptionPane.PLAIN_MESSAGE, null,
						Inventories.getList().toArray(), Inventories.getList().get(0));
				if(o != null)
				{
					new AddFrame(((Inventory) o), getSearchText()); //If the user did not hit cancel
					updateSelected((Inventory) o);
				}
				return;
			}
		}
		JFrame add = new AddFrame(selected, getSearchText());
		add.setLocationRelativeTo(this);
		add.setVisible(true);
	}
	
	/**
	 * Returns then contents of the search bar.
	 * 
	 * @return The text of the search bar, or an empty string if it contains the ghost text.
	 */
	private String getSearchText()
	{
		if(bar.getText().equals(GHOST_TEXT)) return "";
		else return bar.getText();
	}
	
	/**
	 * A button for the menu bar to select an inventory.
	 */
	private class InventoryButton extends JPanel
	{
		private Inventory inventory;
		private boolean selected;
		/**
		 * Constructs the panel.
		 * 
		 * @param i The inventory to be selected with this panel.
		 */
		public InventoryButton(Inventory i)
		{
			inventory = i;
			selected = false;
			setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1, true), new EmptyBorder(2, 2, 2, 2)));
			setBackground(Color.WHITE);
			add(new JLabel(i.getName()));
			addMouseListener(new MouseListener()
			{
				public void mouseClicked(MouseEvent arg0)
				{
					updateSelected(inventory);
					selected = true;
				}
				
				public void mouseEntered(MouseEvent arg0)
				{
					setBackground(Color.LIGHT_GRAY);
				}
				
				public void mouseExited(MouseEvent arg0)
				{
					if (!selected){
						setBackground(Color.WHITE);
					}
				}
				
				public void mousePressed(MouseEvent arg0){}
				public void mouseReleased(MouseEvent arg0){}
			});
		}
		
		public boolean getSelected() {
			return selected;
		}
		
		public Inventory getInventory() {
			return inventory;
		}
		
		public void setSelected(boolean s) {
			selected = s;
		}
	}
	
	/**
	 * Updates the display to contain the new inventory.
	 * 
	 * @param i The new inventory
	 */
	public void addInventory(Inventory i)
	{
		Operation.addToUndo(new Operation(i, Operation.ADDED));
		options.add(new InventoryButton(i));
		updateSelected(i);
		pack();
	}
	
	/**
	 * Updates the display to remove the inventory.
	 * 
	 * @param i The deleted inventory
	 */
	public void removeInventory(Inventory i)
	{
		Operation.addToUndo(new Operation(i, Operation.REMOVED));
		for(Component c: options.getComponents())
		{
			if(c instanceof InventoryButton)
			{
				if(((InventoryButton)c).getInventory().equals(i))
				{
					options.remove(c);
					break;
				}
			}
		}
		updateSelected(MasterInventory.getInventory());
		pack();
	}
}