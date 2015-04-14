import java.io.*;
import java.net.Socket;

public class FTPClient extends Socket{
	private static final String END_INPUT = "\r\n";

	public FTPClient(String server, int port, String user, String pass) throws Exception {
		this.server = server;
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
		int p2 = Integer.parseInt(s[5].substring(0, s[5].length()-3));
		int port = p1*256 + p2;
		setupDataSocket(port);
		dataSocket = new Socket(server, port);
		Listener t1 = new Listener(inFromDataServer);
		t1.run();
	}

	public void setupDataSocket(int port) throws Exception {
		inFromDataServer = new DataInputStream(dataSocket.getInputStream());
		outToDataServer = new DataOutputStream(dataSocket.getOutputStream());
		outToDataServer.writeBytes("USER " + user + END_INPUT);
		outToDataServer.flush();
		outToDataServer.writeBytes("PASS " + pass + END_INPUT);
		outToDataServer.flush();
	}

	public void changeDirectory(String str) throws Exception {
		outToServer.writeBytes("CD " + str + END_INPUT);
		outToServer.flush();
	}

	public String list() throws Exception {
		outToServer.writeBytes("LIST" + END_INPUT);
		outToServer.flush();
		String str = "";
		String temp = "";
		while((temp = inFromDataServer.readLine()) != null) {
			System.out.println(temp);
			str += temp;
			str += "\n";
		}
		return str;
	}

	public void getFile(String serverFile, FileOutputStream localPath) throws Exception {
		outToServer.writeBytes("TYPE I" + END_INPUT);
		outToServer.flush();
		outToServer.writeBytes("RETR" + END_INPUT);
		outToServer.flush();
		FileOutputStream outputStream = new FileOutputStream(serverFile);
		InputStream inputStream = dataSocket.getInputStream();
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while((bytesRead = inputStream.read(buffer)) > -1)
		{
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();

	}

	public void sendCommand(String str) throws Exception {
		outToServer.writeBytes(str);
		outToServer.flush();
	}

	private String server;
	private String user;
	private String pass;
	private Socket socket;
	private Socket dataSocket;
	private BufferedReader inFromServer;
	private DataOutputStream outToServer;
	private DataInputStream inFromDataServer;
	private DataOutputStream outToDataServer;
}
