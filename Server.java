import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.regex.Pattern;
public class Server {
	private static ServerSocket Listener;


	private static String getIp(Scanner inputObj) {
    Printer.print("Enter the Adresse IPv4 of this machine :", "blue");
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
    Printer.print("Enter the port number to open between 5000 and 5050:", "blue");
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
	public static void main(String[] args) throws Exception {
		Scanner inputObj = new Scanner(System.in);
		int clientNumber = 0;
		String serverAddress =getIp(inputObj);
		int serverPort = getPort(inputObj);
	Listener = new ServerSocket();
	Listener.setReuseAddress(true);
	InetAddress serverIP = InetAddress.getByName(serverAddress);
	Listener.bind(new InetSocketAddress(serverIP, serverPort));
	System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
try {

	while (true) {
		new ClientHandler(Listener.accept(), clientNumber++).start();
	}
} finally {
	Listener.close();
} } }