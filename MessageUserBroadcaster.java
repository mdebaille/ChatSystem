import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MessageUserBroadcaster extends Thread{
	
	private static int sendDelay = 2000; // 2s
	private MulticastSocket multicastSocket;
	private DatagramPacket packet;
	private boolean connected;
	
	public MessageUserBroadcaster(MulticastSocket multicastSocket, String myPseudo, InetAddress myIP, int myPort, InetAddress group, int portMulticast){
		this.multicastSocket = multicastSocket;
		MessageUser mess = new MessageUser(myPseudo, myIP, myPort, MessageUser.typeConnect.CONNECTED);
		String message = MessageUser.serializeMessage(mess);
		this.packet = new DatagramPacket(message.getBytes(), message.length(), group, portMulticast);
	}
	
	public void run(){
		while(true){
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
	
	
}
