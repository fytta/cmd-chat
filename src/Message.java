
import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = 2327018724015381754L;
	
	private String name;
	private String text;
	private boolean privateMessage;
	
	public Message(String name, String text) {
		this.name = name;
		this.text = text;
	}
	
	public Message(String name, String text, boolean privateMessage) {
		this.name = name;
		this.text = text;
		this.privateMessage = privateMessage;
	}
	
	public String getText() {
		return text;
	}
	public String getName() {
		return name;
	}

	public boolean isPrivateMessage() {
		return privateMessage;
	}

	public void setPrivateMessage(boolean privateMessage) {
		this.privateMessage = privateMessage;
	}	
	
}
