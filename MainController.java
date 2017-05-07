
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
	private HashMap<InetAddress, SingleChatController> listChatController;

	private NetworkManager networkManager;

	boolean connected = false;

	public MainController(UsersModel um){
		this.um = um;
	}
	
	public void Connect(String pseudo){
		this.pseudo = pseudo;
		listChatController = new HashMap<InetAddress, SingleChatController>();
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
		SingleChatController newChatController = new SingleChatController(this, um.getUser(ipDest), socketDest, pseudo);
		listChatController.put(ipDest, newChatController);
	}

	private void removeChatController(InetAddress ip) {
		listChatController.remove(ip);
	}

	public boolean existChatController(InetAddress ip) {
		return listChatController.containsKey(ip);
	}

	public void openChat(String ipAddress) {
		try {
			InetAddress ip = InetAddress.getByName(ipAddress);

			InfoUser dest = um.getUser(ip);
			System.out.println(dest.getPort());
			if (!existChatController(ip)) {
				Socket socket = new Socket(ip, dest.getPort());
				this.addChatController(socket);
			}

			ChatController chatController = listChatController.get(ip);
			ChatIHM cIHM = new ChatIHM(pseudo, dest.getPseudo(), chatController);
			//chatController.addObservertoModel(cIHM);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void openGroupChat(ArrayList<String> listIp){
		ArrayList<Socket> listSocket = new ArrayList<Socket>();
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
		GroupChatController groupChatController = new GroupChatController(this, listSocket, pseudo);
		ChatIHM cIHM = new ChatIHM(pseudo, "Group", groupChatController);
		groupChatController.addObservertoModel(cIHM);
	}

	public void notifyNewMessage(InetAddress ip) {
		um.notifyNewMessage(ip);
	}
	
	public UsersModel getUsersModel() {
		return this.um;
	}
	/*
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
	}*/
	
	public void addUser(InfoUser info){}
	public void removeUser(InetAddress ip){
		removeChatController(ip);
	}
	public void newMessage(InetAddress ip){}

}
