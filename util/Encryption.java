package util;

public class Encryption {
    public static boolean validatePassword(String password, String hashPassword) {
        // Implementar Hash
        return password == hashPassword;
    }
}
