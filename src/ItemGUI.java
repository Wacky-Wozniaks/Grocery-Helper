/**
 * The button and information panel for an item.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/27/2016
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.LineBorder;

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
		amount.addFocusListener(new FocusListener() //Updates the item information when the user finishes entering data
		{
			public void focusGained(FocusEvent arg0){}
			public void focusLost(FocusEvent arg0)
			{
				int quantity;
				try
				{
					quantity = Integer.parseInt(amount.getText());
				}
				catch(Throwable e)
				{
					JOptionPane.showMessageDialog(frame, "Amount must be an integer.", "Error", JOptionPane.ERROR_MESSAGE, null);
					amount.setText("" + item.getQuantity()); //Resets the the textfield to the old value
					return;
				}
				
				if(quantity < 0) //Ensures that the value is not negative
				{
					JOptionPane.showMessageDialog(frame, "Minimum limit cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE, null);
					min.setText("" + item.getMin()); //Resets the the textfield to the old value
					return;
				}
				
				item.setQuantity(quantity);
				if(item.moreNeeded()) name.setForeground(Color.RED);
				else name.setForeground(Color.BLACK);
			}
		});
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
		min.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent arg0){}
			public void focusLost(FocusEvent arg0) //When the user clicks out of the textfield, it updates the value in the item
			{
				int minimum;
				try
				{
					minimum = Integer.parseInt(min.getText());
				}
				catch(Throwable e)
				{
					JOptionPane.showMessageDialog(frame, "Minimum limit must be an integer.", "Error", JOptionPane.ERROR_MESSAGE, null);
					min.setText("" + item.getMin()); //Resets the the textfield to the old value
					return;
				}
				
				if(minimum < 0) //Ensures that the value is not negative
				{
					JOptionPane.showMessageDialog(frame, "Minimum limit cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE, null);
					min.setText("" + item.getMin()); //Resets the the textfield to the old value
					return;
				}
				
				if (item.getMax() < minimum) //Ensures that the max is not less than the min and if it isn't prompts the user for a new max value 
				{
					Object o = JOptionPane.showInputDialog(frame, "<html>The maximum limit must be greater than or equal to the minimum limit."
							+ "<br>Enter a new value for the maximum limit</html>", "", JOptionPane.ERROR_MESSAGE);
					
					for(int maximum = 0; maximum == 0; o = JOptionPane.showInputDialog(frame, "<html>Invalid value for maximum limit.<br>Enter a valid value."
							+ "</html>", "Set Maximum Limit", JOptionPane.ERROR_MESSAGE)) //Each time an invalid value is entered, prompts the user for a new one
					{
						if(o == null)
						{
							min.setText("" + item.getMin()); //If the user cancels, resets the textfield to the old min value
							return;
						}
					
						try
						{
							maximum = Integer.parseInt((String) o);
							
							//Tests for error values and if it finds them sets the value to 0
							if(maximum <= 0 || maximum < minimum) maximum = 0;
							else //Once a valid maximum has been entered, sets the value
							{
								item.setMax(maximum);
								break;
							}
						}
						catch(Throwable e){} //Error message is handled in for loop
					}
					
				}
				
				item.setMin(minimum);
				if(item.moreNeeded()) name.setForeground(Color.RED);
				else name.setForeground(Color.BLACK);
			}
		});
		panel.add(min, c);
		c.gridx = 0;
		c.gridy++;
		
		panel.add(new JLabel("Maximum Limit"), c);
		c.gridx++;
		max = new JTextField("" + item.getMax(), 2);
		max.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent arg0){}
			public void focusLost(FocusEvent arg0) //When the user clicks out of the textfield, it updates the value in the item
			{
				int maximum;
				try
				{
					maximum = Integer.parseInt(max.getText());
				}
				catch(Throwable e)
				{
					JOptionPane.showMessageDialog(frame, "Maximum limit must be an integer.", "Error", JOptionPane.ERROR_MESSAGE, null);
					max.setText("" + item.getMax()); //Resets the the textfield to the old value
					return;
				}
				
				if(maximum <= 0) //Ensures that the value is not negative
				{
					JOptionPane.showMessageDialog(frame, "Maximum limit cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE, null);
					max.setText("" + item.getMax()); //Resets the the textfield to the old value
					return;
				}
				
				if (maximum < item.getMin()) //Ensures that the max is not less than the min and if it isn't prompts the user for a new min value 
				{
					Object o = JOptionPane.showInputDialog(frame, "<html>The maximum limit must be greater than or equal to the minimum limit."
							+ "<br>Enter a new value for the minimum limit</html>", "", JOptionPane.ERROR_MESSAGE);
					
					for(int minimum = 0; minimum == 0; o = JOptionPane.showInputDialog(frame, "<html>Invalid value for minimum limit.<br>Enter a valid value."
							+ "</html>", "Set Minimum Limit", JOptionPane.ERROR_MESSAGE)) //Each time an invalid value is entered, prompts the user
					{
						if(o == null)
						{
							max.setText("" + item.getMax()); //If the user cancels, resets the textfield to the old max value
							return;
						}
					
						try
						{
							minimum = Integer.parseInt((String) o);
							
							//Tests for error values and if it finds them sets the value to 0
							if(minimum < 0 || maximum < minimum) minimum = 0;
							else //Once a valid minimum has been entered, sets the value
							{
								item.setMin(minimum);
								if(item.moreNeeded()) name.setForeground(Color.RED);
								else name.setForeground(Color.BLACK);
								break;
							}
						}
						catch(Throwable e){} //Error message is handled in for loop
					}
				}
				
				item.setMax(maximum);
			}
		});
		panel.add(max, c);
		c.gridx = 0;
		
		c.gridy++;
		JButton remove = new JButton("Remove Item");
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				int merge = JOptionPane.showConfirmDialog(frame, "Are you sure you wish to remove the item " + item.getName() + "?", "Remove", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				
				if(merge == JOptionPane.YES_OPTION) {
					MasterInventory.remove(item);	
				}
				else {
					return;
				}
				
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