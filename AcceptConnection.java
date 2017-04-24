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
		while(connected){
			try{
				Socket socket = serverSocket.accept();
				networkManager.addChannel(socket);
				System.out.println("Connection acceptée.");
			}catch(IOException e){
				System.out.println("AcceptConnection: " + e.getMessage());
			}
		}
	}
	
	public void setConnected(boolean connected){
		this.connected = connected;
	}
}
