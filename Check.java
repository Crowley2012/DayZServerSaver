import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**************************************************
 * This class contains all the main methods
 * 
 * @author Sean Crowley
 **************************************************/
public class Check extends Thread {

	/** version **/
	private String VERSION = "1.3";

	/** Strings **/
	private String user;
	private String profile;
	private String serverName;
	private String serverIP;
	private String launchParams;

	/** Date **/
	private Date date;
	private DateFormat dateFormat;

	/** Directories **/
	private JFileChooser chooser = new JFileChooser();
	private File profileDir;
	private File steamDir;

	/** State of program **/
	private boolean running;

	/** GUI object **/
	private static GUI g;

	/**************************************************
	 * Getter / Setter methods
	 **************************************************/
	public String getUser() {
		return user;
	}

	public String getProfile() {
		return profile;
	}

	public String getServerName() {
		return serverName;
	}

	public String getServerIP() {
		return serverIP;
	}

	public boolean getRunning() {
		return running;
	}

	public File getProfileDir() {
		return profileDir;
	}

	public File getSteamDir() {
		return steamDir;
	}

	public void setUser() {
		user = System.getProperty("user.name");
	}

	public void setServerName(String s) {
		serverName = s;
	}

	public void setServerIP(String s) {
		serverIP = s;
	}

	public void setRunning(boolean b) {
		running = b;
	}

	public void setDate() {
		dateFormat = new SimpleDateFormat("hh:mm   MM/dd/yyyy");
		date = new Date();
		profileDir = new File("C:/Users/" + user + "/Documents/DayZ/" + user + ".DayZProfile");
		steamDir = new File("C:\\Program Files (x86)\\Steam\\");
		launchParams = "";
	}

	/**************************************************
	 * Starts the program
	 **************************************************/
	public void run() {
		running = true;
		profile = user;

		checkFiles();
		checkStart();
	}

	/**************************************************
	 * Loads saved servers
	 **************************************************/
	public void readConfig() {

		if (!profileDir.exists()) {
			g.setResults("Can not locate DayZ Profile. Please browse to its location.");
			browseProfile();
			g.setResults("");
			running = false;
		}

		if (!steamDir.exists()) {
			g.setResults("Can not locate Steam Directory. Please browse to its location.");
			browseSteam();
			g.setResults("");
			running = false;
		}

		if (profileDir.exists()) {
			try {
				Scanner sc = new Scanner(profileDir);

				while (sc.hasNext()) {
					String s = sc.nextLine();

					if (s.length() > 12 && s.substring(0, 13).equals("lastMPServer=")) {
						serverIP = s.substring(14, s.length() - 2);
					}

					if (s.length() > 17 && s.substring(0, 17).equals("lastMPServerName=")) {
						serverName = s.substring(18, s.length() - 2);
					}
				}

				sc.close();
			} catch (IOException e) {
				System.out.println("Failed to read profile.");
			}
		}
	}

