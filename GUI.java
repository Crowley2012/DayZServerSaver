import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**************************************************
 * This class controls all aspects of the GUI
 * 
 * @author Sean Crowley
 **************************************************/
public class GUI extends Thread {

	/** Frame of GUI **/
	private JFrame frame;

	/** Text area for feedback **/
	public JTextArea results;

	/** File menu items **/
	private JMenuBar menus;
	private JMenu fileMenu;
	private JMenuItem browseProfile;
	private JMenuItem browseSteam;
	private JMenuItem clearServers;
	private JMenuItem quit;

	/** Server menu items **/
	private JMenu serverMenu;
	private JMenuItem serverStart;
	private JMenuItem serverStop;
	private JMenuItem addServers;
	private JMenuItem removeServers;
	private JMenuItem serverDisplay;

	/** Launch menu items **/
	private JMenu launch;
	private JMenuItem params;
	private JMenuItem runDayz;

	/** Help menu items **/
	private JMenu helpMenu;
	private JMenuItem help;
	private JMenuItem about;

	/** Check object **/
	private static Check c;

	/**************************************************
	 * Run method will setup the GUI
	 **************************************************/
	public synchronized void run() {

		// New check thread
		c = new Check();
		c.checkFiles();
		c.setUser();
		c.setDate();

		// Frame
		frame = new JFrame("DayZ Server Saver");

		// Menu bar
		menus = new JMenuBar();

		// File menu
		fileMenu = new JMenu("File");
		browseProfile = new JMenuItem("DayZ Profile Browse");
		browseSteam = new JMenuItem("Steam Dir Browse");
		clearServers = new JMenuItem("Clear Saved");
		quit = new JMenuItem("Quit");

		// Server menu
		serverMenu = new JMenu("Servers");
		serverStart = new JMenuItem("Start");
		serverStop = new JMenuItem("Stop");
		serverDisplay = new JMenuItem("Display");
		addServers = new JMenuItem("Add");
		removeServers = new JMenuItem("Remove");

		// Launch menu
		launch = new JMenu("Launch");
		params = new JMenuItem("Parameters");
		runDayz = new JMenuItem("DayZ");

		// Help menu
		helpMenu = new JMenu("Help");
		help = new JMenuItem("How To Use");
		about = new JMenuItem("About");

		// Establish listener
		ButtonListener listener = new ButtonListener();

		// Add listener to buttons
		quit.addActionListener(listener);
		clearServers.addActionListener(listener);
		help.addActionListener(listener);
		about.addActionListener(listener);
		serverStart.addActionListener(listener);
		serverStop.addActionListener(listener);
		serverDisplay.addActionListener(listener);
		runDayz.addActionListener(listener);
		addServers.addActionListener(listener);
		removeServers.addActionListener(listener);
		browseProfile.addActionListener(listener);
		browseSteam.addActionListener(listener);
		params.addActionListener(listener);

		// Results text area
		results = new JTextArea(10, 20);
		JScrollPane scrollPane = new JScrollPane(results);
		results.setEditable(false);

		// Add file menu items
		fileMenu.add(browseProfile);
		fileMenu.add(browseSteam);
		fileMenu.addSeparator();
		fileMenu.add(clearServers);
		fileMenu.addSeparator();
		fileMenu.add(quit);

		// Add help menu items
		helpMenu.add(help);
		helpMenu.add(about);

		// Add server menu items
		serverMenu.add(serverStart);
		serverMenu.add(serverStop);
		serverMenu.addSeparator();
		serverMenu.add(addServers);
		serverMenu.add(removeServers);
		serverMenu.addSeparator();
		serverMenu.add(serverDisplay);

		// Add launch menu items
		launch.add(runDayz);
		launch.add(params);

		// Add to menu bar
		menus.add(fileMenu);
		menus.add(serverMenu);
		menus.add(launch);
		menus.add(helpMenu);

		// Add items to frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menus);
		frame.add(scrollPane);
		frame.pack();
		frame.setSize(new Dimension(500, 375));
		frame.setVisible(true);
	}

	/**************************************************
	 * ButtonListener method will provide action to the buttons
	 **************************************************/
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			// Starts adding servers to list
			if (e.getSource() == serverStart) {
				if (c.getProfile() != null) {
					results.setText("Current Servers\n");
					if (!c.isAlive()) {
						c.start();
					} else {
						c.setRunning(true);
					}
				} else {
					results.setText("Current Servers\n");
					if (!c.isAlive()) {
						c.start();
					} else {
						c.setRunning(true);
					}
				}
			}

			// Stops adding server to list
			if (e.getSource() == serverStop) {
				results.append("\nStopped");
				c.setRunning(false);
			}

			// Displays saved servers
			if (e.getSource() == serverDisplay) {
				c.display();
			}

			// Clears saved servers
			if (e.getSource() == clearServers) {
				c.resetSave();
			}

			// Sets DayZ Profile directory
			if (e.getSource() == browseProfile) {
				c.browseProfile();
				appendResults(c.getProfileDir().toString() + "\n");
			}

			// Sets Steam directory
			if (e.getSource() == browseSteam) {
				c.browseSteam();
				appendResults(c.getSteamDir().toString() + "\n");
			}

			// Launches DayZ
			if (e.getSource() == runDayz) {
				c.display();
				c.launch();
			}

			// Sets DayZ launch parameters
			if (e.getSource() == params) {
				c.launchParameters();
			}

			// Removes server from list
			if (e.getSource() == removeServers) {
				c.display();
				c.removeServer();
			}

			// Adds custom server to list
			if (e.getSource() == addServers) {
				c.addServer();
			}

			// Displays information on how to use program
			if (e.getSource() == help) {
				c.howTo();
			}

			// About me
			if (e.getSource() == about) {
				c.about();
			}

			// Quits the program
			if (e.getSource() == quit) {
				System.exit(1);
			}
		}
	}

	/**************************************************
	 * Sets the next area
	 **************************************************/
	public void setResults(String s) {
		results.setText(s);
	}

	/**************************************************
	 * Appends to the text area
	 **************************************************/
	public void appendResults(String s) {
		results.append(s);
	}

	/**************************************************
	 * Returns the number of lines in text area
	 **************************************************/
	public int getLines() {
		return results.getLineCount();
	}
}
