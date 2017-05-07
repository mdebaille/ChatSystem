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
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/*
 * Classe repr�sentant la vue de la liste des utilisateurs connect�s
 */

public class MainIHM extends JFrame implements ObserverListUsers {
	
	String myUsername;
	
	JPanel pInputUsername;
	JPanel pError;
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
	
	ArrayList<String> listGroup;
	
	
	public MainIHM(MainController mc){
		this.nbUsers = 0;
		this.mainController = mc;
		listGroup = new ArrayList<String>();
		initComponents();
	}
	
	private void initComponents(){
		
		this.setTitle("Chat System");
		
		// Zone de saisie du pseudo
		pInputUsername = new JPanel();
		pInputUsername.setLayout(new BoxLayout(pInputUsername, BoxLayout.X_AXIS));
		pInputUsername.setBorder(new EmptyBorder(20, 20, 10, 20));	
		JLabel lUsername = new JLabel("Username: ");
		lUsername.setBorder(new EmptyBorder(0,10,0,10));
		taUsername = new JTextArea(1,15);
		pInputUsername.add(lUsername);
		pInputUsername.add(taUsername);
		
		// Affichage d'un message d'erreur
		pError = new JPanel(new BorderLayout());
		pError.setBorder(new EmptyBorder(0, 30, 0, 30));
		
		// Bouton de connexion
		pConnect = new JPanel(new BorderLayout());
		pConnect.setBorder(new EmptyBorder(10, 60, 20, 60));
		JButton bConnect = new JButton("Connect");
		pConnect.add(bConnect, BorderLayout.CENTER);
		
		
		this.setLayout(new GridLayout(3, 1));
		this.add("1", pInputUsername);
		this.add("2", pError);
		this.add("3", pConnect);
		
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Listener sur le bouton de connexion
		bConnect.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		clickConnect();
	    	}
	    });
	}
	
