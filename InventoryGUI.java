/**
 * The GUI for an inventory.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/15/2016
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class InventoryGUI extends JFrame
{
	private Inventory inventory;
	private JTextField bar;
	private LinkedList<Item> groceries;
	private JPanel panel;
	
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
		
		JPanel searchBar = new JPanel();
		bar = new JTextField(15);
		JButton search = new JButton("Search");
		search.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				String text = bar.getText();
				if(text.equals("")) return;
				Item i = inventory.get(text);
				if(i != null) i.getGUI().showFrame();
				else JOptionPane.showMessageDialog(bar, text + " could not be found in this inventory.", "Item Not Found", JOptionPane.PLAIN_MESSAGE);
			}
		});
		searchBar.add(bar);
		searchBar.add(search);
		panel.add(searchBar, c);
		
		c.gridy++;
		Box b = Box.createVerticalBox();
		for(Item i: inventory.getInventory())
		{
			b.add(i.getGUI());
		}
		JScrollPane scroll = new JScrollPane(b, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(searchBar.getPreferredSize().width, 80));
		panel.add(scroll, c);
		
		JButton list = new JButton("Create Grocery List");
		list.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				groceries = inventory.getGroceryList();
				if(groceries.size() == 0)
				{
					JOptionPane.showMessageDialog(panel, "No groceries need to be purchased.", "No List Created", JOptionPane.PLAIN_MESSAGE);
					return;
				}
				Box items = Box.createVerticalBox();
				for(Item i: groceries)
				{
					items.add(new JLabel(i + " - " + i.amountToBuy()));
				}
				JButton update = new JButton("Add to Inventory");
				update.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						for(Item i: groceries)
						{
							i.setQuantity(i.getMax());
						}
					}
				});
				items.add(update);
				JFrame frame = new JFrame();
				frame.add(items);
				frame.pack();
				frame.setVisible(true);
			}
		});
		c.gridy++;
		panel.add(list, c);
		
		add(panel);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}