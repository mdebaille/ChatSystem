import java.net.InetAddress;

public interface ObservableListUsers {
	public void addObserver(ObserverListUsers obs);
	public void removeObserver(ObserverListUsers obs);
	public void notifyNewUser(InfoUser info);
	public void notifyRemoveUser(InetAddress ip);
	public void notifyNewMessage(InetAddress ip);
}
