
public interface ObservableMessages {
	public void addObserver(ObserverMessages obs);
	public void removeObserver(ObserverMessages obs);
	public void removeAllObservers();
	public void notifyMessage(byte[] message);
	
}
