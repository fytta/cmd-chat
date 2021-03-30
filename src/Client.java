
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	private User user;
	private Socket client;
		
	
	private Socket login(String name, String host, int port) throws Exception {
			
		if (name.length() < 3) {
			throw new Exception("Error: Invalid username");
		}
		
		user   = new User(name);
		client = new Socket(host, port);
		
		send(user);
		
		return client;
	}
	
	private Object receive() throws Exception {
		ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
		return ois.readObject();
	}
	
	private <T> void send(T object) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
		oos.writeObject(object);
	}
	
	private Runnable messagesHandler () {
		return new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Message message = (Message) receive();
						if (!message.getName().equals(user.getName())) {
							System.out.println(String.format("> %s: %s", message.getName(), message.getText()));	
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
				
				while(true) {					
					try {
						text = sc.nextLine();
						message = new Message(user.getName(), text);
						send(message);
						
						if (text.equals("disconnect")) {
							System.exit(0);
						}
					} catch (Exception e) {}			
				}	
			}
		};
	}
	
	public static void main(String[] args) throws Exception {
		
		Client client = new Client();
		String host = "localhost";
		String name = args[0];
		int port = 7777;
		
		if (args[0].equals("--help")) {
			System.out.println(" Usage: java client username host port");
			System.out.println(" - username: Username must have more than 2 letters");
			System.out.println(" - host: ip addres or localhost.");
			System.out.println(" - port: Port to connect to the server.");
		}
		else {
			try {
				client.login(name, host, port);
				new Thread(client.messagesHandler()).start();	
				
				Thread.sleep(500);

				new Thread(client.senderHandler()).start();
			}
			catch(ConnectException e) {
				System.out.println("Server is currently unavailable.");
			}
		}
	}

}
