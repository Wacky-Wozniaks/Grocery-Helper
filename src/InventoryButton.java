/**
 * A button for the menu bar in GUI to select an inventory.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 5/29/2016
 */

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class InventoryButton extends JPanel
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
				GUI.updateSelected(inventory);
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