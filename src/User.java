import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class User{

	private Socket client;
	
	private String username;
	
	private User currentChat;
	
	private List<Message> privateMessages = new ArrayList<Message>();
	
	public User (Socket client, String username) {
		this.client = client;
		this.username = username;
	}


	public Socket getClient() {
		return client;
	}

	public String getUsername() {
		return username;
	}


	public User getCurrentChat() {
		return currentChat;
	}


	public void setCurrentChat(User currentChat) {
		this.currentChat = currentChat;
	}
	
	public void addPrivateMessage(Message message) {
		privateMessages.add(message);
	}
	
}
