import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;


public class Message implements Serializable {
	
	private boolean typeFile;	// true si le message contient un fichier, false sinon
	private int size;			// taille de la data (du fichier ou du texte contenu)
	private byte[] data;		// data du fichier ou texte

	public Message(boolean isFile, int dataSize, byte[] data){
		this.typeFile = isFile;
		this.size = dataSize;
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public boolean isTypeFile(){
		return typeFile;
	}
	
	public int getSize(){
		return size;
	}

	public static byte[] serializeMessage(Message message) throws IOException{
		byte isFile = (byte)(message.typeFile?1:0);	 // conversion boolean => byte
		byte[] length = ByteBuffer.allocate(4).putInt(message.size).array(); // conversion int => byte[]
	
		// concatenation de tous les champs dans une seule chaine d'octets
		byte[] result;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		outputStream.write( isFile );
		outputStream.write( length );
		outputStream.write( message.data );
		result = outputStream.toByteArray( );
		outputStream.flush();
		return result;
	}
}
