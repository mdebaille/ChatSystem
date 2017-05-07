import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


import static org.mockito.Mockito.*;

public class TestMessagesModel {

	private Message msg1;
	private Message msg2;
	private Message msg3;
	private Message msg4;
	private Message msg5;
	private Message msg6;
	private Message msg7;
	
	private MessagesModel messagesModel;
	private ChatIHM chatIHM1;
	private ChatIHM chatIHM2;
	
	@Before
	public void initialize(){
		msg1 = new Message(false, 4, ("aaaa").getBytes());
		msg2 = new Message(false, 4, ("bbbb").getBytes());
		msg3 = new Message(false, 4, ("cccc").getBytes());
		msg4 = new Message(false, 4, ("dddd").getBytes());
		msg5 = new Message(false, 4, ("eeee").getBytes());
		msg6 = new Message(false, 4, ("ffff").getBytes());
		msg7 = new Message(false, 4, ("gggg").getBytes());

		chatIHM1 = mock(ChatIHM.class);
		chatIHM2 = mock(ChatIHM.class);
		messagesModel = new MessagesModel();
	}
	
	
	@Test
	public void testAdd1000Message(){
		
		System.out.println("Ajout de 1000 messages avec fen�tre de chat ferm�e");

		messagesModel.setChatActive(false);
		for(int i = 0; i < 1000; i++){
			messagesModel.addMessage(msg1);
		}
		
		assertEquals(1000, messagesModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Ouverture d'une fen�tre de chat");
		
		messagesModel.setChatActive(true);
		messagesModel.addObserver(chatIHM1);
		
		assertEquals(1000, messagesModel.getModelSize());
		verify(chatIHM1, atLeast(1000)).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM1, atMost(1000)).updateMessage(eq("aaaa".getBytes()));
		
		//----------------------------------------------------------------------
		
		System.out.println("Ajout de 1000 messages avec fen�tre de chat ouverte");

		for(int i = 0; i < 1000; i++){
			messagesModel.addMessage(msg1);
		}
		
		assertEquals(2000, messagesModel.getModelSize());
		verify(chatIHM1, atLeast(2000)).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM1, atMost(2000)).updateMessage(eq("aaaa".getBytes()));
		
		//----------------------------------------------------------------------
		
		System.out.println("Ajout d'un nouvel ObserverMessages sur le mod�le");
		
		messagesModel.addObserver(chatIHM2);
		
		assertEquals(2000, messagesModel.getModelSize());
		verify(chatIHM1, atLeast(4000)).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM1, atMost(4000)).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM2, atLeast(2000)).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM2, atMost(2000)).updateMessage(eq("aaaa".getBytes()));
		
		//----------------------------------------------------------------------
		
		System.out.println("Ajout de 1000 messages avec fen�tre de chat ouverte");

		for(int i = 0; i < 1000; i++){
			messagesModel.addMessage(msg1);
		}
		
		assertEquals(3000, messagesModel.getModelSize());
		verify(chatIHM1, atLeast(5000)).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM1, atMost(5000)).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM2, atLeast(3000)).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM2, atMost(3000)).updateMessage(eq("aaaa".getBytes()));
	}
	
	
	@Test
	public void testAddMessage(){
		
		System.out.println("Ajout de 4 messages avec fen�tre de chat ferm�e");

		messagesModel.setChatActive(false);
		messagesModel.addMessage(msg1);
		messagesModel.addMessage(msg2);
		messagesModel.addMessage(msg3);
		
		assertEquals(3, messagesModel.getModelSize());
		
		//----------------------------------------------------------------------
		
		System.out.println("Ouverture d'une fen�tre de chat");
		
		messagesModel.setChatActive(true);
		messagesModel.addObserver(chatIHM1);
		
		assertEquals(3, messagesModel.getModelSize());
		verify(chatIHM1).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM1).updateMessage(eq("bbbb".getBytes()));
		verify(chatIHM1).updateMessage(eq("cccc".getBytes()));
		//verify(chatIHM1, times(3)).updateMessage(any());
		
		//----------------------------------------------------------------------
		
		System.out.println("Ajout de 2 messages avec fen�tre de chat ouverte");

		messagesModel.addMessage(msg4);
		messagesModel.addMessage(msg5);
		
		assertEquals(5, messagesModel.getModelSize());
		verify(chatIHM1).updateMessage(eq("dddd".getBytes()));
		verify(chatIHM1).updateMessage(eq("eeee".getBytes()));
		
		//----------------------------------------------------------------------
		
		System.out.println("Ajout d'un nouvel ObserverMessages sur le mod�le");
		
		messagesModel.setChatActive(true);
		messagesModel.addObserver(chatIHM2);
		
		verify(chatIHM2).updateMessage(eq("aaaa".getBytes()));
		verify(chatIHM2).updateMessage(eq("bbbb".getBytes()));
		verify(chatIHM2).updateMessage(eq("cccc".getBytes()));
		verify(chatIHM2).updateMessage(eq("dddd".getBytes()));
		verify(chatIHM2).updateMessage(eq("eeee".getBytes()));
		//verify(chatIHM2, times(5)).updateMessage(any());
		
		//----------------------------------------------------------------------
		
		System.out.println("Ajout de 2 messages avec fen�tre de chat ouverte");

		messagesModel.addMessage(msg6);
		messagesModel.addMessage(msg7);
		
		assertEquals(7, messagesModel.getModelSize());
		verify(chatIHM1).updateMessage(eq("ffff".getBytes()));
		verify(chatIHM1).updateMessage(eq("gggg".getBytes()));
		verify(chatIHM2).updateMessage(eq("ffff".getBytes()));
		verify(chatIHM2).updateMessage(eq("gggg".getBytes()));
	}
	
	
}
