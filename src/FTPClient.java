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
		
		username();
		password();
	}
	
	public void username() throws Exception {
		outToServer.writeBytes("USER " + user + END_INPUT);
		outToServer.flush();
		System.out.println(inFromServer.readLine());
	}
	
	public void password() throws Exception {
		outToServer.writeBytes("PASS " + pass + END_INPUT);
		outToServer.flush();
		System.out.println(inFromServer.readLine());
	}
	
	public void changeDirectory(String str) throws Exception {
		outToServer.writeBytes("cd /" + str);
		System.out.println(inFromServer.readLine());
	}
	
	public String list() throws Exception {
		outToServer.writeBytes("ls");
		String str = "";
		while(!str.equals(str += inFromServer.readLine()))
			str += "\n";
		return str;
	}
	
	public void getFile(String serverFile, FileOutputStream localPath) {
		
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
