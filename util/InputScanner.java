package util;

import java.util.Scanner;

public class InputScanner {
    private static final Scanner SCANNER = new Scanner(System.in);
    
    public static Scanner getScanner() {
        return SCANNER;
    }
}