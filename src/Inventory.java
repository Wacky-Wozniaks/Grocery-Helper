/**
 * Represents the inventory in a binary search tree
 * 
 * @author Julia McClellan, Luke Giacalone, Hyun Choi
 * @version 05/28/2016
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class Inventory extends Observable {
	private static String inventoryFileLoc = System.getProperty("user.home");
	private static final String EXTENSION = ".inventory";
	
	private TreeMap<String, Item> inventory;
	private String name;
	private File file;
	private InventoryGUI gui;
	private Properties props;
	
	/**
	 * Represents an inventory of items initialized with the given items.
	 * 
	 * @param name The name of the inventory
	 * @param items The items to be added to the inventory
	 */
	public Inventory(String name, Collection<Item> items) {
		
		inventory = new TreeMap<String, Item>();
		for(Item i: items)
		{
			inventory.put(i.getName().toLowerCase(), i);
		}
		this.name = name;
	}
	
	/**
	 * Represents an inventory of items.
	 * 
	 * @param name The name of the inventory
	 */
	public Inventory(String name) {
		inventory = new TreeMap<String, Item>();
		this.name = name;
	}
	
	/**
	 * Creates a grocery list based on what items are below the minimum threshold
	 * 
	 * @return The grocery list
	 */
	public LinkedList<Item> getGroceryList() {
		LinkedList<Item> list = new LinkedList<Item>();
		for(Item i: inventory.values())
			if(i.moreNeeded()) list.add(i);
		return list;
	}
	
	/**
	 * Returns the name of the inventory
	 * 
	 * @return the name of the inventory
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the inventory
	 * 
	 * @param name The new name of the inventory
	 */
	public void setName(String name) {
		this.name = name;
		File newFile = new File(inventoryFileLoc);
		if(file.exists()) file.renameTo(newFile);
		file = newFile;
	}
	
	/**
	 * Exports this inventory to a file
	 * 
	 * @throws IOException if the file cannot be created
	 */
	public void exportInventory() throws IOException {
		FileOutputStream output;
		try {
			output = new FileOutputStream(inventoryFileLoc);
		}
		catch (FileNotFoundException e) {
			updateFilePath();
		}
		finally {
			output = new FileOutputStream(inventoryFileLoc);
		}
		
		for(Map.Entry<String, Item> item: inventory.entrySet()) {
			Item i = item.getValue();
			String itemName = i.getName();
			String itemProp = + i.getMin() + "," + i.getMax() + "," + i.getQuantity() + "," 
					+ i.getCode();
			props.setProperty(itemName, itemProp);
			props.store(output, "--Saved Inventory " + itemName + "--");
		}
		output.close();
	}
	
	/**
	 * Imports an inventory from a file
	 * 
	 * @param inventory Name of the inventory
	 * @throws FileNotFoundException if the inventory file cannot be found
	 */
	public void importInventory() throws FileNotFoundException {
		updateFilePath();
		InputStream in;
		
		in = new FileInputStream(inventoryFileLoc);
		try {
			props.load(in);
			in.close();
			for (Entry<Object, Object> entry: props.entrySet()) {
				String[] attributes = ((String) (entry.getValue())).split(",");
				inventory.put((String) entry.getKey(), new Item((String) entry.getKey(),
						Integer.parseInt(attributes[0]), Integer.parseInt(attributes[1]),
						Integer.parseInt(attributes[2]), Integer.parseInt(attributes[3]),
						this));
			}
			
			/*if (inventory.equals("")) { //If no inventories exist, don't do anything
				return;
			}*/
		} 
		catch (IOException e2) {
			//Something went wrong...
			e2.printStackTrace();
		}
	}
	
	/**
	 * Updates the file path for the location of the properties file depending
	 * on the operating system.
	 */
	private void updateFilePath() {
		props = new Properties();
		
		if (System.getProperty("os.name").contains("Mac")) {
			inventoryFileLoc += File.separator + "Library" + File.separator + "WackyWozniaks" + File.separator;
		}
		else {
			inventoryFileLoc += File.separator + "WackyWozniaks" + File.separator;
		}
		inventoryFileLoc += name + EXTENSION;
		
		File file = new File(inventoryFileLoc);
		file.getParentFile().mkdirs();
	}
	
	/**
	 * Returns the TreeSet of the inventory.
	 * 
	 * @return The inventory represented as a TreeSet.
	 */
	public Collection<Item> getInventory()
	{
		return inventory.values();
	}
	
	/**
	 * Adds the item to the inventory.
	 * 
	 * @param item The item to be added.
	 */
	public boolean add(Item item)
	{
		if(inventory.containsKey(item.getName().toLowerCase())) return false;
		Operation.addToUndo(new Operation(item, Operation.ADDED));
		inventory.put(item.toString().toLowerCase(), item);
		setChanged();
		notifyObservers(item);
		return true;
	}
	
	/**
	 * Adds all the items to the inventory.
	 * 
	 */
	public void addAll(Collection<Item> items)
	{
		for(Item i: items)
		{
			add(i);
		}
	}
	
	/**
	 * Returns the item associated with the name in the map, or null if it does not exist.
	 * 
	 * @param name The name of the object to find.
	 * @return The object in the tree with the given name, or null if it does not exist.
	 */
	public Item get(String name)
	{
		return inventory.get(name.toLowerCase());
	}
	
	/**
	 * Merges the item with the instance of the item that already exists in the inventory. Adds the quantities and sets min and max to the new values.
	 * 
	 * @param item The item to merge into the inventory.
	 */
	public void merge(Item item)
	{
		Item i = inventory.get(item.getName().toLowerCase());
		i.updateQuantity(item.getQuantity());
		i.setMax(item.getMax());
		i.setMin(item.getMin());
	}
	
	/**
	 * Returns the GUI for this inventory.
	 * 
	 * @return The InventoryGUI for this inventory.
	 */
	public InventoryGUI getGUI()
	{
		if(gui == null)
		{
			gui = new InventoryGUI(this);
			addObserver(gui);
		}
		return gui;
	}
	
	/**
	 * Removes an item from the inventory.
	 * 
	 * @param item The item to remove.
	 */
	public void remove(Item item)
	{
		Operation.addToUndo(new Operation(item, Operation.REMOVED));
		inventory.remove(item.getName().toLowerCase());
		setChanged();
		notifyObservers(item);
	}
	
	/**
	 * Returns whether the inventory contains that item.
	 * 
	 * @param item The item to check for.
	 * @return Whether the inventory contains the item.
	 */
	public boolean contains(Item item)
	{
		return inventory.containsValue(item);
	}
	
	/**
	 * Returns whether the inventories are equal based on their names.
	 * 
	 * @param other The object to compare to.
	 */
	public boolean equals(Object other)
	{
		if(!(other instanceof Inventory)) return false;
		return ((Inventory) other).getName().equals(name);
	}
	
	/**
	 * Exports the grocery list of this inventory to a file. Can export to plain text (.txt) or Word Document (.docx).
	 */
	public void exportListToTextFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Microsoft Word Document (.docx)","docx"));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Plain Text Document (.txt)","txt"));
		chooser.setAcceptAllFileFilterUsed(false);
		int option = chooser.showSaveDialog(GUI.getGUI());
		if(option == JFileChooser.APPROVE_OPTION) {
			PrintWriter writer = null;
			try {
				File file = chooser.getSelectedFile();
				
				//Append valid extension if no extension is provided
				if (!(file.getName().endsWith(".docx") || file.getName().endsWith(".txt"))) {
					if (chooser.getFileFilter().getDescription().equals("Microsoft Word Document (.docx)")) {
						file = new File(file.getPath() + ".docx");
					}
					else {
						file = new File(file.getPath() + ".txt");
					}
				}
				
				if (file.getName().endsWith(".docx")) { //Write .docx file
					writeToDocx(this.getListString(), file);
					return; //if an exception is thrown, it will be caught below and display an error message
				}
				else { //Write .txt file
					file.createNewFile();
					writer = new PrintWriter(file);
					for(Item i: this.getGroceryList()) writer.println(i.getName() + " : " + i.amountToBuy());
					writer.close();
				}
				/*
				else if(file.getName().lastIndexOf(".txt") != file.getName().length() - 4) { //if NOT docx NOR txt export
					JOptionPane.showMessageDialog(null, "\".txt\" or \".docx\" extension required!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				*/
				
				
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Error in Saving", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}
	
	/**
	 * Attempts to write the given shopping list to a Microsoft Word (.docx) file.
	 * 
	 * @reference https://github.com/plutext/docx4j/blob/master/src/samples/docx4j/org/docx4j/samples/NewDocxHelloWorld.java
	 * @param list The String form of the list to be exported.
	 * @param file The File to be written to.
	 * @throws Docx4JException If there is a problem in the exporting of the grocery list.
	 */
	private void writeToDocx(String list, File file) throws Docx4JException {
		WordprocessingMLPackage pkg = WordprocessingMLPackage.createPackage();
		MainDocumentPart mdp = pkg.getMainDocumentPart();
		
		//Must add each line separately as a new "paragraph"
		//because the "\n" line break character is not rendered correctly in this library
		for (String line : list.split("\n")) {
			mdp.addParagraphOfText(line);
		}
		
		Docx4J.save(pkg, file, Docx4J.FLAG_SAVE_ZIP_FILE); //save the file
	}
	
	
	/**
	 * Exports this inventory's grocery list to an email.
	 */
	public void exportListToEmail() {
		Email.exportListToEmail(this.getListString());
	}
	
	
	/**
	 * Prints this inventory's grocery list.
	 */
	public void printList() {
		try {
			PrinterJob job = PrinterJob.getPrinterJob();
			
			//Note: the PrintedPage object (implements Printable) is defined in a private inner class
			job.setPrintable(new PrintedPage(this.getListString(), job)); 
			
			if (job.printDialog()) { //Display the standard print dialog and attempt to print if valid selection is made
				job.print();
				JOptionPane.showMessageDialog(GUI.getGUI(), "Sent to printer!", "Printer Job Sent", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		} 
		catch (Throwable e1) {
			JOptionPane.showMessageDialog(GUI.getGUI(), "Print failed!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	/**
	 * Private inner class that represents the physical piece of paper that the printer will output
	 */
	private class PrintedPage implements Printable {
		private String printString; //the string to be sent to the printer
		private PrinterJob job;

		//Letter size paper, units in inches
		private final double PAPERWIDTH = 8.5;
		private final double PAPERHEIGHT = 11;

		//1 inch = 72 pixels
		private final double INCHTOPIXELS = 72; 
		
		/**
		 * Constructs the page.
		 * 
		 * @param printString The text to print.
		 * @param job The PrinterJob for this page.
		 */
		public PrintedPage(String printString, PrinterJob job) {
			this.printString = printString;
			this.job = job;
		}

		/**
		 * Prints the page.
		 * Reference: http://www.java2s.com/Code/Java/2D-Graphics-GUI/Printabledemo.htm
		 * 
		 * @param graphics The Graphics for this page.
		 * @param pageFormat The PageFormat for this page.
		 * @param pageIndex The index for this page.
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
			
			//setting text style
			Graphics2D content = (Graphics2D) graphics;
			content.setFont(new Font("Serif", Font.PLAIN, 14));
			content.setPaint(Color.black);
			
			/*
			 * Parameters for drawString inside the for loop:
			 * drawString(ONE LINE, LEFT MARGIN (1 INCH), VERTICAL MARGIN INCREMENTED EACH LINE)
			 * This basically adds the shopping listline by line because the API
			 * doesn't support the "\n" linebreak character
			 */
			int vertMargin = (int) INCHTOPIXELS;
			for (String line : printString.split("\n")) {
				content.drawString(line, (int) INCHTOPIXELS, vertMargin+= content.getFontMetrics().getHeight());
			}
			
			return PAGE_EXISTS;
		}
		
	}
	
	/**
	 * Creates and returns the String representation of the Grocery List
	 * 
	 * @return the String representation of the Grocery List
	 */
	public String getListString() {
		//Create and store a String that is a readable representation of the shopping list
		String listString = "";
		listString = "Your shopping list generated at ";
		listString += new SimpleDateFormat("h:mm a 'on' MM/dd/yyyy").format(new java.util.Date());
		listString += ":\n";
		for (Item i: this.getGroceryList()) 
			listString += i.getName() + " : " + i.amountToBuy() + "\n";
		return listString;
	}
	
	/**
	 * Returns this inventory as a string.
	 * 
	 * @return The inventory's name.
	 */
	public String toString()
	{
		return name;
	}
}