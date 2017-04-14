import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/*
 * FenÃªtre pour communiquer avec un utilisateur
 * a l'ouverture de la fenetre de chat => lancer la lecture des messages recus (messages recus pendant que chatIHM etait fermee, 
 * et messages qui vont etre recus) => lecture depuis le bufferedreader de chatcontroller avec getLastLine()
 *   Genre:
 *   	String lastline;
 *   	while(true){		 
 *   		lastline = chatController.getLastLine(); // getLastLine() bloque tant qu'il n'y a pas de nouveau message à lire
 *   		afficher_le_msg_dans_IHM(lastline); 
 *   	}
 *   (quick note: Tel quel, si on ferme la fenetre de chat et qu'on la re-ouvre plus tard, le chat ne se rappellera pas des messages
 *   qui ont ete affiches avant la fermeture, on re-ouvre une chatIHM vierge à chaque fois. Il y aurait possibilite d'ajouter
 *   une memoire de messages à chaque chatcontroller pour pouvoir suivre les conversations en entier meme apres fermeture des chatIHM.)
 *   
 */

public class ChatIHM extends JFrame{

	String destPseudo;
	String myPseudo;
	
	ChatController chatController;
	
	JTextArea taSend;
	JTextArea taReceived;
	
	public ChatIHM(String destPseudo, ChatController chatController){
		System.out.println("création du chatIHM");
		this.destPseudo = destPseudo;
		this.chatController = chatController;
		initComponents();
	}

	private void initComponents(){
		this.setTitle("Chat with " + destPseudo);
		this.setPreferredSize(new Dimension(700,700));
		
		JPanel pReceived = new JPanel();
		pReceived.setBorder(new EmptyBorder(10,10,10,10));
		taReceived = new JTextArea(30,50);
		taReceived.setEditable(false);
		pReceived.add(taReceived);
		JScrollPane scrollReceived = new JScrollPane(pReceived, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel pSend = new JPanel();
		pSend.setLayout(new GridBagLayout());
		scrollReceived.setBorder(new EmptyBorder(10,10,10,10));
		

		
		GridBagConstraints cTA = new GridBagConstraints();
		taSend = new JTextArea(3,50);
		JScrollPane scrollSend = new JScrollPane(taSend, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cTA.gridx = 0;
		cTA.gridy = 0;
		cTA.fill = GridBagConstraints.VERTICAL;
		

		
		GridBagConstraints cB = new GridBagConstraints();
		JButton bSend = new JButton("Send");
		cB.gridx = 1;
		cB.gridy = 0;
		cB.weightx = 0.1;
		cB.fill = GridBagConstraints.VERTICAL;
		
		pSend.add(scrollSend, cTA);
		pSend.add(bSend, cB);
		
		
		
		bSend.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		bSendActionPerformed(e);
	    	}
		});
		
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints cScroll = new GridBagConstraints();
		cScroll.gridx = 0;
		cScroll.gridy = 0;
		cScroll.fill = GridBagConstraints.HORIZONTAL;
		GridBagConstraints cSend = new GridBagConstraints();
		cSend.gridx = 0;
		cSend.gridy = 1;
		cSend.weighty = 0.2;
		cSend.fill = GridBagConstraints.HORIZONTAL;
		this.add(scrollReceived, cScroll);
		this.add(pSend, cSend);
		
		this.pack();
		this.setVisible(true);
		
	}
		
	public void bSendActionPerformed(ActionEvent e){
		//System.out.println(chatController);
		//chatController.sendMessage(taSend.getText());
		//taSend.setText("");
		
	}
}
