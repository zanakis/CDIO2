
public class StoreEntry {
	private String name;
	private int number;
	
	public StoreEntry(int number, String name) {
		this.name = name;
		this.number = number;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumber() {
		return number;
	}
}
