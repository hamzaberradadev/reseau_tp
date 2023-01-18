import java.util.Scanner; // for the user input
import java.util.regex.*; // for the ip and port address validation
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client {
	private static Socket socket;

	private static String getIp(Scanner inputObj) {
		Printer.print("Enter the IP address of the server:", "blue");
		String ip = inputObj.nextLine();
		// validate the ip address
		String regexZeroTo255 = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
		String regexIP = regexZeroTo255 + "\\." +regexZeroTo255 + "\\."+ regexZeroTo255 + "\\." + regexZeroTo255;
		if (!Pattern.matches(regexIP, ip)) {
			Printer.print("\t Invalid IP address", "red");
			return getIp(inputObj);
		} 
		else {
			return ip;
		}
	};

	private static Integer getPort(Scanner inputObj) {
		Printer.print("Enter the port number of the server between 5000 and 5050:", "blue");
		String port = inputObj.nextLine();
		// validate the Port address
		String regexPort = "((50[0-4][0-9])|(5050))";
		if (!Pattern.matches(regexPort, port)) {
			Printer.print("\t Invalid Port address", "red");
			return getPort(inputObj);
		} 
		else {
			return Integer.parseInt(port); // convert the string to integer should never throw an exception
		}
	}
	private static String getCommande(Scanner inputObj){
		Printer.print("Enter commande:", "blue");
		String commande = inputObj.nextLine();
		return commande;
	}
	public static void main(String[] args) throws Exception {
		Scanner inputObj = new Scanner(System.in);
		String serverAddress = getIp(inputObj);
		Integer port = getPort(inputObj);
		socket = new Socket(serverAddress, port);
		System.out.format("Serveur lancé sur [%s:%d]\n", serverAddress, port);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		String helloMessageFromServer = in.readUTF();
		Printer.print(helloMessageFromServer, "green");
		
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		// PrintWriter out = new PrintWriter(os);
		String commande = getCommande(inputObj);
		while (!Pattern.matches("(exit)",commande )) try{
			os.writeUTF(commande);
			String response = in.readUTF();
			Printer.print(response, "green");
			commande = getCommande(inputObj);
		} catch (Exception e) {
			Printer.print("Error: " + e.getMessage(), "red");
		}
		// keep the client alive
		socket.close();
	}
}