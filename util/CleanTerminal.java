package util;

public class CleanTerminal {
    public static void displayHeader() {
        System.out.println("PresenteFácil 1.0");
        System.out.println("-----------------");
    }

    public static void clean() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        displayHeader();
    }
}
