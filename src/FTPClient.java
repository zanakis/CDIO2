import java.io.*;
import java.net.Socket;

public class FTPClient extends Socket{
	private static final String END_INPUT = "\r\n";
	
	public FTPClient(String server, int port, String user, String pass) throws Exception {
		this.user = user;
		this.pass = pass;
		socket = new Socket(server, port);
		inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		outToServer = new DataOutputStream(socket.getOutputStream());
		do {
			if(inFromServer.readLine().startsWith("230"))
				break;
			username();
			password();
		} while(!inFromServer.readLine().startsWith("230"));
		passiveMode();
	}
	
	public void username() throws Exception {
		outToServer.writeBytes("USER " + user + END_INPUT);
		outToServer.flush();
	}
	
	public void password() throws Exception {
		outToServer.writeBytes("PASS " + pass + END_INPUT);
		outToServer.flush();
	}
	
	public void passiveMode() throws Exception {
		outToServer.writeBytes("PASV" + END_INPUT);
		outToServer.flush();
		String str = inFromServer.readLine();
		System.out.println(str);
		String[] s = str.trim().split(",");
		int p1 = Integer.parseInt(s[4]);
		int p2 = Integer.parseInt(s[5].replace(")", ""));
	}
	
	public void changeDirectory(String str) throws Exception {
		outToServer.writeBytes("CD /" + str + END_INPUT);
		outToServer.flush();
	}
	
	public String list() throws Exception {
		outToServer.writeBytes("LIST" + END_INPUT);
		outToServer.flush();
		String str = "";
		String temp = "";
		while(!((temp = inFromServer.readLine()) != null)) {
			str += temp;
			str += "\n";
		}
		return str;
	}
	
	public void getFile(String serverFile, FileOutputStream localPath) throws Exception {
		outToServer.writeBytes("RETR" + END_INPUT);
		outToServer.flush();
	}
	
	public void sendCommand(String str) throws Exception {
		outToServer.writeBytes(str);
		outToServer.flush();
	}
	
	private String user;
	private String pass;
	private Socket socket;
	private BufferedReader inFromServer;
	private DataOutputStream outToServer;
}
