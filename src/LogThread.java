

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class LogThread extends Thread {
	private Thread t;
	private String name;
	private Socket socket;
	BufferedReader inFromServer;

	public LogThread(String name, Socket socket) throws IOException {
		this.name = name;
		this.socket = socket;
		inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void run(){
		try {
			while(true) {
				printlog();
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			System.out.println("Thread " +  name + " interrupted.");
		} catch(Exception e) {
			
		}
	}
}