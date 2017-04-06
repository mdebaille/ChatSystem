import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;


/* 
 * Lance IHM principale : Fait
 * Quand bouton connect cliqué :  inscription au groupe de multicast : fait
 * puis crée UsersController : fait
 * puis crée serveursocket qui écoute le réseau
 * 
 * création chat controller et canal ??????????????
 */

public class Chatsystem{
	
	private MainIHM mainIHM;

	private InetAddress address;
	private int portMulticast;
	private int portServSocket;
	private ServerSocket servSocket;
	
	private String pseudo;
	
	private InetAddress group;
	MulticastSocket multicastSocket;
	
	UsersController uc;
	
	boolean connected = false;
	
	public static void main(String[] args) {
		Chatsystem chatSystem = new Chatsystem();
	}
	
	public Chatsystem(){
		try{
			this.address = InetAddress.getByName("localhost");
			this.portServSocket = 17000;
			
			this.portMulticast = 49150;	// valeur à définir
			group = InetAddress.getByName("228.5.6.7"); // valeur à définir
		
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		
		mainIHM= new MainIHM(this);
	}
	
	public void sendMessageUser(){
		try{
		MessageUser mess = new MessageUser(pseudo, address, portServSocket, MessageUser.typeConnect.CONNECTED);
		String m = ""; //Serialisation de mess
		DatagramPacket packet = new DatagramPacket(m.getBytes(), m.length(),group, portMulticast);
		multicastSocket.send(packet);
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void startChatsystem(String pseudo){
		this.pseudo = pseudo;
		
		uc = new UsersController(this);
		mainIHM.setUsersController(uc);
		mainIHM.changeFrame();
		connected = true;
		//test();
		try{
			multicastSocket = new MulticastSocket(portMulticast);
			multicastSocket.joinGroup(group);
			servSocket = new ServerSocket(portServSocket);
			// faire le accept (dans un thread)
		}catch(IOException e){
			System.out.println(e.getMessage());
		}

	}
	
	public void Disconnect(){
		connected = false;
	}
	
	
	public void addChannel(InfoUser info){
		try{
			InetAddress address = info.getIP();
			Socket socket = new Socket(address, info.getPort());
			InputStream iStream = socket.getInputStream();
			OutputStream oStream = socket.getOutputStream();
			
			InputStreamReader isr = new InputStreamReader(iStream); 
			OutputStreamWriter osw = new OutputStreamWriter(oStream); 
			
			BufferedReader bReader = new BufferedReader(isr);
			BufferedWriter bWriter = new BufferedWriter(osw);
			
			ChatController chatController = new ChatController(new ListenSocket(bReader), bWriter);
			
		}catch(UnknownHostException e){
			System.out.println("Erreur de résolution d'adresse serveur");
		}catch(IOException e){
			System.out.println("Client: Erreur lors de la création du socket");
			System.out.println(e.getMessage());
		}
	}

	

	
	public void test (){
		String pseudo;
		InetAddress IP;
		String statut ="";
		final int port=10;
		final MessageUser.typeConnect type = MessageUser.typeConnect.CONNECTED;
		final ArrayList <MessageUser> list = new ArrayList<>();
		for (int i=0;i<10;i++){
			try{
			pseudo = Integer.toString(i);
			IP = InetAddress.getByName("1.1.1."+Integer.toString(i));
			list.add(new MessageUser(pseudo,IP,port,type));
			}catch(UnknownHostException e){
				System.out.println(e.getMessage());
			}
		}
		final Timer timer = new Timer();
		TimerTask task1 = new TimerTask() {
            public void run() {
            	if(connected){
	            	for (MessageUser m : list){
	                	uc.receivedMessageUser(m);
	            	}
            	}else{
            		timer.cancel();
            		timer.purge();
            	}
            }
        };
        TimerTask task2 = new TimerTask() {
            public void run() {
            	if(connected){
            		try{
                		String p = Integer.toString(10);
            			InetAddress i = InetAddress.getByName("1.1.1."+Integer.toString(10));
            			uc.receivedMessageUser(new MessageUser(p,i,port,type));
            			}catch(UnknownHostException e){
            				System.out.println(e.getMessage());
            			}
	            	}
            	}
        };
        timer.schedule(task1, 2000, 2000);
        timer.schedule(task2, 6000, 2000);
	}
	
	public MainIHM getMainIHM(){
		return mainIHM;
	}
}
