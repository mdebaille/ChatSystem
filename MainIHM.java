import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


/*
 * notification qd message reçu d'un certain user
 */


public class MainIHM extends JFrame {
	
	JPanel pInputUsername;
	JPanel pConnect;
	JPanel panel;
	JScrollPane paneList;
	
	JTextArea taUsername;
	
	String myUsername;
	JLabel lUsername;
	JLabel lMyUsername;
	
	Chatsystem chatsystem;
	
	public MainIHM(Chatsystem chatsystem){
		this.chatsystem = chatsystem;
		initComponents();
	}
	
	private void initComponents(){
		
		this.setTitle("Chat System");
		
		pInputUsername = new JPanel();
		pInputUsername.setLayout(new BoxLayout(pInputUsername, BoxLayout.X_AXIS));
		pInputUsername.setBorder(new EmptyBorder(20, 20, 20, 20));	
		lUsername = new JLabel("Username: ");
		lUsername.setBorder(new EmptyBorder(0,10,0,10));
		taUsername = new JTextArea(1,15);
		pInputUsername.add(lUsername);
		pInputUsername.add(taUsername);
		
		pConnect = new JPanel(new BorderLayout());
		pConnect.setBorder(new EmptyBorder(20, 60, 20, 60));
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
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		paneList = new JScrollPane(panel);
		lMyUsername = new JLabel(myUsername);
		JButton bDisconnect = new JButton("Disconnect");
		
		this.remove(pInputUsername);
		this.remove(pConnect);
		
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
