import java.io.DataInputStream;
import java.net.Socket;


public class Listener extends Thread {
	
	public Listener(DataInputStream inFromServer) {
		this.inFromServer = inFromServer;
	}
	
	public synchronized void run() {
		while(true) {
			try {
				System.out.println(inFromServer.readLine());
				Thread.sleep(500);
			} catch(InterruptedException e) {
				System.out.println("Thread interrupted");
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private DataInputStream inFromServer;
}
