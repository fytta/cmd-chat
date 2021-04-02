import java.util.HashMap;
import java.util.Map;

public class Menu {
	
	private static Menu menu;
	private boolean active;
	
	public Message getHelp() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Actions available:\n");
		sb.append("!help: Show commands available\n");
		sb.append("!back: Go back to the general chat\n");
		sb.append("!list-users: List the users connected.\n");
		sb.append("!pm 'username': Send private message with this user. You can send it from general chat.\n");
		sb.append(". 'username' : Send a private message to the last user you chatted with. You can send it from general chat.\n");
		Message message = new Message("Server", sb.toString());
		
		return message;
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public Message getUsersList(Map<String, User> clients) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Users connected:\n");
		
		for (Map.Entry<String, User> entry: clients.entrySet()) {
			sb.append(entry.getKey() + ", ");
		}
		
		String result = sb.toString().substring(0, sb.length()-2);
		
		return new Message("Server", result);		
	}

}
