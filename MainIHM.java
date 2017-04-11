import java.awt.GridLayout; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


/*
 * Gestion de la Jlist en fonction du modèle dans UsersController
 * Ouverture de fenetre qd on clique sur un utilisateur
 * notification qd message reçu d'un certain user
 */





public class MainIHM extends JFrame {

	private JButton bConnect;
	private JTextArea taUsername;
	JPanel panel;
	JScrollPane paneList;
	private JButton bDisconnect;
	String myUsername;
	JLabel lUsername;
	JLabel lMyUsername;
	
	Chatsystem chatsystem;
	
	//private ArrayList<String> listUsers;
	 DefaultListModel<String> listUsers;
	 JList<String> jlist;
	
	public MainIHM(Chatsystem chatsystem){
		this.chatsystem = chatsystem;
		//listUsers = new ArrayList<>();
		listUsers = new DefaultListModel<String>();
		jlist = new JList<String>(listUsers);
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
	
	
public void changeFrame(){
		
		this.setVisible(false);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		paneList = new JScrollPane(panel);
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
	
	public void addUser(String pseudo){
		JButton name = new JButton(pseudo);
		name.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
	    		clickUser(e);
	    	}
		});
		panel.add(name);
		panel.revalidate();
		panel.repaint();
	}
	
	public void removeUser(String pseudo){
		for (int i=0; i<panel.getComponentCount(); i++){
			JButton b = (JButton)panel.getComponent(i);
			if(b.getText().equals(pseudo)){
				panel.remove(i);
			}
		}
		panel.revalidate();
		panel.repaint();
	}

	private void bConnectActionPerformed(ActionEvent e){
		myUsername = taUsername.getText();
		chatsystem.startChatsystem(myUsername);
	}
	
	private void bDisconnectActionPerformed(ActionEvent e){
		chatsystem.Disconnect();
		//chatsystem.testComm();
	}
	
	private void clickUser(ActionEvent e){
		JButton b = (JButton) e.getSource();
		chatsystem.openChat(b.getText());
	}
}
