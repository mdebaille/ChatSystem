import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


public class TestMessageListener {

	private MessagesModel messagesModel;
	private ChatIHM chatIHM;
	private MessageListener messageListener;
	private SingleChatController chatControllerDest;
	private DataInputStream is;
	private OutputStream os;
	
	private Socket inSocket, outSocket;
	
	private Message msg1, msg2, msgErreur, msgFile;
	
	@Before
	public void initialize(){		
		try {
			msg1 = new Message(false, 4, ("aaaa").getBytes());
			msg2 = new Message(false, 4, ("bbbb").getBytes());
			msgErreur = new Message(false, 4, ("cccccc").getBytes());
			msgFile = new Message(true, 18, ("je suis un fichier").getBytes());
			
			chatControllerDest = mock(SingleChatController.class);
			when(chatControllerDest.getInfoDest()).thenReturn(new InfoUser("PseudoEmetteur", InetAddress.getLocalHost(), 49150));
			
			chatIHM = mock(ChatIHM.class);
			
			messagesModel = new MessagesModel();
			messagesModel.setChatActive(true);
			messagesModel.addObserver(chatIHM);

		
			//----Simulation de connexion----
			
			ConnexionListener connexionListener = new ConnexionListener(new ServerSocket(49150));
			connexionListener.start();
			
			Thread.sleep(1000);
			
			outSocket = new Socket(InetAddress.getLocalHost(), 49150);
			
			Thread.sleep(1000);
			
			inSocket = connexionListener.socket;
			
			//------------------------------
			
			os = outSocket.getOutputStream();
			is = new DataInputStream(inSocket.getInputStream());
			
			messageListener = new MessageListener(is, chatControllerDest, messagesModel);	
			messageListener.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testReadData(){
		try {
			System.out.println("---Test readData()---");
			assertEquals(0, messagesModel.getModelSize());
			
			//----------------------------------------------------
			
			System.out.println("Envoi d'un message texte");
			
			sendMessage(msg1);
			Thread.sleep(1000);
			assertEquals(1, messagesModel.getModelSize());
			verify(chatIHM).updateMessage(eq("PseudoEmetteur: aaaa".getBytes()));
			
			//----------------------------------------------------
			
			System.out.println("Envoi d'un message texte");
			
			sendMessage(msg2);
			Thread.sleep(1000);
			assertEquals(2, messagesModel.getModelSize());
			verify(chatIHM).updateMessage(eq("PseudoEmetteur: bbbb".getBytes()));
			
			//----------------------------------------------------
			
			System.out.println("Envoi d'un message fichier");
			
			sendMessage(msgFile);
			Thread.sleep(1000);
			assertEquals(3, messagesModel.getModelSize());
			verify(chatIHM).updateMessage(eq("File received.".getBytes()));
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test(expected = java.lang.OutOfMemoryError.class)
	public void testReadDataError(){
		try {
			System.out.println("---Test readData() erreur---");
			assertEquals(0, messagesModel.getModelSize());
			
			//----------------------------------------------------
			
			System.out.println("Envoi d'un message texte");
			
			sendMessage(msg1);
			Thread.sleep(1000);
			assertEquals(1, messagesModel.getModelSize());
			verify(chatIHM).updateMessage(eq("PseudoEmetteur: aaaa".getBytes()));
			
			//----------------------------------------------------
			
			System.out.println("Envoi d'un message texte dont la taille envoyée est inférieure à la taille réelle du texte");
			
			sendMessage(msgErreur);
			Thread.sleep(1000);
			assertEquals(2, messagesModel.getModelSize());
			verify(chatIHM).updateMessage(eq("PseudoEmetteur: cccc".getBytes()));
			
			//----------------------------------------------------
			
			System.out.println("Envoi d'un message texte");
			
			sendMessage(msg1);
			Thread.sleep(1000);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	// Classe pour simuler une connexion
	
	private class ConnexionListener extends Thread{
		public ServerSocket serverSocket;
		public Socket socket;
		
		public ConnexionListener(ServerSocket serverSocket){
			this.serverSocket = serverSocket;
		}
		
		public void run(){
			try {
				System.out.println("Attente de connexion");
				socket = serverSocket.accept();
				System.out.println("Connexion réussie");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendMessage(Message message){
		try {
			byte[] serializedMessage = Message.serializeMessage(message);
			os.write(serializedMessage, 0, serializedMessage.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

