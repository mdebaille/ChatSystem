import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/*
 * Classe qui rpr�sente le contr�leur dans le MVC avec la fen�tre principale et la liste des utilsateurs connect�s
 * Elle g�re aussi la liste des ChatControlleur qui sont cr��s quand un autre utilisateur nous a envoy� un message ou quand on envoie un message et que le cont�leur avec cet utilisateur n'existe pas encore
 */

public class MainController implements ObserverListUsers{

	private String pseudo;
	private UsersModel um;
	private HashMap<InetAddress, SingleChatController> listChatController;

	private NetworkManager networkManager;

	boolean connected = false;

	public MainController(UsersModel um){
		this.um = um;
	}
	
	// M�thode appel�e quand on clique sur le bouton de connexion
	public void Connect(String pseudo){
		this.pseudo = pseudo;
		// on cr�e la liste de ChatController
		listChatController = new HashMap<InetAddress, SingleChatController>();
		connected = true;
		// on cr�e le networkManager pour g�rer tout ce qui concerne le r�seau
		networkManager = new NetworkManager(pseudo, this);
	}

	// M�thode appel�e quand on clique sur le bouton de d�connexion
	public void Disconnect() {
		connected = false;
		// on envoie un message pour notifier les autres utilisateurs que l'on se d�connecte
		networkManager.sendDisconnect();
		networkManager.notifyDisconnection();
		// on notifie les ChatControllers que l'on se d�connecte
		for(Entry<InetAddress, SingleChatController> entry: listChatController.entrySet()){
			entry.getValue().notifyDisconnection();
			entry.getValue().closeChat();
		}
	}

	// cr�ation d'un nouveau ChatController � partir du socket
	public void addChatController(Socket socketDest) {
		InetAddress ipDest = socketDest.getInetAddress();
		SingleChatController newChatController = new SingleChatController(this, um.getUser(ipDest), socketDest, pseudo);
		listChatController.put(ipDest, newChatController);
	}

	private void removeChatController(InetAddress ip) {
		listChatController.remove(ip);
	}

	public boolean existChatController(InetAddress ip) {
		return listChatController.containsKey(ip);
	}

	// m�thode appel�e quand l'utilisateur a cliqu� sur un utilisateur dans la fen�tre principale
	public void openChat(String ipAddress) {
		try {
			InetAddress ip = InetAddress.getByName(ipAddress);
			InfoUser dest = um.getUser(ip);
			// si le chatController associ� � l'utilisateur distant n'existe pas, on le cr�e
			if (!existChatController(ip)) {
				Socket socket = new Socket(ip, dest.getPort());
				this.addChatController(socket);
			}
			ChatController chatController = listChatController.get(ip);
			// on cr�e une fen�tre de chat pour communiquer avec l'utilisateur distant
			ChatIHM cIHM = new ChatIHM(pseudo, dest.getPseudo(), chatController);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// m�thode appel�e quand l'utilisateur a cliqu� sur le bouton pour envoyer des messages � un groupe d'utilisateur
	public void openGroupChat(ArrayList<String> listIp){
		ArrayList<Socket> listSocket = new ArrayList<Socket>();
		// on construit la liste des sockets correspondants � chacun des utilisateurs s�lectionn�s
		for(String ip: listIp){
			try {
				InetAddress ipAddress = InetAddress.getByName(ip);
				InfoUser dest = um.getUser(ipAddress);
				if(existChatController(ipAddress)){
					listSocket.add(listChatController.get(ipAddress).getSocketDest());
				}else{
					Socket socket = new Socket(ip, dest.getPort());
					listSocket.add(socket);
					this.addChatController(socket);
				}
			} catch(IOException e){
				System.out.println(e.getMessage());
			} 
		}
		// on cr�e le chatController avec la liste des sockets des utilisateurs
		GroupChatController groupChatController = new GroupChatController(this, listSocket, pseudo);
		// on cr�e la fen�tre pour communiquer avec le groupe d'utilisateurs
		ChatIHM cIHM = new ChatIHM(pseudo, "Group", groupChatController);
	}

	// m�thode appel�e quand on re�oit un nouveau message d'un utilisateur avec lequel nous n'avons de fen�tre ouverte pour communiquer
	public void notifyNewMessage(InetAddress ip) {
		um.notifyNewMessage(ip);
	}
	
	public UsersModel getUsersModel() {
		return this.um;
	}
	
	public void addUser(InfoUser info){}
	
	// quand un utilisateur est enlev� de la liste des utilisateurs connect�s, on enl�ve le ChatController correspondant de la liste
	public void removeUser(InetAddress ip){
		removeChatController(ip);
	}
	
	public void newMessage(InetAddress ip){}

}
