import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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

    public String upload(String pth) {
        return "Not implemented yet";
    }
    public String download(String fileName, DataOutputStream out) {
        if (fileName != null) {
            File file = new File(currentDir.getAbsolutePath() + "\\" + fileName);
            if (file.exists()) {
                try {
                    out.writeUTF("200");
                    out.writeUTF(file.getName());
                    out.writeLong(file.length());
                    byte[] bytes = new byte[16 * 1024];
                    try (InputStream in = new FileInputStream(file)) {
                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
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
