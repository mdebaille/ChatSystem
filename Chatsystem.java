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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;


/* 
 * Lance IHM principale : Fait
 * Quand bouton connect cliqu√© :  inscription au groupe de multicast : fait
 * puis cr√©e UsersController : fait
 * puis cr√©e serveursocket qui √©coute le r√©seau
 * 
 * cr√©ation chat controller et canal ??????????????
 */

public class Chatsystem{
	
	private MainIHM mainIHM;

	private InetAddress address;
	private int portMulticast;
	private int portServSocket;
	private ServerSocket servSocket;
	
	private String pseudo;
	
	private InetAddress group;
	private MulticastSocket multicastSocket;
	
	private UsersController uc;
	private HashMap<InetAddress, ChatController> listChatController;
	
	boolean connected = false;
	
	static String role = "e"; // pour testComm(), pas du tout important, a supprimer plus tard
	
	public static void main(String[] args) {
		Chatsystem chatSystem = new Chatsystem();
		
		// pour tetComm()
		if(args.length > 0){
			role = args[0];
		}
	}
	
	public Chatsystem(){
		try{
			this.address = InetAddress.getByName("localhost");
			this.portServSocket = 17000;
			
			this.portMulticast = 49150;	// valeur a definir
			group = InetAddress.getByName("228.5.6.7"); // valeur a†definir
		
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		
		mainIHM = new MainIHM(this);
	}
	
	public void sendMessageUser(){
		try{
			MessageUser mess = new MessageUser(pseudo, address, portServSocket, MessageUser.typeConnect.CONNECTED);
			String m = ""; //Serialisation de mess
			DatagramPacket packet = new DatagramPacket(m.getBytes(), m.length(), group, portMulticast);
			multicastSocket.send(packet);
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void startChatsystem(String pseudo){
		this.pseudo = pseudo;
		
		uc = new UsersController(this);
		listChatController = new HashMap<InetAddress, ChatController>();
		
		mainIHM.setUsersController(uc);
		mainIHM.changeFrame();
		connected = true;
		//test();
		try{
			multicastSocket = new MulticastSocket(portMulticast);
			multicastSocket.joinGroup(group);
			servSocket = new ServerSocket(portServSocket);
			
			// Boucle infinie pour accepter les connections d'autres utilisateurs quand ils veulent communiquer avec nous
			AcceptConnection acceptLoop = new AcceptConnection(servSocket, this);
			acceptLoop.start();
			
			// Boucle infinie qui gere la reception des MessageUser emis en multicast et les passe ‡ UsersController pour que la liste des users soit mise ‡ jour
			MulticastListener multicastListener = new MulticastListener(new DatagramSocket(this.portMulticast), uc);
			multicastListener.start();
			
		}catch(IOException e){
			System.out.println("Chatsystem: " + e.getMessage());
		}

	}
	
	public void Disconnect(){
		connected = false;
	}
	
	public void addChannel(Socket socketDest){
		try{
			InetAddress ipDest= socketDest.getInetAddress();
			
			InputStream iStream = socketDest.getInputStream();
			OutputStream oStream = socketDest.getOutputStream();
			
			InputStreamReader isr = new InputStreamReader(iStream); 
			OutputStreamWriter osw = new OutputStreamWriter(oStream); 
			
			BufferedReader bReader = new BufferedReader(isr);
			BufferedWriter bWriter = new BufferedWriter(osw);
			
			ChatController newChatController = new ChatController(bReader, bWriter);
			listChatController.put(ipDest, newChatController);

		}catch(UnknownHostException e){
			System.out.println("Erreur de r√©solution d'adresse serveur");
		}catch(IOException e){
			System.out.println("Client: Erreur lors de la cr√©ation du socket");
			System.out.println(e.getMessage());
		}
	}
	
	public void removeChannel(InfoUser infoDest){
		listChatController.remove(infoDest.getIP());
	}

	public boolean existChatController(InetAddress ip){
		return listChatController.containsKey(ip);
	}
	

	// Juste pour tester un peu le fonctionnement de AcceptConnection et de ChatController sur 2 machines differentes sans avoir ChatIHM.
	// Pour tester, appeler cette methode au moment du clic sur le bouton disconnect par exemple, puis lancer le recepteur et ensuite l'emetteur
	public void testComm(){
		if(role.equals("e")){
			System.out.println("Emetteur:");
			try{
				InetAddress ipDest = InetAddress.getByName("192.168.0.25");
				int portDest = 17001;
				
				System.out.println("---Demande de connexion---");
				Socket socket = new Socket(ipDest, portDest);
				
				System.out.println("---Creation ChatController---");
				addChannel(socket);
				
				System.out.println("---Envoi de messages---");
				ChatController chatController = listChatController.get(ipDest);
				for(int i = 0; i<5; i++){
					System.out.println("Envoi: Hello" + i);
					chatController.sendMessage("Hello" + i + "\n");
					Thread.sleep(2000);
				}
				
				System.out.println("---Reception de messages---");
				String lastline;
				for(int i = 0; i<5; i++){	
					lastline = chatController.getLastLine(); // getLastLine() bloque tant qu'il n'y a pas de nouveau message ‡ lire
					System.out.println("ReÁu: " + lastline); // affichage du message dans l'IHM
				}
			}catch(IOException e){
				System.out.println(e.getMessage());
			}catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		}else{
			System.out.println("Recepteur:");
			try{
				InetAddress ipEmetteur = InetAddress.getByName("192.168.0.27");
			
				System.out.println("---Reception de connexion---");
				while(!existChatController(ipEmetteur)){}
				ChatController chatController = listChatController.get(ipEmetteur);
				
				System.out.println("---Reception de messages---");
				String lastline;
				for(int i = 0; i<5; i++){	
					lastline = chatController.getLastLine(); // getLastLine() bloque tant qu'il n'y a pas de nouveau message ‡ lire
					System.out.println("ReÁu: " + lastline); // affichage du message dans l'IHM
					System.out.println("Envoi: Salut" + i);
					chatController.sendMessage("Salut" + i + "\n"); // envoi d'une rÈponse
				}
			}catch(IOException e){
				System.out.println(e.getMessage());
			}
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
