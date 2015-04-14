import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class WeightController {
	static final int MIN_PRODUCT_ID = 1;
	static final int MAX_PRODUCT_ID = 999999;
	static final String STORE_PATH = "store.txt";
	static final String LOG_PATH = "log.txt";
	static final String END_INPUT = "\r\n";

	public static void main(String[] args) throws Exception {
		endProgram = false;
		initFiles();
		readFiles();
		connect();
		do {
			startSequence();
		} while(!endProgram);
		clientSocket.close();
	}

	public static void initFiles() throws FileNotFoundException {
		File f = new File(STORE_PATH);
		if(!f.isFile()) {
			PrintWriter writer = new PrintWriter(STORE_PATH);
			writer.close();
		}
		f = new File(LOG_PATH);
		if(!f.isFile()) {
			PrintWriter writer = new PrintWriter(LOG_PATH);
			writer.close();
		}
	}

	public static void readFiles() {
		storeEntries = new ArrayList<StoreEntry>();
		ArrayList<String> entries = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			String s;
			reader = new BufferedReader(new FileReader(STORE_PATH));
			while((s = reader.readLine()) != null)
				entries.add(s);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
		}

		String[] s0;
		for(int i = 0; i < entries.size(); i++) {
			s0 = entries.get(i).split(",");
			storeEntries.add(new StoreEntry(Integer.parseInt(s0[0]), s0[1]));
		}
	}

	public static void connect() throws Exception {
		String host;
		int port = 0;

		System.out.println("host name");
		host = in.next();
		System.out.println("port");
		try {
			port = Integer.parseInt(in.next());
		} catch(Exception e) {
			System.out.println("Invalid port");
			connect();
			return;
		}

		try{
			clientSocket = new Socket(host, port);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch(Exception e){
			System.out.println("Could not connect to server");
			connect();
		}
	}

	public static void startSequence() throws Exception {
		oprId();
		productId();
		weigh();
		log();
	}

	public static void oprId() throws Exception {
		int id = 0;
		outToServer.writeBytes("RM20 4 \"Enter operator ID:\" \" \" \" 	\"" + END_INPUT);
		if(!"RM20 B".equals(inFromServer.readLine())) {
			generalError();
			oprId();
			return;
		}
		String response = rm20(inFromServer.readLine());
		try {
			id = Integer.parseInt(response);
		} catch(Exception e) {
			numberError();
			oprId();
			return;
		}
		oprId = id;
	}

	public static void productId() throws Exception {
		int id = 0;
		outToServer.writeBytes("RM20 4 \"Enter product ID:\" \" \" \" \"" + END_INPUT);
		if(!"RM20 B".equals(inFromServer.readLine())) {
			System.out.println("problem");
			generalError();
			productId();
			return;
		}
		String response = rm20(inFromServer.readLine());
		try {
			id = Integer.parseInt(response);
		} catch(Exception e) {
			System.out.println(response);
			System.out.println(response);
			numberError();
			productId();
			return;
		}
		if((id <= MAX_PRODUCT_ID) && (id >= MIN_PRODUCT_ID))
			productId = id;
		else {
			outToServer.writeBytes("D Invalid product ID" + END_INPUT);
			productId();
			return;
		}
		if(getProduct(productId)==0) {
			outToServer.writeBytes("D Poduct ID doesn't exist" + END_INPUT);
			productId();
			return;
		}
		operatorCheck();
	}
	
	public static int getProduct(int id) throws Exception {
		for(int i = 0; i < storeEntries.size(); i++) {
			if(storeEntries.get(i).getNumber() == id) {
				productName = storeEntries.get(i).getName();
				return productId = id;
			}
		}
		return 0;
	}
	
	public static void operatorCheck() throws Exception {
		outToServer.writeBytes("RM20 8 \"" + productName + "\" \" \" \"y/n\"" + END_INPUT);
		if(!"RM20 B".equals(inFromServer.readLine())) {
			generalError();
			operatorCheck();
			return;
		}
		String response = rm20(inFromServer.readLine());
		if("n".equalsIgnoreCase(response)) {
			productId();
		}
		else if(!"y".equalsIgnoreCase(response)) {
			generalError();
			productId();
		}
	}

	public static void weigh() throws Exception {
		tare();
		fill();
		empty();
	}
	
	public static void tare() throws Exception {
		outToServer.writeBytes("RM20 8 \"Place container on the weight\" \" \" \"y/n\"" + END_INPUT);
		if(!"RM20 B".equals(inFromServer.readLine())) {
			generalError();
			productId();
			return;
		}
		String response = rm20(inFromServer.readLine());
		if("n".equalsIgnoreCase(response)) {
			weigh();
			return;
		} else if(!"y".equalsIgnoreCase(response)) {
			generalError();
			weigh();
			return;
		}
		outToServer.writeBytes("T" + END_INPUT);
		response = numerize(inFromServer.readLine());
		try {
			tare = Double.parseDouble(response);
		} catch(Exception e) {
			generalError();
			weigh();
		}
	}
	
	public static void fill() throws Exception {
		outToServer.writeBytes("RM20 8 \"Place product in container\" \" \" \"y/n\"" + END_INPUT);
		if(!"RM20 B".equals(inFromServer.readLine())) {
			generalError();
			productId();
			return;
		}
		String response = rm20(inFromServer.readLine());
		if("n".equalsIgnoreCase(response)) {
			fill();
			return;
		} else if(!"y".equalsIgnoreCase(response)) {
			generalError();
			fill();
			return;
		}
		outToServer.writeBytes("S" + END_INPUT);
		response = numerize(inFromServer.readLine());
		try {
			net = Double.parseDouble(response);
		} catch(Exception e) {
			generalError();
			fill();
		}
	}

	public static void empty() throws Exception {
		outToServer.writeBytes("RM20 8 \"Clear weight\" \" \" \"y/n\"" + END_INPUT);
		if(!"RM20 B".equals(inFromServer.readLine())) {
			generalError();
			productId();
			return;
		}
		String response = rm20(inFromServer.readLine());
		if("n".equalsIgnoreCase(response)) {
			empty();
			return;
		} else if(!"y".equalsIgnoreCase(response)) {
			generalError();
			empty();
			return;
		}
		outToServer.writeBytes("T" + END_INPUT);
		response = numerize(inFromServer.readLine());
		tare = Double.parseDouble(response);
		if(net+tare < 0) {
			outToServer.writeBytes("D Brutto control OK" + END_INPUT);
		} else outToServer.writeBytes("D Brutto control failed" + END_INPUT);
	}
	
	public static void log() throws Exception {
		String logEntry = new Date() + "," + oprId + "," + productName + "," + net;
		DataOutputStream writer = null;
		try {
			writer = new DataOutputStream(new FileOutputStream(new File(LOG_PATH), true));
			writer.writeBytes(logEntry + END_INPUT);
		} catch (Exception e) {
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
	}

	public static void generalError() throws Exception {
		outToServer.writeBytes("D Illegal input" + END_INPUT);
		Thread.sleep(1000);
	}

	public static void numberError() throws Exception {
		outToServer.writeBytes("D Please enter a number" + END_INPUT);
		Thread.sleep(1000);
	}

	public static String rm20(String response) {
		return response.substring(7);
	}
	
	public static String numerize(String response) {
		Scanner number = new Scanner(response);
		while(!number.hasNextDouble()) {
			number.next();
		}
		return "" +number.nextDouble();
	}
	
	static boolean endProgram;
	static int oprId;
	static int productId;
	static double tare = 0;
	static double net = 0;
	static String productName;
	static Scanner in = new Scanner(System.in);
	static Socket clientSocket;
	static DataOutputStream outToServer;
	static BufferedReader inFromServer;
	static ArrayList<StoreEntry> storeEntries;
}
