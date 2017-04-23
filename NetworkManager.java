import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {

	private String myPseudo;
	private InetAddress myIP;
	private int portServSocket = 17001;
	private ServerSocket servSocket;
	
	private int portMulticast = 17002;
	private InetAddress group;
	private MulticastSocket multicastSocket;
	
	MainController mainController;
	
	public NetworkManager(String pseudo, MainController mainController){
		try{
			this.myPseudo = pseudo;
			this.mainController = mainController;
			this.myIP = InetAddress.getLocalHost();
			this.group = InetAddress.getByName("228.5.6.7");
			multicastSocket = new MulticastSocket(portMulticast);
			multicastSocket.joinGroup(group);
			servSocket = new ServerSocket();
			
			MessageUserBroadcaster messageUserBroadcaster = new MessageUserBroadcaster(this.multicastSocket, this.myPseudo, this.myIP, this.portServSocket, this.group, this.portMulticast);
			messageUserBroadcaster.start();
			
			// Boucle infinie pour accepter les connections d'autres utilisateurs quand ils veulent communiquer avec nous
			AcceptConnection acceptLoop = new AcceptConnection(servSocket, this);
			acceptLoop.start();
			
			// Boucle infinie qui gere la reception des MessageUser emis en multicast et les passe ࡕsersModel pour que la liste des users soit mise ࡪour
			MulticastListener multicastListener = new MulticastListener(multicastSocket, this.mainController.getUsersModel());
			multicastListener.start();
			
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void addChannel(Socket socketDest){
		this.mainController.addChatController(socketDest);
	}

	
}
