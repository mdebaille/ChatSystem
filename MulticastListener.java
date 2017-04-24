import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
				//System.out.println(new String(packet.getData(), 0, packet.getLength()));
				MessageUser msgReceived = deserializeMessage(new String(packet.getData(), 0, packet.getLength())); // deserialiser le contenu du datagram packet recu
				usersModel.receivedMessageUser(msgReceived); // mise a jour de la liste des users suivant le message
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
		}
	}
	
	public MessageUser deserializeMessage(String message){
		String[] champs = message.split("#");
		try {
			MessageUser.typeConnect etat;
			if(champs[3].equals("CONNECTED")){
				etat = MessageUser.typeConnect.CONNECTED;
			}else{
				etat = MessageUser.typeConnect.DISCONNECTED;
			}
			/*System.out.println("Pseudo: " + champs[0]);
			System.out.println("IP: " + champs[1]);
			System.out.println("Port: " + champs[2]);
			System.out.println("Etat: " + champs[3]);*/
			return new MessageUser(champs[0], InetAddress.getByName(champs[1]), Integer.parseInt(champs[2]), etat);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return new MessageUser(champs[0], null, Integer.parseInt(champs[2]), MessageUser.typeConnect.DISCONNECTED);
		}
	}
	
	public void setConnected(boolean connected){
		this.connected = connected;
	}
}
