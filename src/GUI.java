/**
 * The main GUI for the project.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/23/2016
 */

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

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
		try
		{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
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
		}
		
		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		this.setResizable(false);
	}
	
	/**
	 * Replaces the inventory in the central panel with the new inventory.
	 * 
	 * @param i The inventory to display.
	 */
	private void updateSelected(Inventory i)
	{
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
		JMenuItem inventory = new JMenuItem("Inventory"); //Allows another item to be added to the inventory
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
		newMenu.add(inventory);
		newMenu.add(new JMenuItem("Item"));
		file.add(newMenu);
		file.add(new JMenuItem("Export"));
		file.add(new JMenuItem("Print"));
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
		edit.add(undo);
		JMenuItem redo = new JMenuItem("Redo"); //Redoes the last undone operation
		redo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(Operation.canRedo()) Operation.redoLast();
			}
		});
		edit.add(redo);
		menu.add(edit);
		
		this.setJMenuBar(menu);
	}
	
	/**
	 * A button for the menu bar to select an inventory.
	 */
	private class InventoryButton extends JPanel
	{
		private Inventory inventory;
		
		/**
		 * Constructs the panel.
		 * 
		 * @param i The inventory to be selected with this panel.
		 */
		public InventoryButton(Inventory i)
		{
			inventory = i;
			setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1, true), new EmptyBorder(2, 2, 2, 2)));
			setBackground(Color.WHITE);
			add(new JLabel(i.getName()));
			addMouseListener(new MouseListener()
			{
				public void mouseClicked(MouseEvent arg0)
				{
					updateSelected(inventory);
				}
				
				public void mouseEntered(MouseEvent arg0)
				{
					setBackground(Color.LIGHT_GRAY);
				}
				
				public void mouseExited(MouseEvent arg0)
				{
					setBackground(Color.WHITE);
				}
				
				public void mousePressed(MouseEvent arg0){}
				public void mouseReleased(MouseEvent arg0){}
			});
		}
	}
}