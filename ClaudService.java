import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClaudService {
    private String currentPath;
    private File currentDir;

    public ClaudService() {

        this.currentPath = ":C";
        this.currentDir = new File(System.getProperty("user.dir") + "/serverFiles");
        if (!currentDir.exists()) {
            currentDir.mkdir();
        }
    }

    public String ls() {
        return this.currentPath + "-->\n\t" + String.join("\n\t", currentDir.list()) + "\n";
    }

    public String cd(String pth) {
        switch (pth) {
            case "./":
                break;
            case ".":
                break;
            case "..":
                if (!this.currentPath.matches("(:C)")) {
                    this.currentDir = this.currentDir.getParentFile();
                    this.currentPath = this.currentPath.substring(0, currentPath.lastIndexOf("/"));
                }
                break;
            default:
                File newDir = new File(currentDir.getAbsolutePath() + "/" + pth);
                if (newDir.exists() && newDir.isDirectory()) {
                    currentDir = newDir;
                    currentPath += "/" + pth;
                }else{
                    return "Directory does not exist";
                }
                break;
        }
        return this.currentPath;
    }

    public String mkdir(String pth) {
        File newDir = new File(currentDir.getAbsolutePath() + "/" + pth);
        if (!newDir.exists()) {
            newDir.mkdir();
            return "Directory created successfully";
        }
        return "Directory already exists";
    }

    public String rmdir(String pth) {
        File newDir = new File(currentDir.getAbsolutePath() + "/" + pth);
        if (newDir.exists()) {
            newDir.delete();
            return "Directory deleted successfully";
        }
        return "Directory does not exist";
    }

    public String upload(DataInputStream in, DataOutputStream out, Socket socket) throws IOException {
        String fileName = in.readUTF();
        long fileLength = in.readLong();
        File file = new File(currentDir.getAbsolutePath() + "\\" + fileName);
        try (DataOutputStream fileOut = new DataOutputStream(new FileOutputStream(file))) {
            out.writeUTF("200");

            int maxSize = (int) Math.min(fileLength, Integer.MAX_VALUE);
            byte[] bytes = new byte[maxSize];

            int count;
            while (fileLength > 0 && (count = in.read(bytes, 0, maxSize)) > 0) {
                fileOut.write(bytes, 0, count);
                fileLength -= count;
            }
            return "File uploaded successfully";
        } catch (Exception e) {
            return "Error uploading file";
        }
    }
    public String download(String fileName, DataOutputStream out, Socket socket) {
        if (fileName != null) {
            File file = new File(currentDir.getAbsolutePath() + "\\" + fileName);
            if (file.exists()) {
                try {
                    out.writeUTF("200");
                    out.writeUTF(file.getName());
                    out.writeLong(file.length());

                    int maxSize = (int) Math.min(file.length(), Integer.MAX_VALUE);
                    byte[] bytes = new byte[maxSize];

                    try (InputStream in = new FileInputStream(file)) {
                        int count;
                        int total = 0;
                        while ((count = in.read(bytes)) > 0 && total < file.length()) {
                            out.write(bytes, 0, count);
                            total += count;
                        }
                    }
                    return "File found";
                } catch (Exception e) {
                    return "Error sending file";
                }
            }
            return "File not found";
        }
        return "File not found";
    }
}
