import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	private static final int DEFAULT_PORT = 8080;
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

	private ServerSocket server;

	private Map<String, User> clients = new HashMap<String, User>();

	private List<Message> messages = new ArrayList<Message>();
	
	public static void main(String[] args) {
		
		try {
			Server server = new Server();
			int port = (args.length > 1) ? Integer.parseInt(args[0]) : DEFAULT_PORT;
			server.execute(port);
		}
		catch (NumberFormatException e) {
			System.out.println("Invalid port. Must be an integer");
		}
	}

	private void execute(int port) {

		try {
			System.out.println("Server listening on: " + port);
			server = new ServerSocket(port);

			Socket client;
			while ((client = server.accept()) != null) {
				new Thread(clientHandler(client)).start();
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private Runnable clientHandler(Socket client) {
		return new Runnable() {
			
			@Override
			public void run () {
			
				try {
					String username = (String) receive(client);
					//TODO: Check is name is valid
					User user = new User(client, username);
					
					clients.put(username, user); 
					
					LOGGER.log(Level.INFO, "User connected: " + username);

					send(client, new Message("Server", "Welcome " + username));
					
					Menu menu = new Menu();
					String text = "";

					while (true) {
						if (clients.size() > 0) {
							Message message = (Message) receive(client);
							text = message.getText();
							
							if (menu.isActive()) {

								String command = text.split(" ", 1)[0];
								
								if (command.equals("!help")) {
									message = menu.getHelp();
								}
								else if (command.equals("!list-users")) {
									message = menu.getUsersList(clients);
								}
								else if (command.equals("!back")) {
									menu.setActive(false);
									sendAllMessagesToClient(client);
								}
								else {
									send(user.getClient(), new Message("Server", "Unexpected value: " + command));
									continue;
								}
								send(client, message);
							} 
							else if (text.equals("disconnect")) {
								sendToAll(new Message("Server", String.format("User %s has disconnected.", username)));
								disconnectClient(client);
								LOGGER.info(String.format("User %s disconnected.", username));
							}
							else if(text.equals("!menu")) {
								menu.setActive(true);
								send(user.getClient(), new Message("Server", "Welcome to the menu mode\n !help to show actions available."));
							}
							else if(text.length() > 2 && text.substring(0, 3).equals("!pm")) {
								
								String[] command = text.split(" ", 3);
								String mp = command[0];
								String dest = command[1];
								String content = command[2];
								
								User destUser = clients.get(dest);
								if (destUser != null) {
									user.setCurrentChat(destUser);
									
									send(destUser.getClient(), new Message(username, content, true));
								}
								else {
									send(user.getClient(), new Message("Server", String.format("User %s not found.", dest), true));
								}
							}
							else if(text.length() > 0 && text.substring(0, 1).equals(".")) {
								String content = text.substring(1);
								User dest = user.getCurrentChat();
								
								send(dest.getClient(), new Message(username, content, true));
							}
							else {
								messages.add(message);
								sendToAll(message);
							}
						}
					}

				}
				catch (Exception e) {
					clients.remove(client);
					LOGGER.log(Level.WARNING, e.getMessage(), e);
				}
			}
		};
	}
	
	private void sendToAll(Message message) throws Exception {

		for (Map.Entry<String, User> entry: clients.entrySet()) {
			send(entry.getValue().getClient(), message);
		}
		
	}

	private void sendAllMessagesToClient(Socket client) throws Exception {

		for (Message message : messages) {
			send(client, message);
		}

	}

	private Object receive(Socket client) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
		return ois.readObject();
	}

	private <T> void send(Socket client, T object) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
		oos.writeObject(object);
	}

	private void disconnectClient(Socket client) throws Exception {
		clients.remove(client);
		client.close();
	}
}
