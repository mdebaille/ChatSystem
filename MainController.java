import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/*
 * Classe qui rprésente le contrôleur dans le MVC avec la fenêtre principale et la liste des utilsateurs connectés
 * Elle gère aussi la liste des ChatControlleur qui sont créés quand un autre utilisateur nous a envoyé un message ou quand on envoie un message et que le contôleur avec cet utilisateur n'existe pas encore
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
	
	// Méthode appelée quand on clique sur le bouton de connexion
	public void Connect(String pseudo){
		this.pseudo = pseudo;
		// on crée la liste de ChatController
		listChatController = new HashMap<InetAddress, SingleChatController>();
		connected = true;
		// on crée le networkManager pour gérer tout ce qui concerne le réseau
		networkManager = new NetworkManager(pseudo, this);
	}

	// Méthode appelée quand on clique sur le bouton de déconnexion
	public void Disconnect() {
		connected = false;
		// on envoie un message pour notifier les autres utilisateurs que l'on se déconnecte
		networkManager.sendDisconnect();
		networkManager.notifyDisconnection();
		// on notifie les ChatControllers que l'on se déconnecte
		for(Entry<InetAddress, SingleChatController> entry: listChatController.entrySet()){
			entry.getValue().notifyDisconnection();
			entry.getValue().closeChat();
		}
	}

	// création d'un nouveau ChatController à partir du socket
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

	// méthode appelée quand l'utilisateur a cliqué sur un utilisateur dans la fenêtre principale
	public void openChat(String ipAddress) {
		try {
			InetAddress ip = InetAddress.getByName(ipAddress);
			InfoUser dest = um.getUser(ip);
			// si le chatController associé à l'utilisateur distant n'existe pas, on le crée
			if (!existChatController(ip)) {
				Socket socket = new Socket(ip, dest.getPort());
				this.addChatController(socket);
			}
			ChatController chatController = listChatController.get(ip);
			// on crée une fenêtre de chat pour communiquer avec l'utilisateur distant
			ChatIHM cIHM = new ChatIHM(pseudo, dest.getPseudo(), chatController);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// méthode appelée quand l'utilisateur a cliqué sur le bouton pour envoyer des messages à un groupe d'utilisateur
	public void openGroupChat(ArrayList<String> listIp){
		ArrayList<Socket> listSocket = new ArrayList<Socket>();
		// on construit la liste des sockets correspondants à chacun des utilisateurs sélectionnés
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
		// on crée le chatController avec la liste des sockets des utilisateurs
		GroupChatController groupChatController = new GroupChatController(this, listSocket, pseudo);
		// on crée la fenêtre pour communiquer avec le groupe d'utilisateurs
		ChatIHM cIHM = new ChatIHM(pseudo, "Group", groupChatController);
	}

	// méthode appelée quand on reçoit un nouveau message d'un utilisateur avec lequel nous n'avons de fenêtre ouverte pour communiquer
	public void notifyNewMessage(InetAddress ip) {
		um.notifyNewMessage(ip);
	}
	
	public UsersModel getUsersModel() {
		return this.um;
	}
	
	public void addUser(InfoUser info){}
	
	// quand un utilisateur est enlevé de la liste des utilisateurs connectés, on enlève le ChatController correspondant de la liste
	public void removeUser(InetAddress ip){
		removeChatController(ip);
	}
	
	public void newMessage(InetAddress ip){}

}
