import java.io.File;

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
    public String download(String pth) {
        return "Not implemented yet";
    }
}
