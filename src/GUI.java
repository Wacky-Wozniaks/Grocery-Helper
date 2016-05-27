/**
 * The main GUI for the project.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/24/2016
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GUI extends JFrame
{
	private ArrayList<Inventory> inventories;
	private Inventory selected;
	private JPanel display, options;
	
	/**
	 * Constructs the GUI for the given inventories.
	 * 
	 * @param i The inventories in the program.
	 */
	public GUI(ArrayList<Inventory> i)
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
		
		inventories = i;
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		options = new JPanel();
		panel.add(options, c);
		c.gridy++;
		display = new JPanel();
		panel.add(display, c);
		
		if(inventories != null && inventories.size() != 0)
		{
			for(Inventory inventory: inventories)
			{
				options.add(new InventoryButton(inventory));
			}
			updateSelected(inventories.get(0));
			options.getComponent(0).setBackground(Color.LIGHT_GRAY);
		}
		
		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		this.setResizable(false);
		requestFocus(); //makes the frame get the focus
	}
	
	/**
	 * Replaces the inventory in the central panel with the new inventory.
	 * 
	 * @param i The inventory to display.
	 */
	private void updateSelected(Inventory i)
	{
		for(Component tab: options.getComponents()) {
			if (!i.equals(((InventoryButton) tab).getInventory())) {
				((InventoryButton) tab).setSelected(false);
			}
			else {
				((InventoryButton) tab).setSelected(true);
			}
			
			if (!((InventoryButton) tab).getSelected() && !i.equals(((InventoryButton) tab).getInventory())) {
				tab.setBackground(Color.WHITE);
			}
		}
		
		display.setVisible(false);
		selected = i;
		display.removeAll();
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
				Object name =JOptionPane.showInputDialog(display, "Enter the name of the new inventory.", "Add Inventory", JOptionPane.PLAIN_MESSAGE);
				if(name == null) return;
				Inventory i = new Inventory((String) name);
				for(; inventories.contains(i) && name != null; name = JOptionPane.showInputDialog(display, name + " is already an inventory. Enter a different"
						+ " name", "Add Inventory", JOptionPane.PLAIN_MESSAGE)) //Prompts the user for a name until they enter a valid option
				{
					i.setName((String) name);
				}
				if(name == null) return; //If the user chose to cancel after entering an invalid name
				inventories.add(i);
				options.add(new InventoryButton(i));
				updateSelected(i);
			}
		});
		inventory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		newMenu.add(inventory);
		
		JMenuItem item = new JMenuItem("Item..."); //Allows the addition of new items to the selected inventory
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				new AddFrame(selected, "");
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
		JMenuItem undo = new JMenuItem("Undo"); //Undoes the last operation
		undo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(Operation.canUndo()) Operation.undoLast();
			}
		}); 
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		edit.add(undo);
		
		JMenuItem redo = new JMenuItem("Redo"); //Redoes the last undone operation
		redo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(Operation.canRedo()) Operation.redoLast();
			}
		});
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
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
				
			}
		});
		removeInventory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		inventoryMenu.add(removeInventory);
		
		menu.add(inventoryMenu);
		
		this.setJMenuBar(menu);
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
					setBackground(Color.LIGHT_GRAY);
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
}