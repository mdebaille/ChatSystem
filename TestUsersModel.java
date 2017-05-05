import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

//@RunWith(Parameterized.class)
public class TestUsersModel{
	
	private static InetAddress ip1;
	private static InetAddress ip2;
	private static InetAddress ip3;
	private static InetAddress myIp; 
	
	private static InfoUser user1;
	private static InfoUser user2;
	
	private InfoUser user;

	private UsersModel usersModel;
	private MessageUser messageReceived;
	
	@BeforeClass
	public static void setUp(){
		try {
			ip1 = InetAddress.getByName("1.1.1.1");
			ip2 = InetAddress.getByName("1.1.1.2");
			ip3 = InetAddress.getByName("1.1.1.3");
			myIp = InetAddress.getLocalHost();
			
			user1 = new InfoUser("user1", ip1, 1);
			user2 = new InfoUser("user2", ip2, 2); 
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		} 
	}
	
	@Before
	public void initalize(){
		usersModel = new UsersModel();
	}
	
	@Test
	public void testReceivedMessageUser(){
		System.out.println("---Test receivedMessageUser()---");
		
		System.out.println("Reception de connexion user1 #1");
		
		messageReceived = new MessageUser("user1", ip1, 1, MessageUser.typeConnect.CONNECTED);
		usersModel.receivedMessageUser(messageReceived);
		user = usersModel.getUser(ip1);
		
		assertTrue(compare(user1, user));
		assertEquals(1, usersModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Reception de connexion user2 #1");
		
		messageReceived = new MessageUser("user2", ip2, 2, MessageUser.typeConnect.CONNECTED);
		usersModel.receivedMessageUser(messageReceived);
		user = usersModel.getUser(ip2);
		
		assertTrue(compare(user2, user));
		assertEquals(2, usersModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Reception de connexion user2 #2");
		
		messageReceived = new MessageUser("user2", ip2, 2, MessageUser.typeConnect.CONNECTED);
		usersModel.receivedMessageUser(messageReceived);
		user = usersModel.getUser(ip2);
		
		assertTrue(compare(user2, user));
		assertEquals(2, usersModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Reception de deconnexion user2 #1");
		
		messageReceived = new MessageUser("user2", ip2, 2, MessageUser.typeConnect.DISCONNECTED);
		usersModel.receivedMessageUser(messageReceived);
		user = usersModel.getUser(ip2);
		
		assertNull(user);
		assertEquals(1, usersModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Reception de deconnexion user2 #2");
		
		messageReceived = new MessageUser("user2", ip2, 2, MessageUser.typeConnect.DISCONNECTED);
		usersModel.receivedMessageUser(messageReceived);
		user = usersModel.getUser(ip2);
		
		assertNull(user);
		assertEquals(1, usersModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Reception de connexion user1 #2 mais avec erreur sur le pseudo");
		
		messageReceived = new MessageUser("errorPseudo", ip1, 1, MessageUser.typeConnect.CONNECTED);
		usersModel.receivedMessageUser(messageReceived);
		user = usersModel.getUser(ip1);
		
		assertTrue(compare(user1, user));
		assertEquals(1, usersModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Reception de deconnexion user1 #1 mais avec erreur sur le pseudo");
		
		messageReceived = new MessageUser("errorPseudo", ip1, 1, MessageUser.typeConnect.DISCONNECTED);
		usersModel.receivedMessageUser(messageReceived);
		user = usersModel.getUser(ip1);
		
		assertNull(user);
		assertEquals(0, usersModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Reception de notre connexion");
		
		messageReceived = new MessageUser("Nous", myIp, 1, MessageUser.typeConnect.CONNECTED);
		usersModel.receivedMessageUser(messageReceived);
		user = usersModel.getUser(myIp);
		
		assertNull(user);
		assertEquals(0, usersModel.getModelSize());
	}
	
	@Test
	public void testUpdateList(){
		System.out.println("---Test TimerTask---");
		
		System.out.println("Reception de connexion user1 x1");
		
		messageReceived = new MessageUser("user1", ip1, 1, MessageUser.typeConnect.CONNECTED);
		usersModel.receivedMessageUser(messageReceived);
		
		assertEquals(1, usersModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Attente de 6s");
		
		try { Thread.sleep(6000); } catch (InterruptedException e) { System.out.println(e.getMessage());}
		
		assertEquals(1, usersModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Reception de connexion user2 x3");
		
		for(int i = 0; i < 3; i++){
			messageReceived = new MessageUser("user2", ip2, 2, MessageUser.typeConnect.CONNECTED);
			usersModel.receivedMessageUser(messageReceived);
		}
		
		System.out.println("Attente de 6s");
		
		try { Thread.sleep(6000); } catch (InterruptedException e) { System.out.println(e.getMessage());}
		
		assertEquals(1, usersModel.getModelSize());
		assertNull(usersModel.getUser(ip1));
		assertTrue(compare(user2, usersModel.getUser(ip2)));
		
		//----------------------------------------------------------------------
		
		System.out.println("Attente de 6s");
		
		try { Thread.sleep(6000); } catch (InterruptedException e) { System.out.println(e.getMessage());}
		
		assertEquals(0, usersModel.getModelSize());
	}
	
	private boolean compare(InfoUser info1, InfoUser info2){
		return info1.getPseudo().equals(info2.getPseudo()) && info1.getIP().equals(info2.getIP()) && info1.getPort() == info2.getPort();
	}
	
	
}

