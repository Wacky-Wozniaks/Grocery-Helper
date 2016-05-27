/**
 * The GUI that has the Grocery List and allows exporting of the list.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/24/2016
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.mail.*;
import javax.mail.internet.*;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class GroceryListGUI extends JFrame {
	
	private static final Dimension SCROLL_PANEL_SIZE = new Dimension(200, 300);
	private List<Item> groceries;
	
	/**
	 * Constructs a GUI for the grocery list of the inventory.
	 * 
	 * @param inventory The inventory to make the grocery list for.
	 */
	public GroceryListGUI(final Inventory inventory) {
		super("Grocery List");
		
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
		
		groceries = inventory.getGroceryList();
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//Adding scroll panel with grocery list
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.CENTER;
		Box box = Box.createVerticalBox();
		for(Item i: groceries) box.add(new JLabel(i.getName() + " : " + i.amountToBuy()));
		JScrollPane scroll = new JScrollPane(box, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(SCROLL_PANEL_SIZE);
		panel.add(scroll, c);
		
		JPanel buttons = new JPanel();
		//Adding export button
		//Able to export to either a Microsoft Word file (.docx) or a plain text file (.txt)
		c.gridy++;
		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inventory.exportListToTextFile();
			}
		});
		buttons.add(export);
		
		//adding email button
		c.gridy++;
		JButton email = new JButton("Email");
		email.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inventory.exportListToEmail();
			}
		});
		buttons.add(email);
		
		//adding print button
		c.gridy++;
		JButton print = new JButton("Print");
		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inventory.printList();
			}
		});
		buttons.add(print);
		
		panel.add(buttons, c);
		//Adds all groceries to the inventory
		JButton add = new JButton("Add to Inventory");
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				for(Item i: groceries)
				{
					i.setQuantity(i.getMax());
				}
				dispose();
			}
		});
		c.gridy++;
		panel.add(add, c);
		
		this.add(panel);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}
	
}
