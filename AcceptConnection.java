import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptConnection extends Thread{

	private ServerSocket serverSocket;
	private NetworkManager networkManager;
	private boolean connected;
	
	public AcceptConnection(ServerSocket s, NetworkManager networkManager){
		this.serverSocket = s;
		this.networkManager = networkManager;
		this.connected = true;
	}
	
	public void run(){
		Socket socket = null;
		while(connected){
			try{
				socket = serverSocket.accept();
				networkManager.addChannel(socket);
				System.out.println("Connection acceptée.");
			}catch(IOException e){
				System.out.println("AcceptConnection: servSocket fermé");
			}
		}
	}
	
	public void setConnected(boolean connected){
		System.out.println("AcceptConnexion: connected = false");
		this.connected = connected;
	}
}
