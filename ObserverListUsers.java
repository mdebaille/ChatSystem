import java.net.InetAddress;

public interface ObserverListUsers {
	public void addUser(InfoUser info);
	public void removeUser(InetAddress ip);
	public void newMessage(InetAddress ip);
}
