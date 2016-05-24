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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.mail.*;
import javax.mail.internet.*;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class GroceryListGUI extends JFrame {
	
	private static final Dimension SCROLL_PANEL_SIZE = new Dimension(200, 300);
	private List<Item> groceries;
	
	public GroceryListGUI(final Inventory inventory/*final List<Item> groceries*/) {
		super("Grocery List");
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {}
		
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
		
		//Adding export button
		//Able to export to either a Microsoft Word file (.docx) or a plain text file (.txt)
		c.gridy++;
		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inventory.exportListToTextFile();
			}
		});
		panel.add(export, c);
		
		//adding email button
		c.gridy++;
		JButton email = new JButton("Email");
		email.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inventory.exportListToEmail();
			}
		});
		panel.add(email, c);
		
		//adding print button
		c.gridy++;
		JButton print = new JButton("Print");
		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inventory.printList();
			}
		});
		panel.add(print, c);
		
		this.add(panel);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}
	
}
