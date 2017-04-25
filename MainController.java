
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainController implements ObserverListUsers{

	private String pseudo;
	private UsersModel um;
	private HashMap<UserId, SingleChatController> listChatController;

	private NetworkManager networkManager;

	boolean connected = false;

	/*
	 * // pour testComm(), pas du tout important, a supprimer plus tard static
	 * String addressDest; static int portServerDest; static int myPortServer;
	 * 
	 * public static void main(String[] args) { if(args.length >= 2){
	 * portServerDest = Integer.parseInt(args[0]); myPortServer =
	 * Integer.parseInt(args[1]); } try { addressDest =
	 * InetAddress.getLocalHost().getHostName(); } catch (UnknownHostException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * MainController mainController = new MainController(); }
	 */

	public MainController(UsersModel um){
		this.um = um;
	}
	
	public void Connect(String pseudo){
		this.pseudo = pseudo;
		listChatController = new HashMap<UserId, SingleChatController>();
		connected = true;
		networkManager = new NetworkManager(pseudo, this);
	}

	public void Disconnect() {
		connected = false;
		networkManager.sendDisconnect();
		networkManager.notifyDisconnection();
	}

	public void addChatController(Socket socketDest) {
		InetAddress ipDest = socketDest.getInetAddress();
		UserId userId = new UserId(ipDest, socketDest.getPort());
		SingleChatController newChatController = new SingleChatController(this, um.getUser(userId), socketDest, pseudo);
		listChatController.put(userId, newChatController);
	}

	private void removeChatController(UserId id) {
		listChatController.remove(id);
	}

	public boolean existChatController(InetAddress ip) {
		return listChatController.containsKey(ip);
	}

	public void openChat(UserId id) {
		try {
			//InetAddress ip = InetAddress.getByName(ipAddress);

			InfoUser dest = um.getUser(id);
			System.out.println(dest.getPort());
			if (!existChatController(id.getIP())) {
				Socket socket = new Socket(id.getIP(), dest.getPort());
				this.addChatController(socket);
			}

			ChatController chatController = listChatController.get(id.getIP());
			ChatIHM cIHM = new ChatIHM(pseudo, dest.getPseudo(), chatController);
			chatController.linkModelToIHM(cIHM);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void openGroupChat(ArrayList<UserId> listUserId){
		ArrayList<Socket> listSocket = new ArrayList<Socket>();
		for(UserId id: listUserId){
			try {
				//InetAddress ipAddress = InetAddress.getByName(ip);
				InfoUser dest = um.getUser(id);
				if(existChatController(id.getIP())){
					listSocket.add(listChatController.get(id.getIP()).getSocketDest());
				}else{
					Socket socket = new Socket(id.getIP(), dest.getPort());
					listSocket.add(socket);
					this.addChatController(socket);
				}
			} catch(IOException e){
				System.out.println(e.getMessage());
			} 
		}
		GroupChatController groupChatController = new GroupChatController(this, listSocket, pseudo);
		ChatIHM cIHM = new ChatIHM(pseudo, "Group", groupChatController);
		groupChatController.linkModelToIHM(cIHM);
	}

	public void notifyNewMessage(UserId id) {
		um.notifyNewMessage(id);
	}
	
	public UsersModel getUsersModel() {
		return this.um;
	}

	/*
	 * public void testComm(){ try{ //mon ip: "192.168.0.27", autre machine:
	 * "192.168.0.25" InetAddress ipDest = InetAddress.getByName(addressDest);
	 * int portDest = portServerDest; um.addUser(new InfoUser("Test", ipDest,
	 * portDest)); }catch(IOException e){ System.out.println(e.getMessage()); }
	 * }
	 */

	public void testIHM() {
		String pseudo;
		InetAddress IP;
		final int port = 10;
		final MessageUser.typeConnect type = MessageUser.typeConnect.CONNECTED;
		final ArrayList<MessageUser> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			try {
				pseudo = Integer.toString(i);
				IP = InetAddress.getByName("1.1.1." + Integer.toString(i));
				list.add(new MessageUser(pseudo, IP, port, type));
			} catch (UnknownHostException e) {
				System.out.println(e.getMessage());
			}
		}
		final Timer timer = new Timer();
		TimerTask task1 = new TimerTask() {
			public void run() {
				if (connected) {
					for (MessageUser m : list) {
						um.receivedMessageUser(m);
					}
				} else {
					timer.cancel();
					timer.purge();
				}
			}
		};
		TimerTask task2 = new TimerTask() {
			public void run() {
				if (connected) {
					try {
						String p = Integer.toString(10);
						InetAddress i = InetAddress.getByName("1.1.1." + Integer.toString(10));
						um.receivedMessageUser(new MessageUser(p, i, port, type));
					} catch (UnknownHostException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		};
		timer.schedule(task1, 2000, 2000);
		timer.schedule(task2, 6000, 2000);
	}
	
	public void addUser(InfoUser info){}
	public void removeUser(UserId id){
		removeChatController(id);
	}
	public void newMessage(UserId id){}

}
