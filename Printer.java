public class Printer {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void print(String text, String color) {
        switch(color){
            case "black":
                System.out.println(ANSI_BLACK + text + ANSI_RESET);
                break;
            case "red":
                System.out.println(ANSI_RED + text + ANSI_RESET);
                break;
            case "green":
                System.out.println(ANSI_GREEN + text + ANSI_RESET);
                break;
            case "yellow":
                System.out.println(ANSI_YELLOW + text + ANSI_RESET);
                break;
            case "blue":
                System.out.println(ANSI_BLUE + text + ANSI_RESET);
                break;
            default:
                System.out.println( text );
                break;
        }
    }
}
