import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {

	private String myPseudo;
	private InetAddress myIP;
	private int myPort;
	
	private int portMulticast;
	private InetAddress group;
	private MulticastSocket multicastSocket;
	
	public NetworkManager(String pseudo, InetAddress ip, int port, InetAddress groupIP, int multicastPort ){
		myPseudo = pseudo;
		myIP = ip;
		myPort = port;
		/*try{
			multicastSocket = new MulticastSocket(portMulticast);
			multicastSocket.joinGroup(group);
			servSocket = new ServerSocket();
			
			// Boucle infinie pour accepter les connections d'autres utilisateurs quand ils veulent communiquer avec nous
			AcceptConnection acceptLoop = new AcceptConnection(servSocket, this);
			acceptLoop.start();
			
			// Boucle infinie qui gere la reception des MessageUser emis en multicast et les passe ࡕsersModel pour que la liste des users soit mise ࡪour
			MulticastListener multicastListener = new MulticastListener(multicastSocket, um);
			multicastListener.start();
			
		}catch(IOException e){
			System.out.println("Chatsystem: " + e.getMessage());
		}*/
	}
	
	public void sendMessageUser(){
		try{
			MessageUser mess = new MessageUser(myPseudo, myIP, myPort, MessageUser.typeConnect.CONNECTED);
			String m = MessageUser.serializeMessage(mess);
			DatagramPacket packet = new DatagramPacket(m.getBytes(), m.length(), group, portMulticast);
			multicastSocket.send(packet);
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	private ChatController addChannel(Socket socket){
		return null;
	}
	
	private void removeChannel(InfoUser infoUser){
		
	}
	
}
