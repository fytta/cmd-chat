
import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = 2327018724015381754L;
	
	private String name;
	private String text;
	
	public Message(String name, String text) {
		this.name = name;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	public String getName() {
		return name;
	}	
}
