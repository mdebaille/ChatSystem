import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
	
	JTextArea taSend;
	JTextArea taReceived;
	
	ChatController chatController;
	SwingWorker worker;	// pour faire du traitement en parallele de l'interface swing (affichage des messages)
	
	public ChatIHM(String myPseudo, String destPseudo, ChatController chatController){
		System.out.println("création du chatIHM");
		this.myPseudo = myPseudo;
		this.destPseudo = destPseudo;
		this.chatController = chatController;
		
		// doInBackground() s'execute dans un background thread associe a l'interface swing
		this.worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				String lastLine = ""; 
				while(!isCancelled()){	
					lastLine = chatController.getLastLine(); // getLastLine() bloque tant qu'il n'y a pas de nouveau message à lire 
					if(!lastLine.equals("")){
						 System.out.println("Reçu: " +  lastLine);
						 ChatIHM.this.printMessage(destPseudo + ": " + lastLine + "\n");
					}
				}
				return null;
			}
		};
		
		chatController.setChatActive(true);
		initComponents();
		worker.execute(); // lancement de doInBackground()
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
		
		// actionPerfomed du bouton de fermeture de la fenetre
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		       worker.cancel(true); // isCancelled() va maintenant retourner true
		       chatController.setChatActive(false);
		    }
		});
	}
		
	public void bSendActionPerformed(ActionEvent e){
		/*try{
			for(int i = 0; i<5; i++){
				System.out.println("Envoi: Hello" + i);
				chatController.sendMessage("Hello" + i + "\n");
				printMessage(myPseudo + ": Hello" + i + "\n");
				Thread.sleep(1000);
			}
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}*/
		//System.out.println(chatController);
		chatController.sendMessage(taSend.getText() + "\n");
		printMessage(myPseudo + ": " + taSend.getText() + "\n");
		taSend.setText("");
	}
	
	protected void printMessage(String msg){
		taReceived.append(msg);
	}
	
	
}
