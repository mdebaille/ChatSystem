import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/*
 * notification qd message reÃ§u d'un certain user
 */




public class MainIHM extends JFrame implements ObserverListUsers {
	
	String myUsername;
	
	JPanel pInputUsername;
	JPanel pConnect;
	JPanel pList;
	
	JTextArea taUsername;
	
	JLabel lMyUsername;
	JScrollPane scrollList;
	JButton bSendGroup;
	JPanel pSendGroup;
	JPanel pDisconnect;
	
	MainController mainController;
	
	int nbUsers;
	
	ArrayList<UserId> listGroup;
	
	
	public MainIHM(){
		this.nbUsers = 0;
		listGroup = new ArrayList<UserId>();
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
		lMyUsername = new JLabel(myUsername, JLabel.CENTER);
		lMyUsername.setForeground(Color.MAGENTA);
		lMyUsername.setBorder(new EmptyBorder(20,20,20,20));

		GridBagConstraints cList = new GridBagConstraints();
		cList.gridx = 0;
		cList.gridy = 1;
		cList.fill = GridBagConstraints.HORIZONTAL;
		pList = new JPanel();
		pList.setLayout(new GridLayout(nbUsers,1));
		scrollList = new JScrollPane(pList);
		scrollList.setPreferredSize(new Dimension(100,200));
		
		GridBagConstraints cGroup = new GridBagConstraints();
		cGroup.gridx = 0;
		cGroup.gridy = 2;
		cGroup.fill = GridBagConstraints.HORIZONTAL;
		pSendGroup = new JPanel(new BorderLayout());
		pSendGroup.setBorder(new EmptyBorder(20, 60, 10, 60));
		bSendGroup = new JButton("Send to group");
		bSendGroup.setEnabled(false);
		pSendGroup.add(bSendGroup, BorderLayout.CENTER);
		
		GridBagConstraints cDisconnect = new GridBagConstraints();
		cDisconnect.gridx = 0;
		cDisconnect.gridy = 3;
		cDisconnect.fill = GridBagConstraints.HORIZONTAL;
		pDisconnect = new JPanel(new BorderLayout());
		pDisconnect.setBorder(new EmptyBorder(10, 60, 20, 60));
		JButton bDisconnect = new JButton("Disconnect");
		pDisconnect.add(bDisconnect, BorderLayout.CENTER);
		
		
		
		
		this.remove(pInputUsername);
		this.remove(pConnect);
		
		this.setLayout(new GridBagLayout());;
		this.add(lMyUsername, cUsername);
		this.add(scrollList, cList);
		this.add(pSendGroup, cGroup);
		this.add(pDisconnect, cDisconnect);
		
		this.pack();
		this.setVisible(true);
		
		bDisconnect.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		clickDisconnect();
	    	}
	    });
		
		bSendGroup.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		clickSendGroup();
	    	}
	    });
	}

	private void changeFrameDisconnection(){
		this.remove(lMyUsername);
		this.remove(scrollList);
		this.remove(pSendGroup);
		this.remove(pDisconnect);
		initComponents();
	}
	
	public void addUser(InfoUser info){
		JPanel pUser = new JPanel(new FlowLayout());
		
		JCheckBox cb = new JCheckBox();
		cb.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
	    		selectUser(e);
	    	}
		});
		
		JButton bName = new JButton(info.getPseudo());
		bName.setBackground(Color.white);
		
		//Seule methode que j'ai trouve pour associer au bouton d'un user son adresse ip (sans l'afficher)
		//Cette adresse ip sert à identifier de maniere unique le user qu'on selectionne pour se connecter a ce user la et pas un autre
		//Sinon il y a risque de creer un chatcontroller associe a un user possedant le meme pseudo (si on se base uniquement sur le pseudo)
		JLabel hiddenIP = new JLabel(info.getIP().getHostAddress()); 
		hiddenIP.setVisible(false);
		bName.add(hiddenIP);
		
		JLabel hiddenPort = new JLabel(Integer.toString(info.getPort())); 
		hiddenPort.setVisible(false);
		bName.add(hiddenPort);
		
		
		bName.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JButton button = (JButton)e.getSource();
	    		clickUser(button);
	    	}
		});
		
		pUser.add(cb);
		pUser.add(bName);
		
		nbUsers++;
		pList.setLayout(new GridLayout(nbUsers,1));
		pList.add(pUser);
		pList.revalidate();
		pList.repaint();
	}
	
	public void removeUser(UserId id){
		nbUsers--;
		pList.setLayout(new GridLayout(nbUsers,1));
		for (int i=0; i<pList.getComponentCount(); i++){
			JPanel p = (JPanel)pList.getComponent(i);
			JButton b = (JButton)p.getComponent(1);
			JLabel lIp = (JLabel)b.getComponent(0);
			JLabel lPort = (JLabel)b.getComponent(1);
			if(lIp.getText().equals(id.getIP().getHostAddress()) && Integer.parseInt(lPort.getText()) == id.getPort()){
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
		//	this.mainController = new MainController(myUsername, um);
		}
	}
	
	private void clickDisconnect(){
		changeFrameDisconnection();
		mainController.Disconnect();
		mainController = null;
	}
	
	private void clickSendGroup(){
		mainController.openGroupChat(listGroup);
	}
	
	private void clickUser(JButton b){
		JLabel labelIP = (JLabel)b.getComponent(0);
		JLabel labelPort = (JLabel)b.getComponent(1);
		try {
			mainController.openChat(new UserId(InetAddress.getByName(labelIP.getText()), Integer.parseInt(labelPort.getText())));
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
		System.out.println(labelIP.getText());
		if(b.getBackground().equals(Color.decode("#99ff66"))){
			b.setBackground(Color.white);
		}
	}
	
	private void selectUser(ItemEvent e){
		JComponent cb = (JComponent) e.getSource();
		Container panel = cb.getParent();
		JButton b = (JButton) panel.getComponent(1);
		JLabel labelIP = (JLabel) b.getComponent(0);
		JLabel labelPort = (JLabel) b.getComponent(1);
		try{
			if (e.getStateChange() == ItemEvent.DESELECTED){
				if(listGroup.size() == 1){
					bSendGroup.setEnabled(false);
				}
				listGroup.remove(new UserId(InetAddress.getByName(labelIP.getText()), Integer.parseInt(labelPort.getText())));
				System.out.println("Suppression de l'id [" + labelIP.getText() + ", " + labelPort.getText() + "] dans le  groupe");
			}else{
				if (listGroup.size() == 0){
					bSendGroup.setEnabled(true);
				}
				listGroup.add(new UserId(InetAddress.getByName(labelIP.getText()), Integer.parseInt(labelPort.getText())));
				System.out.println("Ajout de l'id [" + labelIP.getText() + ", " + labelPort.getText() + "] au  groupe");
			}
		}catch(UnknownHostException ex){
			System.out.println(ex.getMessage());
		}
	}
	
	public void newMessage(UserId id){
		for (int i=0; i<pList.getComponentCount(); i++){
			JComponent c = (JComponent)pList.getComponent(i);
			JButton b = (JButton)c.getComponent(1);
			JLabel lIp = (JLabel) b.getComponent(0);
			JLabel lPort = (JLabel) b.getComponent(1);
			if(lIp.getText().equals(id.getIP().getHostAddress()) && lPort.getText().equals(Integer.toString(id.getPort())) && b.getBackground().equals(Color.white)){
				b.setBackground(Color.decode("#99ff66"));
			}
		}
	}
}
