import java.util.*;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class CopyOfZyboClient {
	static final int PORT = 21;

	private static void connect() {
		in = new Scanner(System.in);
		System.out.println("Enter server address");
		String server = in.next();
		System.out.println("Enter username");
		String user = in.next();
		System.out.println("Enter password");
		String pass = in.next();
		ftpClient = new FTPClient();
		try {
			ftpClient.connect(server, PORT);
			int replyCode = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				System.out.println("Operation failed. Server reply code: " + replyCode);
				return;
			}
			if (!ftpClient.login(user, pass)) {
				System.out.println("Could not login to the server");
			} else {
				System.out.println("LOGGED IN SERVER");
				return;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		connect();
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

	public static void transferMenu() {
		int choice = 0;
		System.out.println("1. Transfer file");
		System.out.println("2. Channge transfer directory (default: program direcctory");
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
			FTPFile[] files = ftpClient.listFiles("/upload");
			for(FTPFile f: files) {
				if(!f.isDirectory())
					System.out.println(f.getName());
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void transfer(String filename) {
		try {
			FileOutputStream localFilepath = new FileOutputStream(localPath + filename);
			ftpClient.retrieveFile("/upload/" + filename, localFilepath);
			System.out.println("File transfered to " + localPath);
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
			showServerReply();
		}
		menu();
	}

	public static boolean sendCommand() {
		//		sende commands - grov model
		System.out.println("Enter command to send");
		String command = in.next();
		try {
			ftpClient.sendCommand(command);
		} catch(Exception e) {
			System.out.println("Invalid command");
		}
		return false;
	}

	private static void showServerReply() {
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String reply : replies) {
				System.out.println("SERVER: " + reply);
			}
		}
	}

	public static void quit() {
		System.exit(0);
	}

	public static void main(String[] args) {
		connect();
		menu();
	}

	static String localPath = "";
	static Scanner in;
	static FTPClient ftpClient;
}