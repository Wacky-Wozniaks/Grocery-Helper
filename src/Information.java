/**
 * Window to display information about the program or display legal/copyright information.
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 5/29/2016
 */

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
		/*
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
		 */

		super("Information");
		panel = new JPanel();
		add(panel);

		textArea = new JTextArea();
		textArea.setColumns(20);
		textArea.setLineWrap(true);
		textArea.setRows(5);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);



		try {
			initText(window);
		} catch (IOException e) {}


		scrollPanel = new JScrollPane(textArea);
		System.out.println("FINAL TEXT AREA TEXT: " + textArea.getText());
		panel.add(scrollPanel);
		this.add(panel);


		/*
			//Open window that corresponds to what window was prompted
			if (window == ABOUT_GH) {
				initGH();
			}
			else if (window == ABOUT_LEGAL) {
				initLegal();
			}
			else if (window == ABOUT_LICENSE) {
				initLicense();
			}
			else { //If none of these selections, something went wrong
				throw new IllegalArgumentException();
			}
		 */

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Initializes text conttent
	 * @throws IOException 
	 */
	private void initText(int window) throws IOException {
		System.out.println("inittext");
		//Open window that corresponds to what window was prompted
		if (window == ABOUT_GH) {
			System.out.println("about gh");
			setTitle("About GroceryHelper");
			add(new JLabel("About GroceryHelper"));

			String line = "";
			try {
				FileReader reader = new FileReader(INFORMATION_LOC);
				BufferedReader buffer = new BufferedReader(reader);

				while (line != null) {
					//System.out.println("appending " + line);
					textArea.append(line + "\n");
					line = buffer.readLine();
					//System.out.println("content " + textArea.getText());
				}
				buffer.close();
			}
			catch (IOException e) {
				e.printStackTrace();
				throw new IOException();
			}
		}
		else if (window == ABOUT_LEGAL) {
			setTitle("Legal Information");
			add(new JLabel("Legal Information"));

			try {
				InputStream in = getClass().getResourceAsStream(LEGAL_LOC);
				textArea.read(new InputStreamReader(in), null);
			}
			catch (IOException e) {
				throw new IOException();
			}
		}
		else if (window == ABOUT_LICENSE) {
			setTitle("End-User License Agreement");
			add(new JLabel("Legal Information"));

			try {
				InputStream in = getClass().getResourceAsStream(LICENSE_LOC);
				textArea.read(new InputStreamReader(in), null);
			}
			catch (IOException e) {
				throw new IOException();
			}
		}
		else { //If none of these selections, something went wrong
			throw new IllegalArgumentException();
		}
	}
}