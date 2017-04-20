import java.io.IOException;

public class MessageUserBroadcaster extends Thread{
	
	private static int sendDelay;
	private NetworkManager networkManager;
	
	public MessageUserBroadcaster(NetworkManager nm, int delay){
		networkManager = nm;
		sendDelay = delay;
	}
	
	public void run(){
		while(true){
			networkManager.sendMessageUser();
			try {
				Thread.sleep(sendDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
