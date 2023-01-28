import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat; // for date format
import java.util.Date; // for date parsing

public class ClientHandler extends Thread {
	private Socket socket;
	private int clientNumber;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'@'HH:mm:ss");
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		Printer.print("New connection with client#" + clientNumber + " at " + socket, "green");
	}

	public Boolean isClientAlive() {
		return socket.isConnected();
	}

	public void showCommand(String command) {
		String log = "[ "+socket.getRemoteSocketAddress().toString().substring(1)+" - "+sdf.format(new Date())+" ]";
		Printer.print(log + " : " + command, "yellow");
	}

	@Override
	public void run() {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("Hello from server - you are client#" + clientNumber);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			ClaudService s = new ClaudService();

			while (isClientAlive()) {
				String command = in.readUTF();
				// Show the command in the server console
				showCommand(command);
				if (command.matches("mkdir ..*")) {
					String dirName = command.replace("mkdir ", "");
					out.writeUTF(s.mkdir(dirName));
				} else if (command.equals("ls")) {
					out.writeUTF(s.ls());
				} else if (command.matches("cd ..*")) {
					String dirName = command.replace("cd ", "");
					out.writeUTF(s.cd(dirName));
				} else if (command.matches("rmdir ..*")) {
					String dirName = command.replace("rmdir ", "");
					out.writeUTF(s.rmdir(dirName));
				} else if (command.matches("upload ..*")) {
					// code to receive and save the file
				} else if (command.matches("download ..*")) {
					String filePath = command.replace("download ", "");
					s.download(filePath, out);
				} else {
					out.writeUTF("Invalid command");
				}
			}
		} catch (IOException e) {
			Printer.print("Error handling client# " + clientNumber + ": " + e, "red");
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				Printer.print("Couldn't close a socket, what's going on?", "red");
			}
			Printer.print("Connection with client# " + clientNumber + " closed", "yellow");
		}
	}

}
