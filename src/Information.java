/**
 * Window to display information about the program or display legal/copyright information.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 5/29/2016
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class Information extends JFrame {
	//Location of each information file
	private static final String INFORMATION_LOC = "resources/INFORMATION.info";
	private static final String LEGAL_LOC = "resources/LEGAL.info";
	private static final String LICENSE_LOC = "resources/LICENSE.info";

	//Constants to denote which window to open
	public static final int ABOUT_GH = 1;
	public static final int ABOUT_LEGAL = 2;
	public static final int ABOUT_LICENSE = 3;

	//Essential components of the window
	private JPanel panel;
	private JScrollPane scrollPanel;
	private JTextArea textArea;

	/**
	 * Instantiates a new Information window
	 * 
	 * @param The information of the window to open
	 */
	public Information (int window) {
		super("Information");
		panel = new JPanel();
		add(panel);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		

		try {
			initText(window);
		} catch (IOException e) {}

		scrollPanel = new JScrollPane(textArea);
		panel.add(scrollPanel);
		this.add(panel);
		
		this.setResizable(false);
		this.setPreferredSize(new Dimension(300, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)));
		this.pack();
		scrollPanel.setPreferredSize(new Dimension((int) this.getPreferredSize().getWidth() - 15, 
				(int) this.getPreferredSize().getHeight() - 35));
		this.setVisible(true);
	}

	/**
	 * Initializes text conttent
	 * @throws IOException 
	 */
	private void initText(int window) throws IOException {
		FileReader reader = null;
		
		//Open window that corresponds to what window was prompted
		if (window == ABOUT_GH) {
			setTitle("About GroceryHelper");
			add(new JLabel("About GroceryHelper"));
			reader = new FileReader(INFORMATION_LOC);
		}
		else if (window == ABOUT_LEGAL) {
			setTitle("Legal Information");
			add(new JLabel("Legal Information"));
			reader = new FileReader(LEGAL_LOC);
		}
		else if (window == ABOUT_LICENSE) {
			setTitle("End-User License Agreement");
			add(new JLabel("Legal Information"));
			reader = new FileReader(LICENSE_LOC);
		}
		else { //If none of these selections, something went wrong
			throw new IllegalArgumentException();
		}
		
		
		//Load text into the text area
		BufferedReader buffer = new BufferedReader(reader);
		String line = "";
		try {
			while (line != null) {
				textArea.append(line + "\n");
				line = buffer.readLine();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IOException();
		}
		reader.close();
		buffer.close();
	}
}