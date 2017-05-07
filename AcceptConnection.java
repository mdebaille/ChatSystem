import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Thread qui permet g�rer la connexion de nouveaux clients au serveur
 * Quand un client se connecte, un ChatController est cr�e avec le socket correspondant 
 * et ajout� � la liste des ChatControllers de MainController
 */

public class AcceptConnection extends Thread{

	private ServerSocket serverSocket;
	private NetworkManager networkManager;
	private boolean connected;
	
	public AcceptConnection(ServerSocket s, NetworkManager networkManager){
		// socket du serveur (sur lequel on attend la connexion des clients)
		this.serverSocket = s;
		this.networkManager = networkManager;
		this.connected = true;
	}
	
	public void run(){
		Socket socket = null;
		while(connected){
			try{
				// m�thode bloquante qui retourne le socket sur lequel on peut communiquer avec le nouveau client
				socket = serverSocket.accept();
				// on g�re l'ajout de ce nouveau socket
				networkManager.addChannel(socket);
				System.out.println("Connection acceptee.");
			}catch(IOException e){
				// Lorsque l'on se d�connecte le socket du serveur est ferm� et cette exception est lev�e
				System.out.println("AcceptConnection: servSocket ferme");
			}
		}
	}
	
	public void setConnected(boolean connected){
		System.out.println("AcceptConnexion: connected = false");
		this.connected = connected;
	}
}
