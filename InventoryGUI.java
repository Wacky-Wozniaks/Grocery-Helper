/**
 * The GUI for the project.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/12/2016
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InventoryGUI extends JFrame
{
	/**
	 * Creates a panel for an Item.
	 * 
	 * @param item What the panel will display.
	 * @return The panel for the item.
	 */
	public static JPanel createItemPanel(Item item)
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		JLabel name = new JLabel(item.getName());
		panel.add(name, c);
		c.gridy++;
		
		JTextField quantity = new JTextField("" + item.getQuantity(), 2);
		
		JPanel updateQ = new JPanel();
		JButton plus = new JButton("+");
		JTextField amount = new JTextField("" + item.getQuantity(), 2);
		JButton minus = new JButton("-");
		plus.addActionListener(new UpdateListener(true, item, amount));
		minus.addActionListener(new UpdateListener(false, item, amount));
		updateQ.add(minus);
		updateQ.add(amount);
		updateQ.add(plus);
		panel.add(updateQ, c);
		c.gridy++;
		
		panel.add(new JLabel("Minimum Limit"), c);
		c.gridx++;
		JTextField min = new JTextField("" + item.getMin(), 2);
		panel.add(min, c);
		c.gridx = 0;
		c.gridy++;
		
		panel.add(new JLabel("Maximum Limit"), c);
		c.gridx++;
		JTextField max = new JTextField("" + item.getMax(), 2);
		panel.add(max, c);
		c.gridx = 0;
		c.gridy++;
		
		JButton update = new JButton("Update");
		update.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					item.setQuantity(Integer.parseInt(quantity.getText()));
					item.setMin(Integer.parseInt(min.getText()));
					item.setMax(Integer.parseInt(max.getText()));
				}
				catch(Throwable e)
				{
					JOptionPane.showMessageDialog(panel, "Error", "Enter an integer.", JOptionPane.ERROR_MESSAGE, null);
				}
			}
		});
		panel.add(update, c);
		
		return panel;
	}
	
	/**
	 * A listener for the update quantity buttons.
	 */
	private static class UpdateListener implements ActionListener
	{
		private boolean plus;
		private Item item;
		private JTextField field;
		
		public UpdateListener(boolean plus, Item item, JTextField field)
		{
			this.plus = plus;
			this.item = item;
			this.field = field;
		}
		
		public void actionPerformed(ActionEvent arg0)
		{
			try
			{
				if(plus) item.updateQuantity(1);
				else item.updateQuantity(-1);
				field.setText("" + item.getQuantity());
			}
			catch(Throwable e)
			{
				JOptionPane.showMessageDialog(field, "Error", "Enter an integer.", JOptionPane.ERROR_MESSAGE, null);
			}
		}
	}
	
	public static void main(String[] args)
	{
		Item i = new Item("Apple", 5, 10, 7);
		JPanel panel = createItemPanel(i);
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}