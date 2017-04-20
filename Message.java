import java.io.Serializable;


public class Message implements Serializable {
	
	//private boolean typeFile;
	private String data;

	public Message(String data){
		this.data = data;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