// M�thode appel�e quand l'utilisateur a cliqu� sur le bouton de coonexion	
private void changeFrameConnection(){
		
		this.setVisible(false);

		// pseudo de l'utilisateur actuel
		GridBagConstraints cUsername = new GridBagConstraints();
		cUsername.gridx = 0;
		cUsername.gridy = 0;
		cUsername.fill = GridBagConstraints.HORIZONTAL;
		lMyUsername = new JLabel(myUsername, JLabel.CENTER);
		lMyUsername.setForeground(Color.MAGENTA);
		lMyUsername.setBorder(new EmptyBorder(20,20,20,20));

		// Liste des utilisateurs connect�s
		GridBagConstraints cList = new GridBagConstraints();
		cList.gridx = 0;
		cList.gridy = 1;
		cList.fill = GridBagConstraints.HORIZONTAL;
		pList = new JPanel();
		pList.setLayout(new GridLayout(nbUsers,1));
		scrollList = new JScrollPane(pList);
		scrollList.setPreferredSize(new Dimension(100,200));
		
		// Bouton pour envoyer � un groupe
		GridBagConstraints cGroup = new GridBagConstraints();
		cGroup.gridx = 0;
		cGroup.gridy = 2;
		cGroup.fill = GridBagConstraints.HORIZONTAL;
		pSendGroup = new JPanel(new BorderLayout());
		pSendGroup.setBorder(new EmptyBorder(20, 60, 10, 60));
		bSendGroup = new JButton("Send to group");
		bSendGroup.setEnabled(false);
		pSendGroup.add(bSendGroup, BorderLayout.CENTER);
		
		// Bouton de d�connexion
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
		this.remove(pError);
		
		this.setLayout(new GridBagLayout());;
		this.add(lMyUsername, cUsername);
		this.add(scrollList, cList);
		this.add(pSendGroup, cGroup);
		this.add(pDisconnect, cDisconnect);
		
		this.pack();
		this.setVisible(true);
		
		// Listener sur le bouton de d�connexion
		bDisconnect.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		clickDisconnect();
	    	}
	    });
		
		// Listener sur le bouton d'envoi � un groupe d'utilisateurs
		bSendGroup.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		clickSendGroup();
	    	}
	    });
	}

	// M�thode appel�e qaund l'utilsateur a cliqu� sur le bouton de d�connexion
	private void changeFrameDisconnection(){
		this.remove(lMyUsername);
		this.remove(scrollList);
		this.remove(pSendGroup);
		this.remove(pDisconnect);
		initComponents();
	}
	
	// M�thode appel�e quand un utilisateur est ajout� � la liste des utilsateurs connect�s
	public void addUser(InfoUser info){
		
		// Cr�ation d'un nouveau composant � ajouter � la liste
		JPanel pUser = new JPanel(new FlowLayout());
		// Checkbox permettant de s�lectionner un utilisateur pour l'ajouter � la liste des utilisateurs pour envoyer un message � un groupe
		JCheckBox cb = new JCheckBox();
		// Listener sur la checkbox
		cb.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
	    		selectUser(e);
	    	}
		});
		// Bouton avec le nom du nouvel utilisateur
		JButton bName = new JButton(info.getPseudo());
		bName.setBackground(Color.white);
		
		//Label cach� associ� au bouton pour conna�tre l'adresse IP de l'utilisateur correspondant au bouton :
		//Cette adresse ip sert � identifier de maniere unique l'utilisateur qu'on selectionne pour se connecter a ce user la et pas un autre
		//Sinon il y a risque de creer un chatcontroller associe a un user possedant le meme pseudo (si on se base uniquement sur le pseudo)
		JLabel hiddenIP = new JLabel(info.getIP().getHostAddress()); 
		hiddenIP.setVisible(false);
		bName.add(hiddenIP);
		
		// Listener sur le bouton correspondant au nouvel utilisateur
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
	
	// M�thode appel�e quand un utilisateur est enlev� de la liste des utilisateurs connect�s
	public void removeUser(InetAddress ip){
		nbUsers--;
		pList.setLayout(new GridLayout(nbUsers,1));
		// on cherche l'utilisateur dans la liste gr�ce � son adresse IP
		for (int i=0; i<pList.getComponentCount(); i++){
			JPanel p = (JPanel)pList.getComponent(i);
			JButton b = (JButton)p.getComponent(1);
			JLabel lIp = (JLabel)b.getComponent(0);
			if(lIp.getText().equals(ip.getHostAddress())){
				pList.remove(i);
			}
		}
		pList.revalidate();
		pList.repaint();
	}

	private void clickConnect(){
		myUsername = taUsername.getText();
		pError.removeAll();
		if(myUsername.contains("#") || myUsername.equals("")){
			JLabel lError = null;
			if(myUsername.contains("#") ){
				// on interdit la caract�re # car il est utilis� pour la s�rialisation des messages
				lError = new JLabel("Caract�re '#' interdit");
			} else{
				// on interdit les pseudos vides
				lError = new JLabel("Erreur : pseudo vide");
			}
			lError.setForeground(Color.red);
			// on affiche un message d'erreur si le pseudo saisi n'est pas valide
			pError.add(lError, BorderLayout.CENTER);
			pError.revalidate();
			pError.repaint();
		}
		else{
			changeFrameConnection();
			mainController.Connect(myUsername);
		}
	}
	
	private void clickDisconnect(){
		changeFrameDisconnection();
		mainController.Disconnect();
	}
	
	private void clickSendGroup(){
		mainController.openGroupChat(listGroup);
	}
	
	private void clickUser(JButton b){
		JLabel labelIP = (JLabel)b.getComponent(0);
		// une fen�tre pour communiquer avec l'utilisateur est ouverte
		mainController.openChat(labelIP.getText());
		// si le bouton �tait vert car un nouveau message �tait re�u, on remet sa couleur normale
		if(b.getBackground().equals(Color.decode("#99ff66"))){
			b.setBackground(Color.white);
		}
	}
	
	// M�thode appel�e quand on s�lectionne un utilisateur pour pouvoir l'ajouter � liste pour un envoi de message � un groupe
	private void selectUser(ItemEvent e){
		
		// on r�cup�re l'IP qui correspond � cette utilisateur
		JComponent cb = (JComponent) e.getSource();
		Container panel = cb.getParent();
		JButton b = (JButton) panel.getComponent(1);
		JLabel labelIP = (JLabel) b.getComponent(0);
		
		// si on d�selectionne, on enl�ve l'utilisateur de la liste du groupe
		// et si c'�tait le seul utilisateur s�lectionn� pour l'nvoi � un group, on d�sactive le bouton pour l'envoi � un groupe
		if (e.getStateChange() == ItemEvent.DESELECTED){
			if(listGroup.size() == 1){
				bSendGroup.setEnabled(false);
			}
			listGroup.remove(labelIP.getText());
			System.out.println("Suppression de l'ip " + labelIP.getText() + " dans le  groupe");
		// si on s�lectionne, on ajoute l'utilisateur � liste du groupe
		// et si c'est le premier utilisateur de la liste, on active le bouton d'envoi � un groupe
		}else{
			if (listGroup.size() == 0){
				bSendGroup.setEnabled(true);
			}
			listGroup.add(labelIP.getText());
			System.out.println("Ajout de l'ip" + labelIP.getText() + " au  groupe");
		}
	}
	
	// // m�thode appel�e quand on re�oit un nouveau message d'un utilisateur avec lequel nous n'avons de fen�tre ouverte pour communiquer
	public void newMessage(InetAddress ip){
		for (int i=0; i<pList.getComponentCount(); i++){
			JComponent c = (JComponent)pList.getComponent(i);
			JButton b = (JButton)c.getComponent(1);
			JLabel lIp = (JLabel) b.getComponent(0);
			// si un nouveau message n'a pas d�j� �t� notifi�, on change la couleur du bouton
			if(lIp.getText().equals(ip.getHostAddress()) && b.getBackground().equals(Color.white)){
				b.setBackground(Color.decode("#99ff66"));
			}
		}
	}
	
}
