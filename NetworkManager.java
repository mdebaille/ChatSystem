import java.io.IOException;
import java.net.DatagramPacket;
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
	MessageUserBroadcaster messageUserBroadcaster;
	AcceptConnection acceptLoop;
	MulticastListener multicastListener;
	
	public NetworkManager(String pseudo, MainController mainController){
		try{
			this.myPseudo = pseudo;
			this.mainController = mainController;
			this.myIP = InetAddress.getLocalHost();
			this.group = InetAddress.getByName("228.5.6.7");
			multicastSocket = new MulticastSocket(portMulticast);
			multicastSocket.joinGroup(group);
			servSocket = new ServerSocket(portServSocket);
			
			// Envoie les MessageUser periodiquement (pour signaler notre connection)
			messageUserBroadcaster = new MessageUserBroadcaster(this.multicastSocket, this.myPseudo, this.myIP, this.portServSocket, this.group, this.portMulticast);
			messageUserBroadcaster.start();
			
			// Accepte les connections d'autres utilisateurs quand ils veulent communiquer avec nous
			acceptLoop = new AcceptConnection(servSocket, this);
			acceptLoop.start();
			
			// Gere la reception des MessageUser emis en multicast et les passe a UsersModel pour que la liste des users soit mise a jour
			multicastListener = new MulticastListener(multicastSocket, this.mainController.getUsersModel());
			multicastListener.start();
			
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void addChannel(Socket socketDest){
		this.mainController.addChatController(socketDest);
	}
	
	public void sendDisconnect(){
		try{
			MessageUser mess = new MessageUser(myPseudo, myIP, portServSocket, MessageUser.typeConnect.DISCONNECTED);
			String message = MessageUser.serializeMessage(mess);
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), group, portMulticast);
			multicastSocket.send(packet);
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	public void notifyDisconnection(){
		messageUserBroadcaster.setConnected(false);
		acceptLoop.setConnected(false);
		multicastListener.setConnected(false);
	}
	
}
