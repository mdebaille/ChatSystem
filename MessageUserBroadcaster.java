import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/*
 * Thread qui g�re l'envoi de MessageUser pour notifier que l'utilisateur est connect�
 * Cet envoi se fait r�guli�rement,
 * De cette mani�re, si les autres ne re�oivent plus ces messages, ils savent que l'utilisateur est maintenant d�connect�
 * (m�me si n'a pas eu l'occasion d'envoyer un message de d�connexion)
 */

public class MessageUserBroadcaster extends Thread{
	// d�lai entre chaque envoi de MessageUser
	private static int sendDelay = 2000; // 2s
	private MulticastSocket multicastSocket;
	private DatagramPacket packet;
	private boolean connected;
	
	public MessageUserBroadcaster(MulticastSocket multicastSocket, String myPseudo, InetAddress myIP, int myPort, InetAddress group, int portMulticast){
		this.multicastSocket = multicastSocket;
		MessageUser mess = new MessageUser(myPseudo, myIP, myPort, MessageUser.typeConnect.CONNECTED);
		String message = MessageUser.serializeMessage(mess);
		this.packet = new DatagramPacket(message.getBytes(), message.length(), group, portMulticast);
		this.connected = true;
	}
	
	public void run(){
		while(connected){
			try{
				multicastSocket.send(packet);
			}catch (IOException e){
				System.out.println(e.getMessage());
			}
			try {
				Thread.sleep(sendDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void setConnected(boolean connected){
		this.connected = connected;
	}
	
}
