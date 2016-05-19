/**
 * The GUI that has the Grocery List and allows exporting of the list.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/15/2016
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class GroceryListGUI extends JFrame {
	
	private static final Dimension SCROLL_PANEL_SIZE = new Dimension(200, 300);
	
	public GroceryListGUI(final List<Item> groceries) {
		super("Grocery List");
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {}
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//adding scroll panel with grocery list
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.CENTER;
		Box box = Box.createVerticalBox();
		for(Item i: groceries) box.add(new JLabel(i.getName() + " : " + i.amountToBuy()));
		JScrollPane scroll = new JScrollPane(box, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(SCROLL_PANEL_SIZE);
		panel.add(scroll, c);
		
		//adding export button
		c.gridy++;
		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				//chooser.addChoosableFileFilter(new FileNameExtensionFilter("Microsoft Word Document (.docx)","docx"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Plain Text Document (.txt)","txt"));
				int option = chooser.showSaveDialog(null);
				if(option == JFileChooser.APPROVE_OPTION) {
					PrintWriter writer = null;
					try {
						File file = chooser.getSelectedFile();
						if(file.getName().lastIndexOf(".txt") != file.getName().length() - 4) {
							JOptionPane.showMessageDialog(null, "\".txt\" entension required!", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						file.createNewFile();
						writer = new PrintWriter(file);
						for(Item i: groceries) writer.println(i.getName() + " : " + i.amountToBuy());
						writer.close();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error in Saving", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});
		panel.add(export, c);
		
		this.add(panel);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}
	
}
