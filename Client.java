import java.util.Scanner; // for the user input
import java.util.regex.*; // for the ip and port address validation
import java.io.DataInputStream;
import java.net.Socket;

public class Client {
	private static Socket socket;

	private static String getIp(Scanner inputObj) {
		System.out.println("Enter the IP address of the server:");
		String ip = inputObj.nextLine();
		// validate the ip address
		String regexZeroTo255 = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
		String regexIP = regexZeroTo255 + "\\." +regexZeroTo255 + "\\."+ regexZeroTo255 + "\\." + regexZeroTo255;
		if (!Pattern.matches(regexIP, ip)) {
			System.out.println("Invalid IP address");
			return getIp(inputObj);
		} 
		else {
			return ip;
		}
	};

	private static Integer getPort(Scanner inputObj) {
		System.out.println("Enter the port number of the server between 5000 and 5050:");
		String port = inputObj.nextLine();
		// validate the Port address
		String regexPort = "((50[0-4][0-9])|(5050))";
		if (!Pattern.matches(regexPort, port)) {
			System.out.println("Invalid Port address");
			return getPort(inputObj);
		} 
		else {
			return Integer.parseInt(port); // convert the string to integer should never throw an exception
		}
	}
	public static void main(String[] args) throws Exception {
		Scanner inputObj = new Scanner(System.in);
		String serverAddress = getIp(inputObj);
		Integer port = getPort(inputObj);
		inputObj.close();
		// String serverAddress = "127.0.0.1";
		// int port = 5000;
		socket = new Socket(serverAddress, port);
		System.out.format("Serveur lancé sur [%s:%d]", serverAddress, port);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		socket.close();
	}
}