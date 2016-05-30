/**
 * Represents the email exporting methods.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/25/2016
 */

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Email {
	private static String pw;
	/**
	 * Exports the grocery list of the inventory and sends it as an email.
	 */
	public static void exportListToEmail(String listString) {
		JDialog sending;
		try {
			String emailAddr = (String) JOptionPane.showInputDialog(GUI.getGUI(), "Enter your email address:",
					"Email My Shopping List", JOptionPane.PLAIN_MESSAGE);
			sending = new JDialog((JFrame) null, "Grocery List", false);
			sending.add(new JLabel("Sending..."));
			sending.setPreferredSize(new Dimension(300, 150));
			sending.pack();
			sending.setVisible(true);
			
			if (!isValidEmail(emailAddr)) { //Checks syntactical validity of email address
				sending.dispose();
				throw new IllegalArgumentException();
			}
			
			if (!sendEmail(emailAddr, listString)) { //Attempts to send the email
				sending.dispose();
				sending.setVisible(false);
				throw new RuntimeException(); //If email doesn't go through, throw an exception
			}
			else { //Confirms successful sending
				sending.dispose();
				JOptionPane.showMessageDialog(GUI.getGUI(), "Email sent!", "Emailed list", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		} 
		catch (IllegalArgumentException e1) { //Error message for invalid email address
			JOptionPane.showMessageDialog(GUI.getGUI(), "Invalid email address!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (Throwable e2) { //Error message for all other sending failures
			JOptionPane.showMessageDialog(GUI.getGUI(), "Sending failed!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	/**
	 * Attempts to send an email, returns whether or not such attempt succeeded.
	 * Exception thrown if something goes wrong; these are caught elsewhere.
	 * 
	 * @param emailAddr The email address the list will be sent to.
	 * @return True if email sent, false if not.
	 */
	private static boolean sendEmail(String emailAddr, String listString) {
		//Gmail connection configurations
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		//Load local save file that contains the super secret password to the Gmail account
		pw = null;
		InputStream email = Email.class.getClassLoader().getResourceAsStream("email.compsci");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(email));
			pw = reader.readLine();
			System.out.println("password is " + pw);
			email.close();
		}
		catch (IOException e1) {
			return false; //Error in reading password file
		}
		catch (NullPointerException e2) {
			return false; //Error in reading password file
		}

		//Logs in to the account with given authenticaion information
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("MXShoppingList", pw);
			}
		});

		//Builds the contents of the email and tries to send it
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("MXShoppingList@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(emailAddr));
			message.setSubject("Your Shopping List");
			message.setText(listString);

			Transport.send(message);
		}
		catch (Throwable e) {
			return false;
		}

		return true;
	}
	
	/**
	 * Checks if a given string is a valid email address (syntactically) according to RFC822 standards
	 * 
	 * @param s The email to see if syntactically valid.
	 * @return True if valid, false if invalid (through a caught exception)
	 */
	private static boolean isValidEmail(String s) {
		try {
			InternetAddress email = new InternetAddress(s);
			email.validate(); //throws exception if syntactically invalid
		}
		catch (Throwable e) {
			return false;
		}
		return true;
	}
	
}
