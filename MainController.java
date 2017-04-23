
import java.io.IOException;
import java.net.InetAddress;
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

public class MainController{
	
	private MainIHM mainIHM;
	private String pseudo;
	private UsersModel um;
	private HashMap<InetAddress, ChatController> listChatController;
	
	private NetworkManager networkManager;
	
	boolean connected = false;
	
	// pour testComm(), pas du tout important, a supprimer plus tard
	static String addressDest;
	static int portServerDest;
	static int myPortServer;
	
	public static void main(String[] args) {
		/*if(args.length >= 2){
			portServerDest = Integer.parseInt(args[0]);
			myPortServer = Integer.parseInt(args[1]);
		}
		try {
			addressDest = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		MainController mainController = new MainController();
	}
	
	public MainController(){
		mainIHM = new MainIHM(this);
	}
	
	
	public void startChatsystem(String pseudo){
		this.pseudo = pseudo;
		um = new UsersModel(this);
		listChatController = new HashMap<InetAddress, ChatController>();
		connected = true;
		networkManager = new NetworkManager(pseudo, this);
		testComm();
	}
	
	public void Disconnect(){
		connected = false;
	}
	
	public void addChatController(Socket socketDest){
		InetAddress ipDest= socketDest.getInetAddress();
		ChatController newChatController = new ChatController(this, um.getUser(ipDest), socketDest, pseudo);
		listChatController.put(ipDest, newChatController);

	}
	
	public void removeChatController(InetAddress ip){
		listChatController.remove(ip);
	}

	public boolean existChatController(InetAddress ip){
		return listChatController.containsKey(ip);
	}
	
	public void openChat(String ipAddress){
		try{
			InetAddress ip = InetAddress.getByName(ipAddress);
	
			InfoUser dest = um.getUser(ip);
			System.out.println(dest.getPort());
			if(!existChatController(ip)){
				Socket socket = new Socket(ip, dest.getPort());
				this.addChatController(socket);
			}
			
			ChatController chatController = listChatController.get(ip);
			ChatIHM cIHM = new ChatIHM(pseudo, dest.getPseudo(), chatController);
			chatController.linkModelToIHM(cIHM);
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void notifyNewMessage(InetAddress ip){
		mainIHM.notifyNewMessage(ip);
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
	
	public UsersModel getUsersModel(){
		return this.um;
	}
	
}