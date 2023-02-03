import java.util.Scanner; // for the user input
import java.util.regex.*; // for the ip and port address validation
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;


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

	private static void onConnextion(DataInputStream in, DataOutputStream os, Scanner inputObj) throws Exception {
		String commande = getCommande(inputObj);
		while (!Pattern.matches("(exit)",commande )) try{

			// Show the command in the server console
			if (commande.matches("mkdir ..*")) {
				handleTextCommande(in, os, commande);

			} else if (commande.equals("ls")) {
				handleTextCommande(in, os, commande);

			} else if (commande.matches("cd ..*")) {
				handleTextCommande(in, os, commande);
				
			} else if (commande.matches("rmdir ..*")) {
				handleTextCommande(in, os, commande);

			} else if (commande.matches("upload ..*")) {
				handleUpload(in, os, commande);

			} else if (commande.matches("download ..*")) {
				handleDownload(in, os, commande);

			} else {
				Printer.print("Invalid command", "red");
			}
			commande = getCommande(inputObj);

		}catch(

	Exception e)
	{
		Printer.print("Error: " + e.getMessage(), "red");
	}
	}

	private static void handleTextCommande(DataInputStream in, DataOutputStream os, String commande) throws IOException {
		os.writeUTF(commande);
		String response = in.readUTF();
		Printer.print(response, "green");
	}

	private static void handleDownload(DataInputStream in, DataOutputStream os, String commande)
			throws IOException, SocketException, FileNotFoundException {
		os.writeUTF(commande);
		String response = in.readUTF();
		if (response.equals("200")) {

			// code to receive and save the file
			File file = new File(System.getProperty("user.dir")+"\\"+ in.readUTF());
			long fileSize = in.readLong();

			int maxSize = (int) Math.min(fileSize, Integer.MAX_VALUE);
			byte[] bytes = new byte[maxSize];
			
		    try (OutputStream output = new FileOutputStream(file)) {
				int totalCount = 0;
		        int count = 0;
		        while ((totalCount+=count) < fileSize && (count = in.read(bytes)) > 0 ) {
		            output.write(bytes, 0, count);
		        }
		    }
			Printer.print("File downloaded successfully" + response, "green");
		} else {
			Printer.print(response, "red");
		}
	}

	private static void handleUpload(DataInputStream in, DataOutputStream os, String commande)
			throws IOException, SocketException, FileNotFoundException {
		File file = new File(commande.split(" ")[1]);
		if (file.exists()) {
			os.writeUTF("upload");
			os.writeUTF(file.getName());
			os.writeLong(file.length());

			// send the 200 code to the server to start the upload
			String response = in.readUTF();
			if (response.equals("200")) {

				int maxSize = (int) Math.min(file.length(), Integer.MAX_VALUE);
				byte[] bytes = new byte[maxSize];

				try (InputStream input = new FileInputStream(file)) {
					int count;
					long total = 0;
					while ((count = input.read(bytes)) > 0 && total < file.length()) {
						os.write(bytes, 0, count);
						total += count;
					}
				}

				Printer.print("File uploaded successfully", "green");
			} else {
				Printer.print("Server not able to accepte this request", "red");
			}
		} else {
			Printer.print("File not found", "red");
		}
	}
	public static void main(String[] args) throws Exception {
		Scanner inputObj = new Scanner(System.in);
		String serverAddress = getIp(inputObj);
		Integer port = getPort(inputObj);
		socket = new Socket(serverAddress, port);
		System.out.format("Serveur lancé sur [%s:%d]\n", serverAddress, port);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		onConnextion(in,os,inputObj);
		socket.close();
}}