import java.net.InetAddress;

public interface ObserverListUsers {
	public void addUser(InfoUser info);
	public void removeUser(UserId id);
	public void newMessage(UserId id);
}
