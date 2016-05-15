/**
 * The button and information panel for an item.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/15/2016
 */

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class ItemGUI extends JPanel
{
	private Item item;
	
	//Components for the panel.
	private JLabel name, quantity;
	
	//The popup frame and its components which need to be available to the panel.
	private JFrame frame;
	private JTextField min, max, amount;
	
	/**
	 * Creates the item's GUI components.
	 * @param item The item to create components for.
	 */
	public ItemGUI(Item item)
	{
		this.item = item;
		createItemFrame(); //Initializes the frame that will pop up if the panel is clicked on.
		
		name = new JLabel(item.getName());
		if(item.moreNeeded()) name.setForeground(Color.RED);
		add(name);
		add(Box.createHorizontalStrut(15));
		quantity = new JLabel("" + item.getQuantity());
		add(quantity);
		
		setBackground(Color.WHITE);
		setBorder(new LineBorder(Color.BLACK, 1, true));
		
		addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0)
			{
				frame.setVisible(true);
			}

			public void mouseEntered(MouseEvent arg0)
			{
				setBackground(Color.LIGHT_GRAY);
			}

			public void mouseExited(MouseEvent arg0)
			{
				setBackground(Color.WHITE);
			}

			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
			
		});
	}
	
	/**
	 * Creates a frame for an item.
	 * 
	 * @param item What the frame will display.
	 * @return The frame for the item.
	 */
	public void createItemFrame()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		panel.add(new JLabel(item.getName()), c);
		c.gridy++;
		
		JPanel updateQ = new JPanel();
		JButton plus = new JButton("+");
		amount = new JTextField("" + item.getQuantity(), 2);
		JButton minus = new JButton("-");
		plus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				item.updateQuantity(1);
				amount.setText("" + item.getQuantity());
				if(!item.moreNeeded()) name.setForeground(Color.BLACK);
				quantity.setText("" + item.getQuantity());
			}
		});
		minus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(item.getQuantity() != 0) item.updateQuantity(-1);
				else JOptionPane.showMessageDialog(frame, "Quantity cannot be below 0.", "Error", JOptionPane.ERROR_MESSAGE, null);
				amount.setText("" + item.getQuantity());
				if(item.moreNeeded()) name.setForeground(Color.RED);
				quantity.setText("" + item.getQuantity());
			}
		});
		updateQ.add(minus);
		updateQ.add(amount);
		updateQ.add(plus);
		panel.add(updateQ, c);
		
		c.gridy++;
		panel.add(Box.createVerticalStrut(10), c);
		c.gridy++;
		
		panel.add(new JLabel("Minimum Limit"), c);
		c.gridx++;
		min = new JTextField("" + item.getMin(), 2);
		panel.add(min, c);
		c.gridx = 0;
		c.gridy++;
		
		panel.add(new JLabel("Maximum Limit"), c);
		c.gridx++;
		max = new JTextField("" + item.getMax(), 2);
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
					item.setMin(Integer.parseInt(min.getText()));
					item.setMax(Integer.parseInt(max.getText()));
					if(item.moreNeeded()) name.setForeground(Color.RED);
					else name.setForeground(Color.BLACK);
				}
				catch(Throwable e)
				{
					JOptionPane.showMessageDialog(frame, "Enter an integer.", "Error", JOptionPane.ERROR_MESSAGE, null);
				}
			}
		});
		panel.add(update, c);
		
		frame = new JFrame();
		frame.add(panel);
		frame.setSize(300, 200);
	}
}