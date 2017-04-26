import java.net.InetAddress;

public class UserId {
	
	private InetAddress ip;
	private int port;
	
	public UserId(InetAddress ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public InetAddress getIP(){
		return ip;
	}

	public int getPort() {
		return port;
	}
	
}
