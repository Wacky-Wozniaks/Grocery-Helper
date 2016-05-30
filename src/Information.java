/**
 * Window to display information about the program or display legal/copyright information.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 5/29/2016
 */

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Information extends JFrame {
	//Constants denoting which window to open
	public static final int ABOUT_GH = 1;
	public static final int ABOUT_LEGAL = 2;
	public static final int ABOUT_LICENSE = 3;
	public static final int ABOUT_UNINSTALL = 4;

	//Location of each file
	private static final String INFORMATION_LOC = "INFORMATION.info";
	private static final String LEGAL_LOC = "LEGAL.info";
	private static final String LICENSE_LOC = "LICENSE.info";
	private static final String LOGO_LOC = "LOGO.png";
	private static final String UNINSTALL_LOC= "UNINSTALL.info";

	//Essential components of the window
	private JPanel panel;
	private JTextArea textArea;
	private JScrollPane scrollPanel;
	private JLabel textLabel; //Only used for About GroceryHelper window
	
	//Preferred width and height for windows (one-third of screen width, one-half of screen height, respectively)
	//The HEIGHT field is not used for the About GroceryHelper window
	private static final int WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 4);
	private static final int HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
	
	/**
	 * Instantiates a new Information window
	 * 
	 * @param The information of the window to open
	 * @throws IOException 
	 */
	public Information (int window) {
		super("Information");
		panel = new JPanel();
		add(panel);

		textLabel = new JLabel();

		try {
			initText(window);
		} 
		catch (IOException e) {
			//Something went wrong
		}

		if (window == ABOUT_GH) {
			panel.add(textLabel);
		}
		else {
			panel.add(scrollPanel);
		}
		textLabel.setVerticalAlignment(JLabel.CENTER);
		this.add(panel);
		
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Initializes text conttent
	 * @throws IOException 
	 */
	private void initText(int window) throws IOException {
		InputStream textStream; //Stream and Buffer of text to feed into a String, taken from a file with a given location
		BufferedReader buffer;
		
		//Open window that corresponds to what window was prompted
		if (window == ABOUT_GH) {
			setTitle("About GroceryHelper");
			add(new JLabel("About GroceryHelper"));
			textStream = getClass().getClassLoader().getResourceAsStream(INFORMATION_LOC);
			
			//Load text into the text area
			String text = "<html><p style=\"width:" + (int) (this.getPreferredSize().getWidth()*2) +
					"px; font-size:11px;\">";
			BufferedImage logo = ImageIO.read(getClass().getResource(LOGO_LOC));
			JLabel logoLabel = new JLabel();
			int sideLength = (int) (this.getPreferredSize().getWidth()*1.33); //Side length of square "logo box"
			logoLabel.setPreferredSize(new Dimension(sideLength, sideLength)); //Logo's width and height is the width of the window
			ImageIcon logoIcon = new ImageIcon(logo.getScaledInstance(logoLabel.getPreferredSize().width,
					logoLabel.getPreferredSize().height, Image.SCALE_SMOOTH)); //Scale the logo itself
			logoLabel.setIcon(logoIcon);
			logoLabel.setVerticalAlignment(JLabel.CENTER);
			logoLabel.setHorizontalAlignment(JLabel.CENTER);
			panel.add(logoLabel);
			
			String line = "";
			buffer = new BufferedReader(new InputStreamReader(textStream));
			while (line != null) {
				line = buffer.readLine();
				if (line != null) {
					text += (line + "<br>"); //Build the entire String, line by line
				}
			}
			text += "</p></html>";
			textLabel.setText(text);
			this.setPreferredSize(new Dimension((int) (this.getPreferredSize().getWidth()*4), (int) textLabel.getPreferredSize().getHeight() + 40));
		}
		else { //If one of other two windows opened
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			
			textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);
			
			if (window == ABOUT_LEGAL) {
				setTitle("Legal Information");
				add(new JLabel("Legal Information"));
				textStream = getClass().getClassLoader().getResourceAsStream(LEGAL_LOC);
			}
			else if (window == ABOUT_LICENSE) {
				setTitle("End User License Agreement");
				add(new JLabel("Legal Information"));
				textStream = getClass().getClassLoader().getResourceAsStream(LICENSE_LOC);
			}
			else if(window == ABOUT_UNINSTALL) {
				setTitle("Uninstall Information");
				add(new JLabel("Uninstall Information"));
				textStream = getClass().getClassLoader().getResourceAsStream(UNINSTALL_LOC);
			}
			else { //If none of these selections, something went wrong
				throw new IllegalArgumentException();
			}
			
			String text = "";
			String line = "";
			buffer = new BufferedReader(new InputStreamReader(textStream));
			while (line != null) {
				line = buffer.readLine();
				if (line != null) {
					text += line + "\n"; //Build the entire String, line by line
				}
			}
			
			textArea.setText(text);
			textArea.setCaretPosition(0); //After adding text, scroll back to the top
			scrollPanel = new JScrollPane(textArea);
			scrollPanel.setPreferredSize(new Dimension((int) this.getPreferredSize().getWidth(), 
							(int) this.getPreferredSize().getHeight()));
		}
	}
}