	/**************************************************
	 * Save server to file
	 **************************************************/
	public void saveCurrent() {
		File f = new File("servers.txt");

		if (f.exists() && !f.isDirectory()) {

			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("servers.txt", true)))) {

				String server = serverIP + " | " + serverName + " | " + dateFormat.format(date);
				String serverFormat = serverName + "\n" + serverIP + "\n" + dateFormat.format(date) + "\n";

				if (!checkRepeat(serverIP)) {
					out.println(server);
				}
				g.appendResults("\n" + serverFormat);

			} catch (IOException e1) {
				g.appendResults("Error writing to file.");
			}

		} else {
			g.appendResults("File does not exisit.");
		}

	}

	/**************************************************
	 * Checks for a repeated server
	 **************************************************/
	public boolean checkRepeat(String servIP) {
		boolean repeat = false;
		String info[] = new String[3];
		ArrayList<String> servers = new ArrayList<String>();

		try {
			File f = new File("servers.txt");
			Scanner sc = new Scanner(f);

			while (sc.hasNext()) {
				servers.add(sc.nextLine());
			}

			if (!servers.isEmpty()) {
				info = servers.get(servers.size() - 1).split("\\|");

				if (info[0].trim().equals(servIP.trim())) {
					repeat = true;
				}
			}

			sc.close();
		}

		catch (IOException e) {
			g.setResults("Failed to read: Servers.txt");
		}

		return repeat;

	}

	/**************************************************
	 * Checks if servers.txt exists if not it creates it
	 **************************************************/
	public void checkFiles() {

		File f = new File("servers.txt");

		if (!f.exists() || f.isDirectory()) {
			PrintWriter writer = null;
			try {
				writer = new PrintWriter("servers.txt", "UTF-8");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			writer.close();
		}
	}

	/**************************************************
	 * Checks for new server
	 **************************************************/
	public void checkStart() {
		while (true) {
			while (running) {
				if (g.getLines() > 16) {
					g.setResults("Current Server\n");
				}
				readConfig();
				saveCurrent();
				try {
					Thread.sleep(150000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**************************************************
	 * Displays saved servers
	 **************************************************/
	public void display() {
		try {
			File f = new File("servers.txt");
			Scanner sc = new Scanner(f);
			int i = 1;

			g.setResults("Server History\n\n");

			while (sc.hasNext()) {
				String s = sc.nextLine();
				String info[] = new String[3];
				info = s.split("\\|");

				g.appendResults("Server:    " + i + "\nName:    " + info[1].trim() + "\nIP:            " + info[0].trim() + "\nDate:       " + info[2].trim() + "\n\n");

				i++;
			}

			sc.close();
		}

		catch (IOException e) {
			g.setResults("Failed to read: Servers.txt");
		}
	}

	/**************************************************
	 * Resets servers.txt
	 **************************************************/
	public void resetSave() {
		PrintWriter writer = null;

		try {
			writer = new PrintWriter("servers.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		writer.close();
		g.setResults("Servers.txt has been reset.");
	}

	/**************************************************
	 * Launches DayZ
	 **************************************************/
	public void launch() {
		int index = 0;
		ArrayList<String> servers = new ArrayList<String>();
		String info[] = new String[3];
		String IP[] = new String[2];

		try {
			index = Integer.parseInt(JOptionPane.showInputDialog("Type in the Server Number that you\n would like to connect to!"));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Incorrect. Please type in the server number.\n (Server > Display)", profile, index);
			return;
		}

		try {
			File f = new File("servers.txt");
			Scanner sc = new Scanner(f);

			while (sc.hasNext()) {
				servers.add(sc.nextLine());
			}

			String s = servers.get(index - 1);
			info = s.split("\\|");
			IP = info[0].split("\\:");
			sc.close();
		} catch (IOException e) {
			g.setResults("Failed to read: Servers.txt");
		} catch (IndexOutOfBoundsException e) {
			g.setResults("Incorrect server selection");
		}
		if (index >= 1 && index <= servers.size()) {
			try {
				Runtime.getRuntime().exec(steamDir + "\\Steam.exe -applaunch 221100 -connect=" + IP[0] + " -port=" + IP[1] + " " + launchParams.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**************************************************
	 * Add custom server
	 **************************************************/
	public void addServer() {
		File f = new File("servers.txt");
		JTextField field1 = new JTextField();
		JTextField field2 = new JTextField();
		String value1 = "";
		String value2 = "";

		Object[] message = { "Server IP", field1, "Server Name", field2, };

		int option = JOptionPane.showConfirmDialog(null, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			value1 = field1.getText();
			value2 = field2.getText();
		}

		if (f.exists() && !f.isDirectory()) {

			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("servers.txt", true)))) {
				out.println(value1 + " | " + value2 + " | " + dateFormat.format(date));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		display();

	}

	/**************************************************
	 * Remove server form list
	 **************************************************/
	public void removeServer() {
		ArrayList<String> servers = new ArrayList<String>();
		int index = 0;
		String info[] = new String[3];

		try {
			index = Integer.parseInt(JOptionPane.showInputDialog("Type in the Server Number that you\n would like to remove."));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Incorrect. Please type in the server number.\n (Server > Display)", profile, index);
			return;
		}

		try {
			File f = new File("servers.txt");
			Scanner sc = new Scanner(f);

			while (sc.hasNext()) {
				servers.add(sc.nextLine());
			}
			sc.close();

			servers.remove(index - 1);
			resetSave();

			for (String s : servers) {
				info = s.split("\\|");
				try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("servers.txt", true)))) {
					out.println(info[0] + " | " + info[1] + " | " + info[2]);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		catch (IOException e) {
			g.setResults("Failed to read: Servers.txt");
		}
		display();
	}

	/**************************************************
	 * Set DayZ profile location
	 **************************************************/
	public void browseProfile() {
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Browse to DayZ Profiles (Ex. /Documents/Dayz or /Dayz Other Profiles)");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			String s = chooser.getSelectedFile().toString() + "\\" + user + ".DayZProfile";
			profileDir = new File(s);
		} else {
			System.out.println("No Selection");
		}
	}

	/**************************************************
	 * Set Steam location
	 **************************************************/
	public void browseSteam() {
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Browse to Steam Directory (Ex. C:/Program Files (x86)/Steam)");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			String s = chooser.getSelectedFile().toString();
			steamDir = new File(s);
		} else {
			System.out.println("No Selection");
		}

	}

	/**************************************************
	 * Set launch parameters
	 **************************************************/
	public void launchParameters() {
		launchParams = JOptionPane.showInputDialog("Type in DayZ launch params (Ex. -cpuCount=4 -maxMem=2048)");
	}

	/**************************************************
	 * About
	 **************************************************/
	public void about() {
		g.setResults("-About-\n\n");
		g.appendResults("Version\n");
		g.appendResults("   " + VERSION + "\n\n");
		g.appendResults("Created by\n");
		g.appendResults("   Sean Crowley\n");
		g.appendResults("   /u/Crowley2012\n\n");
		g.appendResults("Contact\n");
		g.appendResults("   Contact with bugs/new features\n");
		g.appendResults("   Crowley.P.Sean@gmail.com");
	}

	/**************************************************
	 * Help
	 **************************************************/
	public void howTo() {
		g.setResults("-How To Use-\n\n");
		g.appendResults("Description\n");
		g.appendResults("   This program will keep a list of all the servers you have joined.\n");
		g.appendResults("   The program must be running when you are playing DayZ.\n\n");
		g.appendResults("Features\n");
		g.appendResults("   Clear Saved: \tWill clear server list.\n");
		g.appendResults("   Start: \tWill start updating server list.\n");
		g.appendResults("   Stop: \tWill stop updating server list.\n");
		g.appendResults("   Add: \tWill add custom server to server list.\n");
		g.appendResults("   Remove: \tWill remove server from server list.\n");
		g.appendResults("   Display: \tWill display server list.\n");
		g.appendResults("   Launch: \tWill launch DayZ and join selected server.\n");
		g.appendResults("   Parameters: \tSet parameters to launch DayZ with.\n");
		g.appendResults("   Profile Browse: Browse to the location of your DayZ Profiles.\n");
		g.appendResults("   Steam Browse: Browse to the location of your Steam Directory.\n\n");
		g.appendResults("How To Run\n");
		g.appendResults("   Click Servers > Start and then start playing DayZ!\n");

	}

	/**************************************************
	 * Main method
	 **************************************************/
	public static void main(String[] args) {
		g = new GUI();
		g.start();

	}
}
