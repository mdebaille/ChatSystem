import java.io.IOException;
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
				//chatsystem.newConnection(socket);
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}
}
