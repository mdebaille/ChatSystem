import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


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
	private MulticastSocket multicastSocket;
	
	private UsersModel um;
	private HashMap<InetAddress, ChatController> listChatController;
	
	boolean connected = false;
	
	// pour testComm(), pas du tout important, a supprimer plus tard
	static String addressDest;
	static int portServerDest;
	
	public static void main(String[] args) {
		Chatsystem chatSystem = new Chatsystem();
		
		// pour tetComm()
		if(args.length >= 2){
			addressDest = args[0];
			portServerDest = Integer.parseInt(args[1]);
		}
	}
	
	public Chatsystem(){
		try{
			this.address = InetAddress.getByName("localhost");
			this.portServSocket = 17001;
			
			this.portMulticast = 49150;	// valeur a definir
			group = InetAddress.getByName("228.5.6.7"); // valeur adefinir
		
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
		
		um = new UsersModel(this);
		listChatController = new HashMap<InetAddress, ChatController>();
		
		mainIHM.changeFrame();
		connected = true;
		//testIHM();
		
		try{
			multicastSocket = new MulticastSocket(portMulticast);
			multicastSocket.joinGroup(group);
			servSocket = new ServerSocket(portServSocket);
			
			// Boucle infinie pour accepter les connections d'autres utilisateurs quand ils veulent communiquer avec nous
			AcceptConnection acceptLoop = new AcceptConnection(servSocket, this);
			acceptLoop.start();
			
			// Boucle infinie qui gere la reception des MessageUser emis en multicast et les passe ࡕsersModel pour que la liste des users soit mise ࡪour
			MulticastListener multicastListener = new MulticastListener(new DatagramSocket(this.portMulticast), um);
			multicastListener.start();
			
		}catch(IOException e){
			System.out.println("Chatsystem: " + e.getMessage());
		}
		
		testComm();
	}
	
	public void Disconnect(){
		connected = false;
	}
	
	public ChatController addChannel(Socket socketDest){
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

			return newChatController;
		}catch(UnknownHostException e){
			System.out.println("Erreur de résolution d'adresse serveur");
		}catch(IOException e){
			System.out.println("Client: Erreur lors de la création du socket");
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public void removeChannel(InfoUser infoDest){
		listChatController.remove(infoDest.getIP());
	}

	public boolean existChatController(InetAddress ip){
		return listChatController.containsKey(ip);
	}
	
	public void openChat(String ipAddress){
		try{
			InetAddress ip = InetAddress.getByName(ipAddress);
			InfoUser dest = um.getUser(ip);
			
			if(!existChatController(ip)){
				Socket socket = new Socket(ip, dest.getPort());
				this.addChannel(socket);
			}
			
			ChatIHM cIHM = new ChatIHM(pseudo, dest.getPseudo(), listChatController.get(ip));
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void testComm(){
		try{
			//mon ip: "192.168.0.27", autre machine: "192.168.0.25"
			InetAddress ipDest = InetAddress.getByName(addressDest);
			int portDest = portServerDest;
			um.addUser(new InfoUser("Test", ipDest, portDest));
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void testIHM (){
		String pseudo;
		InetAddress IP;
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
	                	um.receivedMessageUser(m);
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
            			um.receivedMessageUser(new MessageUser(p,i,port,type));
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
