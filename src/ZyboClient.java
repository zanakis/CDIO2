import java.util.*;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

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
		ftpClient = new FTPClient();
		try {
			ftpClient.connect(server, PORT);
			//			showServerReply(ftpClient);
			int replyCode = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				System.out.println("Operation failed. Server reply code: " + replyCode);
				return;
			}
			//			boolean success = ftpClient.login(user, pass);
			//			showServerReply(ftpClient);
			if (!ftpClient.login(user, pass)) {
				System.out.println("Could not login to the server");
				return;
			} else {
				System.out.println("LOGGED IN SERVER");
				return;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		connect();
	}

	//	private static void showServerReply(FTPClient ftpClient) {
	//		String[] replies = ftpClient.getReplyStrings();
	//		if (replies != null && replies.length > 0) {
	//			for (String reply : replies) {
	//				System.out.println("SERVER: " + reply);
	//			}
	//		}
	//	}

	public static void menu() {
		int choice = 0;
		System.out.println("1. Transfer menu");
		System.out.println("2. Commando menu");
		try {
			choice = in.nextInt();
		} catch(Exception e) {
			System.out.println("Please enter a number");
			menu();
			return;
		}
		switch(choice) {
		case 1: transferMenu();
		break;
		case 2: commandMenu();
		break;
		default: System.out.println("Please enter a valid choice");
		}
	}

	public static void transferMenu() {
		listFiles();
		System.out.println("Which file do you want to transfer?");
		//		transfer(filepath)?
	}

	public static void listFiles() {
		try {
			FTPFile[] files = ftpClient.listFiles("home/FTP");
			for(FTPFile f: files) {
				System.out.println(f.getName());
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	//	transfer(filepath) {
	//		RETR(filepath)?
	//	}

	public static void commandMenu() {

	}

	public static void main(String[] args) {
		connect();
		menu();
	}

	static Scanner in;
	static FTPClient ftpClient;
}