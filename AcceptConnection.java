import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptConnection extends Thread{

	private ServerSocket serverSocket;
	private NetworkManager networkManager;
	
	public AcceptConnection(ServerSocket s, NetworkManager networkManager){
		this.serverSocket = s;
		this.networkManager = networkManager;
	}
	
	public void run(){
		while(true){
			try{
				Socket socket = serverSocket.accept();
				networkManager.addChannel(socket);
				System.out.println("Connection acceptée.");
			}catch(IOException e){
				System.out.println("AcceptConnection: " + e.getMessage());
			}
		}
	}
}
