import java.net.Socket;
import java.util.Map;

public class Menu {
	
	private static Menu menu;
	private boolean active = false;
	
	
	public Message getHelp() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Actions available:\n");
		sb.append("!help: Show commands available\n");
		sb.append("!back: Go back to the general chat\n");
		sb.append("!list-users: List the users connected.\n");
		sb.append("!whisper 'username': Open a private chat with this user. \n");
		Message message = new Message("Server", sb.toString());
		
		return message;
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public Message getUsersList(Map<Socket, User> clients) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Users connected:\n");
		for (Map.Entry<Socket, User> entry : clients.entrySet()) {
			sb.append(entry.getValue().getName() + ", ");
		}
		String result = sb.toString().substring(0, sb.length()-2);
		
		return new Message("Server", result);
		
	}

}
