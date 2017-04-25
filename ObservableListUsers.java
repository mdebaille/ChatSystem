import java.net.InetAddress;

public interface ObservableListUsers {
	public void addObserver(ObserverListUsers obs);
	public void removeObserver(ObserverListUsers obs);
	public void notifyNewUser(InfoUser info);
	public void notifyRemoveUser(UserId id);
	public void notifyNewMessage(UserId id);
}
