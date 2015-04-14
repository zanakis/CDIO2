import java.util.*;
import java.io.FileOutputStream;

public class ZyboClient {
	static final int PORT = 21;

	private static void connect() {
		in = new Scanner(System.in);
		System.out.println("Enter server address");
		String server = in.next();
		System.out.println("Enter username");
		String user = in.next();
		System.out.println("Enter password");
		String pass = in.next();
		try {
			socket = new FTPClient(server, PORT, user, pass);
//			socket.changeDirectory("/upload");
		} catch (Exception e) {
//			System.out.println(e.getMessage());
		}
	}

	public static void menu() {
		int choice = 0;
		System.out.println("1. Transfer menu");
		System.out.println("2. Commando menu");
		System.out.println("0. Quit");
		try {
			choice = in.nextInt();
		} catch(Exception e) {
			System.out.println("Please enter a number");
			menu();
			return;
		}
		switch(choice) {
		case 0: quit();
		break;
		case 1: transferMenu();
		break;
		case 2: commandMenu();
		break;
		default: System.out.println("Please enter a valid choice");
		}
	}
	
//	brugeren får mulighed for at skifte downloadmappen
	public static void transferMenu() {
		int choice = 0;
		System.out.println("1. Transfer file");
		System.out.println("2. Channge transfer directory (default: program directory)");
		try {
			choice = in.nextInt();
		} catch(Exception e) {
			System.out.println("Please enter a number");
			transferMenu();
			return;
		}
		switch(choice) {
		case 1: listFiles();
		System.out.println("Which file do you want to transfer?");
		String filepath = in.next();
		transfer(filepath);
		break;
		case 2: changeDownloadDirectory();
		break;
		default: System.out.println("Please enter a valid number");
		}
	}

	public static void listFiles() {
		try {
			System.out.println(socket.list());
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void transfer(String filename) {
		try {
			FileOutputStream localFilepath = new FileOutputStream(localPath + filename);
			socket.getFile(filename, localFilepath);
			if(localPath != null && !localPath.equals(""))
				System.out.println("File transfered to " + localPath);
			else System.out.println("File transfered to default directory");
		} catch(Exception e) {
			System.out.println("File doesn't exist");
		}
		menu();
	}

	public static void changeDownloadDirectory() {
		localPath = in.next();
	}

	public static void commandMenu() {
		//		mangler implementation
		while(true) {
			if(sendCommand())
				break;
		}
		menu();
	}

	public static boolean sendCommand() {
		//		sende commands - grov model
		System.out.println("Enter command to send, send 0 to quit");
		in.nextLine();
		String command = in.nextLine();
		if(command.equals("0"))
			return true;
		try {
			socket.sendCommand(command);
		} catch(Exception e) {
			System.out.println("Invalid command");
		}
		return false;
	}

	public static void quit() {
		try {
			socket.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		System.exit(0);
	}

	public static void main(String[] args) {
		connect();
		menu();
	}

	static String localPath = "";
	static Scanner in;
	static FTPClient socket;
}