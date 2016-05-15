/**
 * The main class for the project.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/14/2016
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

//import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Controller
{
	/**
	 * Runs the program.
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch(Throwable e){}
		Item i = new Item("Apple", 5, 10, 7);
		JPanel panel = InventoryGUI.createItemPanel(i);
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}