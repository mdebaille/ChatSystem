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
 * notification qd message reÃ§u d'un certain user
 */


public class MainIHM extends JFrame {
	
	String myUsername;
	
	JPanel pInputUsername;
	JPanel pConnect;
	JPanel pList;
	
	JTextArea taUsername;
	
	Chatsystem chatsystem;
	
	int nbUsers;
	
	public MainIHM(Chatsystem chatsystem){
		this.chatsystem = chatsystem;
		this.nbUsers = 0;
		initComponents();
	}
	
	private void initComponents(){
		
		this.setTitle("Chat System");
		
		pInputUsername = new JPanel();
		pInputUsername.setLayout(new BoxLayout(pInputUsername, BoxLayout.X_AXIS));
		pInputUsername.setBorder(new EmptyBorder(20, 20, 20, 20));	
		JLabel lUsername = new JLabel("Username: ");
		lUsername.setBorder(new EmptyBorder(0,10,0,10));
		taUsername = new JTextArea(1,15);
		pInputUsername.add(lUsername);
		pInputUsername.add(taUsername);
		
		pConnect = new JPanel(new BorderLayout());
		pConnect.setBorder(new EmptyBorder(10, 60, 20, 60));
		JButton bConnect = new JButton("Connect");
		pConnect.add(bConnect, BorderLayout.CENTER);
		
		
		this.setLayout(new GridLayout(2, 1));
		this.add("1", pInputUsername);
		this.add("2", pConnect);
		
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		bConnect.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		bConnectActionPerformed(e);
	    	}
	    });
	}
	
	
public void changeFrame(){
		
		this.setVisible(false);

		GridBagConstraints cUsername = new GridBagConstraints();
		cUsername.gridx = 0;
		cUsername.gridy = 0;
		cUsername.fill = GridBagConstraints.HORIZONTAL;
		JLabel lMyUsername = new JLabel(myUsername, JLabel.CENTER);
		lMyUsername.setForeground(Color.MAGENTA);
		lMyUsername.setBorder(new EmptyBorder(20,20,20,20));

		GridBagConstraints cList = new GridBagConstraints();
		cList.gridx = 0;
		cList.gridy = 1;
		cList.fill = GridBagConstraints.HORIZONTAL;
		pList = new JPanel();
		pList.setLayout(new GridLayout(nbUsers,1));
		JScrollPane scrollList = new JScrollPane(pList);
		scrollList.setPreferredSize(new Dimension(100,200));
		
		GridBagConstraints cDisconnect = new GridBagConstraints();
		cDisconnect.gridx = 0;
		cDisconnect.gridy = 2;
		cDisconnect.fill = GridBagConstraints.HORIZONTAL;
		JPanel pDisconnect = new JPanel(new BorderLayout());
		pDisconnect.setBorder(new EmptyBorder(20, 60, 20, 60));
		JButton bDisconnect = new JButton("Disconnect");
		pDisconnect.add(bDisconnect, BorderLayout.CENTER);
		
		
		this.remove(pInputUsername);
		this.remove(pConnect);
		
		this.setLayout(new GridBagLayout());;
		this.add(lMyUsername, cUsername);
		this.add(scrollList, cList);
		this.add(pDisconnect, cDisconnect);
		
		this.pack();
		this.setVisible(true);
		
		bDisconnect.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		bDisconnectActionPerformed(e);
	    	}
	    });
	}
	
	public void addUser(InfoUser info){
		JButton bName = new JButton(info.getPseudo());
		bName.setBackground(Color.white);
		
		//Seule methode que j'ai trouve pour associer au bouton d'un user son adresse ip (sans l'afficher)
		//Cette adresse ip sert à identifier de maniere unique le user qu'on selectionne pour se connecter a ce user la et pas un autre
		//Sinon il y a risque de creer un chatcontroller associe a un user possedant le meme pseudo (si on se base uniquement sur le pseudo)
		JLabel hiddenIP = new JLabel(info.getIP().getHostName()); 
		hiddenIP.setVisible(false);
		bName.add(hiddenIP);
		
		bName.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
	    		clickUser(e);
	    	}
		});
		nbUsers++;
		pList.setLayout(new GridLayout(nbUsers,1));
		pList.add(bName);
		pList.revalidate();
		pList.repaint();
	}
	
	public void removeUser(String pseudo){
		nbUsers--;
		pList.setLayout(new GridLayout(nbUsers,1));
		for (int i=0; i<pList.getComponentCount(); i++){
			JButton b = (JButton)pList.getComponent(i);
			if(b.getText().equals(pseudo)){
				pList.remove(i);
			}
		}
		pList.revalidate();
		pList.repaint();
	}

	private void bConnectActionPerformed(ActionEvent e){
		myUsername = taUsername.getText();
		chatsystem.startChatsystem(myUsername);
	}
	
	private void bDisconnectActionPerformed(ActionEvent e){
		chatsystem.Disconnect();
	}
	
	private void clickUser(ActionEvent e){
		JButton b = (JButton) e.getSource();
		JLabel ipLabel = (JLabel)b.getComponent(0);
		chatsystem.openChat(ipLabel.getText());
		System.out.println(ipLabel.getText());
	}
}
