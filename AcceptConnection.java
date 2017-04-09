import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptConnection extends Thread{

	private ServerSocket serverSocket;
	private Chatsystem chatsystem;
	
	public AcceptConnection(ServerSocket s, Chatsystem c){
		this.serverSocket = s;
		this.chatsystem = c;
	}
	
	public void run(){
		while(true){
			try{
				Socket socket = serverSocket.accept();
				chatsystem.addChannel(socket);
				System.out.println("Connection acceptée.");
			}catch(IOException e){
				System.out.println("AcceptConnection: " + e.getMessage());
			}
		}
	}
}
