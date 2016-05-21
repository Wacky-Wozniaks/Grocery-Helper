/**
 * The GUI that has the Grocery List and allows exporting of the list.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/15/2016
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
import java.io.IOException;
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
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.*;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;



public class GroceryListGUI extends JFrame {
	
	private static final Dimension SCROLL_PANEL_SIZE = new Dimension(200, 300);
	private String pw;
	private String listString;
	
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
		
		//create String that represents a readable representation of the shopping list
		listString = "Your shopping list generated at ";
		listString += new SimpleDateFormat("h:mm a 'on' MM/dd/yyyy").format(new java.util.Date());
		listString += ":\n";
		for (Item i: groceries) {
			listString += i.getName() + " : " + i.amountToBuy() + "\n";
		}
		
		//adding export button
		c.gridy++;
		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Microsoft Word Document (.docx)","docx"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Plain Text Document (.txt)","txt"));
				chooser.setAcceptAllFileFilterUsed(false);
				int option = chooser.showSaveDialog(null);
				if(option == JFileChooser.APPROVE_OPTION) {
					PrintWriter writer = null;
					try {
						File file = chooser.getSelectedFile();
						if (file.getName().lastIndexOf(".docx") == file.getName().length() - 5) {
							writeToDocx(listString, file);
							return; //if an exception is thrown, it will be caught below and display an error message
							
						}
						else if(file.getName().lastIndexOf(".txt") != file.getName().length() - 4) { //if NOT docx NOR txt export
							JOptionPane.showMessageDialog(null, "\".txt\" or \".docx\" extension required!", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						//write text file
						file.createNewFile();
						writer = new PrintWriter(file);
						for(Item i: groceries) writer.println(i.getName() + " : " + i.amountToBuy());
						writer.close();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "Error in Saving", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
		});
		panel.add(export, c);
		
		//adding email button
		c.gridy++;
		JButton email = new JButton("Email");
		email.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//JOptionPane sending;
				JDialog sending;
				try {
					String emailAddr = (String) JOptionPane.showInputDialog(null, "Enter your email address:",
							"Email My Shopping List", JOptionPane.PLAIN_MESSAGE);
					sending = new JDialog((JFrame) null, "Grocery List", false);
					sending.add(new JLabel("Sending..."));
					sending.setLocationRelativeTo(null);
					sending.setPreferredSize(new Dimension(300, 150));
					sending.pack();
					sending.setVisible(true);
					if (!isValidEmail(emailAddr)) {
						sending.dispose();
						throw new IllegalArgumentException();
					}
					
					
					if (!sendEmail(emailAddr, listString)) { //attempt to send the email
						sending.dispose();
						throw new RuntimeException(); //if email doesn't go through, throw an exception
					}
					else {
						sending.dispose();
						JOptionPane.showMessageDialog(null, "Email sent!", "Emailed list", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					
				} 
				catch (RuntimeException e1) { //two different error messages for different cases
					JOptionPane.showMessageDialog(null, "Sending failed!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				catch (Throwable e2) {
					JOptionPane.showMessageDialog(null, "Invalid email address!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		panel.add(email, c);
		
		
		//adding email button
		c.gridy++;
		JButton print = new JButton("Print");
		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PrinterJob job = PrinterJob.getPrinterJob();
					
					job.setPrintable(new PrintedPage(listString, job));
					
					if (job.printDialog()) {
						job.print();
						JOptionPane.showMessageDialog(null, "Sent to printer!", "Printer Job Sent", JOptionPane.INFORMATION_MESSAGE);
						return;
						
					}
				} 
				catch (Throwable e1) {
					JOptionPane.showMessageDialog(null, "Print failed!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		panel.add(print, c);
		
		this.add(panel);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}
	
	//attempts to send an email, returns whether or not such attempt succeeded
	private boolean sendEmail(String emailAddr, String list) {
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		
		File file = new File("email.compsci");
		pw = "";
		try {
			Scanner scan = new Scanner(file);
			pw = scan.nextLine();
		}
		catch (Throwable e) {
			JOptionPane.showMessageDialog(null, "Sending failed!", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("MXShoppingList", pw);
				}
		});

		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("MXShoppingList@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailAddr));
			message.setSubject("Your Shopping List");
			message.setText(list);

			Transport.send(message);
		}
		catch (Throwable e) {
			return false;
		}
		
		return true; 
		

	}
	
	//checks if a given string is a valid email address
	private boolean isValidEmail(String s) {
		try {
			InternetAddress email = new InternetAddress(s);
			email.validate();
		}
		catch (Throwable e) {
			return false;
		}
		return true;
	}
	
	//Reference: https://github.com/plutext/docx4j/blob/master/src/samples/docx4j/org/docx4j/samples/NewDocxHelloWorld.java
	//attempts to write the given shopping list to a Microsoft Word (.docx) file
	private void writeToDocx(String list, File file) throws Exception {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		MainDocumentPart mdp = pkg.getMainDocumentPart();
		
		for (String line : list.split("\n")) {
			mdp.addParagraphOfText(line);
		}
		
		
		
		Docx4J.save(pkg, file, Docx4J.FLAG_SAVE_ZIP_FILE);
		
	}
	
	private class PrintedPage implements Printable {

		private String printString;
		private PrinterJob job;
		
		//Letter size paper, units in inches
		private final double PAPERWIDTH = 8.5;
		private final double PAPERHEIGHT = 11;
		
		//1 inch = 72 pixels
		private final double INCHTOPIXELS = 72; 
		
		public PrintedPage(String printString, PrinterJob job) {
			this.printString = printString;
			this.job = job;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
		 */
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
				throws PrinterException {
			
			
			
			PageFormat format = job.defaultPage();
			Paper paper = new Paper();
			
			paper.setSize(PAPERWIDTH * INCHTOPIXELS, PAPERHEIGHT * INCHTOPIXELS); //72 pixels per inch
			
			//1 inch margins
			paper.setImageableArea(INCHTOPIXELS, INCHTOPIXELS, paper.getWidth() - INCHTOPIXELS*2,
					paper.getHeight() - INCHTOPIXELS*2);
			format.setPaper(paper);
			
			job.setPrintable(this, format);
			
			
			if (pageIndex != 0) {
				return NO_SUCH_PAGE;
			}
			
			Graphics2D content = (Graphics2D) graphics;
			content.setFont(new Font("Serif", Font.PLAIN, 14));
			content.setPaint(Color.black);
			
			
			//what the parameters are (for drawString inside the for loop):
			//drawString(ONE LINE, LEFT MARGIN (1 INCH), VERTICAL MARGIN INCREMENTED EACH LINE
			int vertMargin = (int) INCHTOPIXELS;
			for (String line : printString.split("\n")) {
		        content.drawString(line, (int) INCHTOPIXELS, vertMargin+= content.getFontMetrics().getHeight());
			}
			
			return PAGE_EXISTS;
		}
		
	}
	
	
	
}
