import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

	private ServerSocket server;

	private Map<Socket, User> clients = new HashMap<Socket, User>();

	private List<Message> messages = new ArrayList<Message>();

	private void execute(int port) {

		try {
			System.out.println("Server listening on: " + port);
			server = new ServerSocket(port);

			Socket client;
			while ((client = server.accept()) != null) {
				new Thread(messageHandler(client)).start();
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private Runnable messageHandler(Socket client) { //TODO: MANAGE NAME COLLISIONS
		return new Runnable() {

			@Override
			public void run() {

				try {
					Menu menu = new Menu();
					User user = (User) receive(client);
					clients.put(client, user);
					LOGGER.log(Level.INFO, "User connected: " + user.getName());

					send(client, new Message("Server", "Welcome " + user.getName()));

					String text = "";

					while (true) {
						if (clients.size() > 0) {
							Message message = (Message) receive(client);
							text = message.getText();

							if (text.equals("disconnect")) {
								sendToAll(new Message("Server", String.format("User %s has disconnected.", user.getName())));
								disconnectClient(client);
								LOGGER.info(String.format("User %s disconnected.", user.getName()));
							}
							else if(text.equals("!menu")) {
								menu.setActive(true);
							}

							if (menu.isActive()) {

								String[] command = text.split(" ");
								menuHandler(client, menu, command);

							} else {
								messages.add(message);
								sendToAll(message);
							}
						}
					}

				} catch (Exception e) {
					clients.remove(client);
					LOGGER.log(Level.WARNING, e.getMessage(), e);
				}
			}

		};
	}
	
	private void menuHandler(Socket client, Menu menu, String[] command) throws Exception{
		Message message;
		switch (command[0]) {
		case "!menu": {
			send(client, new Message("Server",
					"Welcome to the menu mode\n !help to show actions available."));
			break;
		}
		case "!help": {
			message = menu.getHelp();
			send(client, message);
			break;
		}
		case "!back": {
			menu.setActive(false);
			sendAllMessagesToClient(client);
			break;
		}
		case "!list-users": {
			message = menu.getUsersList(clients);
			send(client, message);
			break;
		}
		case "!mp": {
			System.out.println("MP with " + command[1]);
			break;
		}
		default:
			send(client, new Message("Server", "Unexpected value: " + command[0]));
		}	
	}

	private void sendToAll(Message message) throws Exception {

		for (Map.Entry<Socket, User> entry : clients.entrySet()) {
			send(entry.getKey(), message);
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

	public static void main(String[] args) {
		Server server = new Server();
		server.execute(7777);
	}
}
