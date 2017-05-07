import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 * Thread qui écoute sur le socket de multicast
 * Ainsi il notifie la classe UsersModel que des nouveaux MessageUser on était reçu
 */

public class MulticastListener extends Thread{
	
	private DatagramSocket socket;
	private UsersModel usersModel;
	private boolean connected;
	
	public MulticastListener(DatagramSocket socket, UsersModel um){
		this.socket = socket;
		this.usersModel = um;
		this.connected = true;
	}
	
	public void run(){
		while(connected){
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
				socket.receive(packet);	// bloque le thread, attend la reception d'un packet datagramme
				MessageUser msgReceived = MessageUser.deserializeMessage(new String(packet.getData(), 0, packet.getLength())); // deserialiser le contenu du datagram packet recu
				usersModel.receivedMessageUser(msgReceived); // mise a jour de la liste des users suivant le message
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void setConnected(boolean connected){
		this.connected = connected;
	}
}
