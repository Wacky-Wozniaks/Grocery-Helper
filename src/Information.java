/**
 * Window to display information about the program or display legal/copyright information.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 5/29/2016
 */

import java.awt.Dimension;
import java.awt.Image;
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
	private JLabel logoLabel, caption;
	
	//Preferred width and height for windows
	private static final int WIDTH = 500;
	private static final int HEIGHT = 500;
	
	/**
	 * Instantiates a new Information window
	 * 
	 * @param window The information of the window to open
	 */
	public Information (int window) {
		super("Information");
		panel = new JPanel();
		add(panel);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		try {
			initText(window);
		} 
		catch (IOException e) {
			//Something went wrong
		}

		panel.add(scrollPanel);
		this.add(panel);
		
		panel.setVisible(true);
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
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		scrollPanel = new JScrollPane(textArea);
		if (window == ABOUT_GH) {
			setTitle("About GroceryHelper");
			textStream = getClass().getClassLoader().getResourceAsStream(INFORMATION_LOC);
			
			BufferedImage logo = ImageIO.read(getClass().getResource(LOGO_LOC));
			logoLabel = new JLabel();
			ImageIcon logoIcon = new ImageIcon(logo.getScaledInstance((int) (this.getPreferredSize().getWidth()/2),
					(int) (this.getPreferredSize().getHeight()/2), Image.SCALE_SMOOTH)); //Scale the logo itself
			logoLabel.setIcon(logoIcon);
			logoLabel.setVerticalAlignment(JLabel.CENTER);
			logoLabel.setHorizontalAlignment(JLabel.CENTER);
			panel.add(logoLabel);
			scrollPanel = new JScrollPane(textArea);
			scrollPanel.setPreferredSize(new Dimension(WIDTH - 20, 
							(int) (HEIGHT - logoLabel.getPreferredSize().getHeight() - 50))); //dimensions of scrollpane so they fit in the window
		}
		else if (window == ABOUT_LEGAL) {
			setTitle("Legal Information");
			caption = new JLabel("Usage of Third Party Libraries");
			textStream = getClass().getClassLoader().getResourceAsStream(LEGAL_LOC);
		}
		else if (window == ABOUT_LICENSE) {
			setTitle("EULA");
			caption = new JLabel("GroceryHelper™ License Agreement");
			textStream = getClass().getClassLoader().getResourceAsStream(LICENSE_LOC);
		}
		else if(window == ABOUT_UNINSTALL) {
			setTitle("Uninstall Information");
			caption = new JLabel("Uninstall Information");
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
		
		if (window != ABOUT_GH) {
			panel.add(caption);
			scrollPanel.setPreferredSize(new Dimension(WIDTH - 20, 
							(int) (HEIGHT - caption.getPreferredSize().getHeight() - 40)));
		}
		textArea.setText(text);
		textArea.setCaretPosition(0); //After adding text, scroll back to the top
		
	}
}