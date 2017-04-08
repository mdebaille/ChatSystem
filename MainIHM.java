import java.awt.GridLayout; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/*
 * Gestion de la Jlist en fonction du modèle dans UsersController
 * Ouverture de fenetre qd on clique sur un utilisateur
 * notification qd message reçu d'un certain user
 */





public class MainIHM extends JFrame implements ListSelectionListener, ListDataListener{

	private JButton bConnect;
	private JTextArea taUsername;
	JPanel panel;
	JScrollPane paneList;
	private JButton bDisconnect;
	String myUsername;
	JLabel lUsername;
	JLabel lMyUsername;
	
	UsersController usersController;
	JList<String> listUsers;
	
	Chatsystem chatsystem;
	
	public MainIHM(Chatsystem chatsystem){
		this.chatsystem = chatsystem;
		initComponents();
	}
	
	private void initComponents(){
		
		lUsername = new JLabel("Username: ");
		bConnect = new JButton("Connect");
		taUsername = new JTextArea(1,15);
		
		this.setLayout(new GridLayout(1, 3));
		this.add("1", lUsername);
		this.add("2", taUsername);
		this.add("3", bConnect);
		
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		bConnect.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		bConnectActionPerformed(e);
	    	}
	    });
	}
	
	
	public void setUsersController(UsersController uc){
		this.usersController = uc;
		listUsers = new JList <String>(usersController.getList());
		listUsers.addListSelectionListener(this);
	}
	
public void changeFrame(){
		
		this.setVisible(false);
		paneList = new JScrollPane(listUsers);
		lMyUsername = new JLabel(myUsername);
		bDisconnect = new JButton("Disconnect");
		
		this.remove(lUsername);
		this.remove(taUsername);
		this.remove(bConnect);
		
		this.setLayout(new GridLayout(3, 1));
		this.add(lUsername);
		this.add(paneList);
		this.add(bDisconnect);
		
		this.pack();
		this.setVisible(true);
		
		bDisconnect.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e){
	    		bDisconnectActionPerformed(e);
	    	}
	    });
	}
	

	private void bConnectActionPerformed(ActionEvent e){
		myUsername = taUsername.getText();
		chatsystem.startChatsystem(myUsername);
	}
	
	private void bDisconnectActionPerformed(ActionEvent e){
		chatsystem.Disconnect();
		//chatsystem.testComm();
	}
	
	public void valueChanged(ListSelectionEvent evt) { 
		 //etiquette.setText((String)liste.getSelectedValue());
	}
	
	
	public void contentsChanged(ListDataEvent listDataEvent) {
		System.out.println("modification");
	}
	public void intervalAdded(ListDataEvent listDataEvent) {
		System.out.println("ajout");
	}
	public void intervalRemoved(ListDataEvent listDataEvent) {
		System.out.println("suppression");
	}
	

}
