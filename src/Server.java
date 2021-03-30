import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
	
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

	private ServerSocket server;

	private Set<Socket> clients = new HashSet<Socket>();

	private List<Message> messages = new ArrayList<Message>();

	private void execute(int port) {

		try {
			System.out.println("Server listening on: " + port);
			server = new ServerSocket(port);

			Socket client;
			while ((client = server.accept()) != null) {
				clients.add(client);
				new Thread(messageHandler(client)).start();
			}
		}
		catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private Runnable messageHandler(Socket client) {
		return new Runnable() {

			@Override
			public void run() {
				
				try {
					User user = (User) receive(client);
					
					LOGGER.log(Level.INFO, "User connected: "+user.getName());
					
					send(client, new Message("Server", "Welcome " + user.getName()));

					String text = "";
					
					while (true) {
						if (clients.size() > 0) {
							Message message = (Message) receive(client);
							
							messages.add(message);

							sendToAll(message);

							text = message.getText();

							if (text.equals("disconnect")) {
								sendToAll(new Message("Server", String.format("User %s has disconnected.", user.getName())));
								disconnectClient(client);
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

	private void sendToAll(Message message) throws Exception {
		for (Socket client : clients) {
			System.out.println(clients.size());
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
