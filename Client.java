import java.util.Scanner; // for the user input
import java.util.regex.*; // for the ip and port address validation
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	private static Socket socket;

	private static String getIp(Scanner inputObj) {
		Printer.print("Enter the IPv4 of the server:", "blue");
		String ip = inputObj.nextLine();
		// validate the ip address
		String regexZeroTo255 = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
		String regexIP = regexZeroTo255 + "\\." + regexZeroTo255 + "\\." + regexZeroTo255 + "\\." + regexZeroTo255;
		if (!Pattern.matches(regexIP, ip)) {
			Printer.print("\t Invalid IP address", "red");
			return getIp(inputObj);
		} else {
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
		} else {
			return Integer.parseInt(port); // convert the string to integer should never throw an exception
		}
	}

	private static String getCommande(Scanner inputObj) {
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
			// Show the command in the server console
			if (commande.matches("mkdir ..*")) {
				os.writeUTF(commande);
				String response = in.readUTF();
				Printer.print(response, "green");
			} else if (commande.equals("ls")) {
				os.writeUTF(commande);
				String response = in.readUTF();
				Printer.print(response, "green");
			} else if (commande.matches("cd ..*")) {
				os.writeUTF(commande);
				String response = in.readUTF();
				Printer.print(response, "green");
			} else if (commande.matches("rmdir ..*")) {
				os.writeUTF(commande);
				String response = in.readUTF();
				Printer.print(response, "green");
			} else if (commande.matches("upload ..*")) {
				File file = new File(commande.split(" ")[1]);
				if (file.exists()) {
					os.writeUTF(commande);
					String response = in.readUTF();
					if (response.equals("200")) {
						os.writeUTF(file.getName());
						os.writeLong(file.length());
						byte[] bytes = new byte[16 * 1024];
						try (InputStream input = new FileInputStream(file)) {
							int count;
							while ((count = input.read(bytes)) > 0) {
								os.write(bytes, 0, count);
							}
						}
						Printer.print("File uploaded successfully", "green");
					} else {
						Printer.print("Server not able to accepte this request", "red");
					}
				} else {
					Printer.print("File not found", "red");
				}
			} else if (commande.matches("download ..*")) {
				os.writeUTF(commande);
				String response = in.readUTF();
				if (response.equals("200")) {
					// code to receive and save the file
					File file = new File(System.getProperty("user.dir")+"\\"+ in.readUTF());
					System.out.println(file.getAbsolutePath());
					long fileSize = in.readLong();
					byte[] bytes = new byte[16 * 1024];
                    try (OutputStream output = new FileOutputStream(file)) {
						int totalCount = 0;
                        int count = 0;
                        while ((totalCount+=count) < fileSize && (count = in.read(bytes)) > 0 ) {
                            output.write(bytes, 0, count);
                        }
                    }
					Printer.print("File downloaded successfully", "green");
				} else {
					Printer.print(response, "red");
				}
			} else {
				Printer.print("Invalid command", "red");
			}
			commande = getCommande(inputObj);

		}catch(

	Exception e)
	{
		Printer.print("Error: " + e.getMessage(), "red");
	}
	// keep the client alive
	socket.close();
}}