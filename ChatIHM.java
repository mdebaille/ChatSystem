
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;


import javax.swing.*;
import javax.swing.border.EmptyBorder;

/*
 * Classe qui repr�sente la vue du MVC avec MessageModel et ChatController
 * Fenetre pour communiquer avec un  ou plusieurs utilisateurs  
 */

public class ChatIHM extends JFrame implements ObserverMessages{

	String destPseudo;
	String myPseudo;
	
	JTextArea taSend;
	JTextArea taReceived;
	
	ChatController chatController;
	
	public ChatIHM(String myPseudo, final String destPseudo, final ChatController chatController){
		System.out.println("cr�ation du chatIHM");
		this.myPseudo = myPseudo;
		this.destPseudo = destPseudo;
		this.chatController = chatController;
		initComponents();
		// on notifie le contr�leur que la fen�tre de chat � �t� ouverte
		chatController.setChatActive(true, this);
	}

	private void initComponents(){
		this.setTitle("Chat with " + destPseudo);
		this.setPreferredSize(new Dimension(700,700));
		
		// Zone d'affichage des messages envoy�s et re�us
		JPanel pReceived = new JPanel();
		pReceived.setBorder(new EmptyBorder(10,10,10,10));
		taReceived = new JTextArea(30,50);
		taReceived.setEditable(false);
		pReceived.add(taReceived);
		JScrollPane scrollReceived = new JScrollPane(pReceived, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		// Zone pour envoyer des messages
		JPanel pSend = new JPanel();
		pSend.setLayout(new GridBagLayout());
		scrollReceived.setBorder(new EmptyBorder(10,10,10,10));
		
		// Zone de texte o� saisir le message � envoyer
		GridBagConstraints cTA = new GridBagConstraints();
		taSend = new JTextArea(3,50);
		JScrollPane scrollSend = new JScrollPane(taSend, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cTA.gridx = 0;
		cTA.gridy = 0;
		cTA.fill = GridBagConstraints.VERTICAL;
		
		// Bouton pour envoyer un message
		GridBagConstraints cB = new GridBagConstraints();
		JButton bSend = new JButton("Send");
		cB.gridx = 1;
		cB.gridy = 0;
		cB.weightx = 0.1;
		cB.fill = GridBagConstraints.VERTICAL;
		
		// Bouton pour envoyer un fichier
		GridBagConstraints cF = new GridBagConstraints();
		JButton bFile = new JButton("File");
		cF.gridx = 2;
		cF.gridy = 0;
		cF.weightx = 0.1;
		cF.fill = GridBagConstraints.VERTICAL;
		
		pSend.add(scrollSend, cTA);
		pSend.add(bSend, cB);
		pSend.add(bFile, cF);
	
		// Listener sur le bouton pour envoyer un message
		bSend.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		clickSend();
	    	}
		});
		
		// Listener sur le bouton pour envoyer un fichier
		bFile.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		clickSelectFile();
	    	}
		});
		
		// Placement des deux zones principales dans la fen�tre
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
		
		// Listener sur le bouton de fermeture de la fenetre
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	// on notifie le contr�leur que le fen�tre de chat est maintenant ferm�e
		       chatController.setChatActive(false, ChatIHM.this);
		    }
		});
	}
		
	public void clickSend(){
		// Lorsque l'on clique sur le bouton pour envoyer un message, le contr�leur est notifi� avec le contenu du texte � envoyer
		chatController.sendMessage(new Message(false, taSend.getText().length(), taSend.getText().getBytes()));
		// On efface le contenu de la zone o� on saisi les nouveaux messages
		taSend.setText("");
	}
	
	private void clickSelectFile(){
		// ouverture d'une fenetre de selection de fichier
		final JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(this);
	    
		// copie du fichier dans un tableau d'octet et envoi
	    try {
	    	byte[] fileData = new byte[(int) fc.getSelectedFile().length()];
	        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fc.getSelectedFile()));
			bis.read(fileData, 0, fileData.length);
			chatController.sendMessage(new Message(true, fileData.length, fileData));
	    } catch (IOException e) {	
			e.printStackTrace();
		}
	}

	public void updateMessage(byte[] message){
		// quand un nouveau message est re�u, on l'affiche dans a zone pour afficher les messages
		taReceived.append(new String(message) + "\n");
	}
	
	public void close(){
		this.dispose();
	}
	
	
}
