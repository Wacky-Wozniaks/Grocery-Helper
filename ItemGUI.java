/**
 * The button and information panel for an item.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/20/2016
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class ItemGUI extends JPanel
{
	private Item item;
	
	//Components for the panel.
	private JLabel name, quantity;
	private JPanel namePanel, quantityPanel;
	
	//The popup frame and its components which need to be available to the panel.
	private JFrame frame;
	private JTextField min, max, amount;
	
	/**
	 * Creates the item's GUI components.
	 * @param item The item to create components for.
	 */
	public ItemGUI(Item item)
	{
		try
		{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch(Throwable e){}
		this.item = item;
		createItemFrame(); //Initializes the frame that will pop up if the panel is clicked on.
		
		setLayout(new BorderLayout());
		name = new JLabel(item.getName());
		if(item.moreNeeded()) name.setForeground(Color.RED);
		namePanel = new JPanel();
		namePanel.add(Box.createHorizontalStrut(5));
		namePanel.add(name);
		namePanel.setBackground(Color.WHITE);
		add(namePanel, BorderLayout.WEST);
		
		quantity = new JLabel("" + item.getQuantity());
		quantityPanel = new JPanel();
		quantityPanel.add(quantity);
		quantityPanel.add(Box.createHorizontalStrut(5));
		quantityPanel.setBackground(Color.WHITE);
		add(quantityPanel, BorderLayout.EAST);
		
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
				namePanel.setBackground(Color.LIGHT_GRAY);
				quantityPanel.setBackground(Color.LIGHT_GRAY);
			}

			public void mouseExited(MouseEvent arg0)
			{
				setBackground(Color.WHITE);
				namePanel.setBackground(Color.WHITE);
				quantityPanel.setBackground(Color.WHITE);
			}

			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
			
		});
	}
	
	/**
	 * Updates the displayed quantity of the item within the GUI components.
	 */
	public void updateQuantity()
	{
		quantity.setText("" + item.getQuantity());
		amount.setText("" + item.getQuantity());
		if(item.moreNeeded()) name.setForeground(Color.RED);
		else name.setForeground(Color.BLACK);
		min.setText(item.getMin() + "");
		max.setText(item.getMax() + "");
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
		
		JLabel n = new JLabel(item.getName());
		Font font = n.getFont();
		n.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		panel.add(n, c);
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
					int minimum = Integer.parseInt(min.getText());
					int maximum = Integer.parseInt(max.getText());
					
					if (maximum < minimum) {
						JOptionPane.showMessageDialog(frame, "The Maximum Limit must be greater than or equal to the Minimum Limit.", "", JOptionPane.ERROR_MESSAGE, null);
						return;
					}
					
					item.setMin(Integer.parseInt(min.getText()));
					item.setMax(Integer.parseInt(max.getText()));
					if(item.moreNeeded()) name.setForeground(Color.RED);
					else name.setForeground(Color.BLACK);
					frame.setVisible(false);
				}
				catch(Throwable e)
				{
					JOptionPane.showMessageDialog(frame, "Enter an integer.", "Error", JOptionPane.ERROR_MESSAGE, null);
				}
			}
		});
		panel.add(update, c);
		
		c.gridy++;
		JButton remove = new JButton("Remove Item");
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				item.getInventory().remove(item);
				frame.dispose();
			}
		});
		panel.add(remove, c);
		
		frame = new JFrame();
		frame.add(panel);
		frame.setResizable(false);
		frame.setSize(300, 225);
	}
	
	/**
	 * Sets the frame for the item visible.
	 */
	public void showFrame()
	{
		frame.setVisible(true);
	}
}