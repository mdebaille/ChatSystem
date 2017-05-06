import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class TestMulticastListener {

	private int portMulticast = 49150;
	private InetAddress group;
	private MulticastSocket multicastSocket;
	
	private MulticastListener multicastListener;
	private UsersModel usersModel;

	private InfoUser user1, user2, user3;
	private DatagramPacket packet1, packet2, packet3, packetDeco1, packetDeco2, packetDeco3;
	
	@Before
	public void initialize(){
		try {
			group = InetAddress.getByName("228.5.6.7");
			multicastSocket = new MulticastSocket(portMulticast);
			multicastSocket.joinGroup(group);
			
			usersModel = new UsersModel();
			multicastListener = new MulticastListener(multicastSocket, usersModel);
			
			user1 = new InfoUser("Pseudo 1", InetAddress.getByName("1.1.1.1"), 1);
			user2 = new InfoUser("Pseudo 2", InetAddress.getByName("1.1.1.2"), 2);
			user3 = new InfoUser("Pseudo 3", InetAddress.getByName("1.1.1.3"), 3);
			
			packet1 = createPacket(user1, MessageUser.typeConnect.CONNECTED);
			packet2 = createPacket(user2, MessageUser.typeConnect.CONNECTED);
			packet3 = createPacket(user3, MessageUser.typeConnect.CONNECTED);
			packetDeco1 = createPacket(user1, MessageUser.typeConnect.DISCONNECTED);
			packetDeco2 = createPacket(user2, MessageUser.typeConnect.DISCONNECTED);
			packetDeco3 = createPacket(user3, MessageUser.typeConnect.DISCONNECTED);

			multicastListener.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMulticastListener(){
		try {
			assertEquals(0, usersModel.getModelSize());
			
			System.out.println("Envoi de connexion: Pseudo 1");
			
			multicastSocket.send(packet1);
			Thread.sleep(1000);
			assertEquals(1, usersModel.getModelSize());
			assertTrue(compare(user1, usersModel.getUser(user1.getIP())));
			
			System.out.println("Envoi de connexion: Pseudo 2");
			
			multicastSocket.send(packet2);
			Thread.sleep(1000);
			assertEquals(2, usersModel.getModelSize());
			assertTrue(compare(user2, usersModel.getUser(user2.getIP())));
			
			System.out.println("Envoi de deconnexion: Pseudo 1");
			
			multicastSocket.send(packetDeco1);
			Thread.sleep(1000);
			assertEquals(1, usersModel.getModelSize());
			assertNull(usersModel.getUser(user1.getIP()));
			
			System.out.println("Envoi de deconnexion: Pseudo 2");
			
			multicastSocket.send(packetDeco2);
			Thread.sleep(1000);
			assertEquals(0, usersModel.getModelSize());
			assertNull(usersModel.getUser(user2.getIP()));
			
			System.out.println("Envoi de 3 connexions différentes \"d'un coup\"");
			
			multicastSocket.send(packet1);
			multicastSocket.send(packet2);
			multicastSocket.send(packet3);
			Thread.sleep(3000);
			assertEquals(3, usersModel.getModelSize());
			assertTrue(compare(user1, usersModel.getUser(user1.getIP())));
			assertTrue(compare(user2, usersModel.getUser(user2.getIP())));
			assertTrue(compare(user3, usersModel.getUser(user3.getIP())));
			
			System.out.println("Deconnexion de tous les utilisateurs");
			
			multicastSocket.send(packetDeco1);
			multicastSocket.send(packetDeco2);
			multicastSocket.send(packetDeco3);
			Thread.sleep(3000);
			assertEquals(0, usersModel.getModelSize());
			
			System.out.println("Envoi de 1000 connexions différentes \"d'un coup\"");
			
			for(int j = 0; j < 4; j++){
				for(int i = 0; i < 250 ; i++){
					InfoUser user = new InfoUser("Pseudo " + i, InetAddress.getByName("1.1." + j + "." + i), i);
					DatagramPacket packet = createPacket(user, MessageUser.typeConnect.CONNECTED);
					multicastSocket.send(packet);
				}
			}
			Thread.sleep(2000);
			assertEquals(1000, usersModel.getModelSize());
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private boolean compare(InfoUser info1, InfoUser info2){
		return info1.getPseudo().equals(info2.getPseudo()) && info1.getIP().equals(info2.getIP()) && info1.getPort() == info2.getPort();
	}
	
	private DatagramPacket createPacket(InfoUser info, MessageUser.typeConnect tc){
		MessageUser msg = new MessageUser(info.getPseudo(), info.getIP(), info.getPort(), tc);
		String message = MessageUser.serializeMessage(msg);
		return new DatagramPacket(message.getBytes(), message.length(), group, portMulticast);
	}
	
}
