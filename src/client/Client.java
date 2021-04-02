package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

import model.Message;

/**
 * Connect to the cmd-chat server and allows you to 
 * manage the messages from cmd terminal.
 * 
 * @author fytta
 *
 */
public class Client {

	private String username;
	private Socket client;
	private boolean inMenu = false;

	public static void main(String[] args) {

		try {
			if (args[0].equals("--help")) {
				System.out.println(" Usage: java client username host port");
				System.out.println(" - username: Username must have more than 2 letters");
				System.out.println(" - host: ip addres or localhost.");
				System.out.println(" - port: Port to connect to the server.");
			} else {
				Client client = new Client();
				String name = args[0];
				String host = args[1];
				int port = Integer.parseInt(args[2]);

				client.login(name, host, port);
				new Thread(client.messagesHandler()).start();

				Thread.sleep(500);

				new Thread(client.senderHandler()).start();
			}

		} catch (ConnectException e) {
			System.out.println("Server is currently unavailable.");
		} catch (NumberFormatException e) {
			System.out.println("Invalid port. Must be an integer.");
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Invalid parameters. Required parameters: 'username host port");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private Socket login(String name, String host, int port) throws Exception {

		if (name.length() < 3) {
			throw new Exception("Error: Invalid username");
		}

		username = name;
		client = new Socket(host, port);

		send(username);

		return client;
	}

	private Runnable messagesHandler() {
		return new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Message message = (Message) receive();

						if (!message.getName().equals(username)) {

							String messageFormatted = null;

							if (inMenu) {
								if (message.getName().equals("Server")) {
									messageFormatted = String.format("> %s: %s", message.getName(), message.getText())
											.toString();
								}
							} else {
								if (message.isPrivateMessage()) {
									messageFormatted = String
											.format("<< %s: %s >>", message.getName(), message.getText()).toString();
								} else {
									messageFormatted = String.format("> %s: %s", message.getName(), message.getText())
											.toString();
								}

							}
							System.out.println(messageFormatted);
						}

					} catch (Exception e) {
						System.out.println("Connection closed unexpectedly.");
						System.exit(1);
					}
				}
			}
		};
	}

	private Runnable senderHandler() {
		return new Runnable() {

			@Override
			public void run() {
				Message message;
				String text = "";
				Scanner sc = new Scanner(System.in);

				while (true) {
					try {
						text = sc.nextLine();
						message = new Message(username, text);
						send(message);

						if (text.equals("disconnect")) {
							System.exit(0);
						} else if (text.equals("!back")) {
							inMenu = false;
						} else if (text.equals("!menu")) {
							inMenu = true;
						}
					} catch (Exception e) {
					}
				}
			}
		};
	}

	private Object receive() throws Exception {
		ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
		return ois.readObject();
	}

	private <T> void send(T object) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
		oos.writeObject(object);
	}

}
