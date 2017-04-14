import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastListener extends Thread{
	
	private DatagramSocket socket;
	private UsersModel usersModel;
	
	public MulticastListener(DatagramSocket socket, UsersModel um){
		this.socket = socket;
		this.usersModel = um;
	}
	
	public void run(){
		while(true){
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
				socket.receive(packet);	// bloque le thread, attend la reception d'un packet datagramme
				MessageUser msgReceived = new MessageUser("pseudo", InetAddress.getByName("1.1.1.1"), 1, MessageUser.typeConnect.CONNECTED); // deserialiser le contenu du datagram packet recu
				usersModel.receivedMessageUser(msgReceived); // mise a jour de la liste des users suivant le message
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}
}
