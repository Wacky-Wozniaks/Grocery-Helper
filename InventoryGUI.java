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

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
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
		JPanel panel = new JPanel(new GridBagLayout());
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
				for(Item i : inventory.getInventory())
				{
					if(i.getName().equalsIgnoreCase(text))
					{
						i.getGUI().showFrame();
						return;
					}
				}
				JOptionPane.showMessageDialog(bar, text + " could not be found in this inventory.", "Item Not Found", JOptionPane.PLAIN_MESSAGE);
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
		
		add(panel);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}