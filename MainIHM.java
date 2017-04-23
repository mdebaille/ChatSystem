import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

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
	
	MainController mainController;
	
	int nbUsers;
	
	public MainIHM(MainController mainController){
		this.mainController = mainController;
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
	    		clickConnect();
	    	}
	    });
	}
	
	
private void changeFrameConnection(){
		
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
	    		clickDisconnect();
	    	}
	    });
	}

	private void changeFrameDisconnection(){
		
	}
	
	public void addUser(InfoUser info){
		JButton bName = new JButton(info.getPseudo());
		bName.setBackground(Color.white);
		
		//Seule methode que j'ai trouve pour associer au bouton d'un user son adresse ip (sans l'afficher)
		//Cette adresse ip sert à identifier de maniere unique le user qu'on selectionne pour se connecter a ce user la et pas un autre
		//Sinon il y a risque de creer un chatcontroller associe a un user possedant le meme pseudo (si on se base uniquement sur le pseudo)
		JLabel hiddenIP = new JLabel(info.getIP().getHostAddress()); 
		hiddenIP.setVisible(false);
		bName.add(hiddenIP);
		
		bName.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JButton button = (JButton)e.getSource();
	    		clickUser(button);
	    	}
		});
		nbUsers++;
		pList.setLayout(new GridLayout(nbUsers,1));
		pList.add(bName);
		pList.revalidate();
		pList.repaint();
	}
	
	public void removeUser(InfoUser info){
		nbUsers--;
		pList.setLayout(new GridLayout(nbUsers,1));
		for (int i=0; i<pList.getComponentCount(); i++){
			JButton b = (JButton)pList.getComponent(i);
			JLabel l = (JLabel)b.getComponent(0);
			if(l.getText().equals(info.getIP().getHostAddress())){
				pList.remove(i);
			}
		}
		pList.revalidate();
		pList.repaint();
	}

	private void clickConnect(){
		myUsername = taUsername.getText();
		if(myUsername.contains("#")){
			System.out.println("Le pseudo ne doit pas contenir le caract�re '#'");
		}else{
			changeFrameConnection();
			mainController.startChatsystem(myUsername);	
		}
	}
	
	private void clickDisconnect(){
		mainController.Disconnect();
		changeFrameDisconnection();
	}
	
	private void clickUser(JButton b){
		JLabel ipLabel = (JLabel)b.getComponent(0);
		mainController.openChat(ipLabel.getText());
		System.out.println(ipLabel.getText());
		if(b.getBackground().equals(Color.decode("#99ff66"))){
			b.setBackground(Color.white);
		}
	}
	
	public void notifyNewMessage(InetAddress ip){
		for (int i=0; i<pList.getComponentCount(); i++){
			JButton b = (JButton)pList.getComponent(i);
			JLabel l = (JLabel) b.getComponent(0);
			if(l.getText().equals(ip.getHostAddress()) && b.getBackground().equals(Color.white)){
				b.setBackground(Color.decode("#99ff66"));
			}
		}
	}
}
