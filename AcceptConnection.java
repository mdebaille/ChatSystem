import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Thread qui permet gérer la connexion de nouveaux clients au serveur
 * Quand un client se connecte, un ChatController est crée avec le socket correspondant 
 * et ajouté à la liste des ChatControllers de MainController
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
				// méthode bloquante qui retourne le socket sur lequel on peut communiquer avec le nouveau client
				socket = serverSocket.accept();
				// on gère l'ajout de ce nouveau socket
				networkManager.addChannel(socket);
				System.out.println("Connection acceptee.");
			}catch(IOException e){
				// Lorsque l'on se déconnecte le socket du serveur est fermé et cette exception est levée
				System.out.println("AcceptConnection: servSocket ferme");
			}
		}
	}
	
	public void setConnected(boolean connected){
		System.out.println("AcceptConnexion: connected = false");
		this.connected = connected;
	}
}
