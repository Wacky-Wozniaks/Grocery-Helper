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



public class GroceryListGUI extends JFrame {
	
	private static final Dimension SCROLL_PANEL_SIZE = new Dimension(200, 300);
	private String pw;
	
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
		
		//adding email button
		c.gridy++;
		JButton email = new JButton("Email");
		email.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String emailAddr = (String) JOptionPane.showInputDialog(null, "Enter your email address:", "Email My Shopping List", JOptionPane.PLAIN_MESSAGE);
					if (!isValidEmail(emailAddr)) {
						throw new IllegalArgumentException();
					}
					
					String list = "Your shopping list generated at ";
					list += new SimpleDateFormat("h:mm a 'on' MM/dd/yyyy").format(new java.util.Date());
					list += ":\n";
					for (Item i: groceries) {
						list += i.getName() + " : " + i.amountToBuy() + "\n";
					}
					
					if (!sendEmail(emailAddr, list)) { //attempt to send the email here
						throw new RuntimeException();
					}
					else {
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
					
					
					String list = "Your shopping list generated at ";
					list += new SimpleDateFormat("h:mm a 'on' MM/dd/yyyy").format(new java.util.Date());
					list += ":\n";
					for (Item i: groceries) {
						list += i.getName() + " : " + i.amountToBuy() + "\n";
					}
					
					PrinterJob job = PrinterJob.getPrinterJob();
					
					job.setPrintable(new PrintedPage(list, job));
					
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
	
	//attempts to send an email, returns the success of such attempt
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
			paper.setImageableArea(INCHTOPIXELS, INCHTOPIXELS, paper.getWidth() - INCHTOPIXELS*2, paper.getHeight() - INCHTOPIXELS*2);
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
