package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
    public static String encryptPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte messageDigest[] = md.digest(password.getBytes("UTF-8"));

        return new String(messageDigest);
    }

    public static boolean validatePassword(String password, String hashPassword) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return encryptPassword(password) == hashPassword;
    }
}
