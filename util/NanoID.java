package util;

import java.security.SecureRandom;
import java.util.Random;

// Referência: https://zelark.github.io/nano-id-cc/
public class NanoID {
    private static final char[] ALPHABET = 
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz-".toCharArray();
    
    private static final int DEFAULT_SIZE = 21;
    
    private static final SecureRandom DEFAULT_NUMBER_GENERATOR = new SecureRandom();
    
    public static String randomNanoId() {
        return randomNanoId(DEFAULT_SIZE);
    }
    
    public static String randomNanoId(int size) {
        return randomNanoId(DEFAULT_NUMBER_GENERATOR, ALPHABET, size);
    }
    
    public static String randomNanoId(char[] alphabet, int size) {
        return randomNanoId(DEFAULT_NUMBER_GENERATOR, alphabet, size);
    }
    
    public static String randomNanoId(Random random, char[] alphabet, int size) {
        if (random == null) {
            throw new IllegalArgumentException("Random não pode ser nulo.");
        }
        
        if (alphabet == null) {
            throw new IllegalArgumentException("Alfabeto não pode ser nulo.");
        }
        
        if (alphabet.length == 0 || alphabet.length >= 256) {
            throw new IllegalArgumentException("Alfabeto deve conter entre 1 e 255 símbolos.");
        }
        
        if (size <= 0) {
            throw new IllegalArgumentException("Tamanho deve ser maior que zero.");
        }
        
        final int mask = (2 << (int) Math.floor(Math.log(alphabet.length - 1) / Math.log(2))) - 1;
        final int step = (int) Math.ceil(1.6 * mask * size / alphabet.length);
        
        final StringBuilder idBuilder = new StringBuilder();
        
        while (true) {
            final byte[] bytes = new byte[step];
            random.nextBytes(bytes);
            
            for (int i = 0; i < step; i++) {
                final int alphabetIndex = bytes[i] & mask;
                
                if (alphabetIndex < alphabet.length) {
                    idBuilder.append(alphabet[alphabetIndex]);
                    if (idBuilder.length() == size) {
                        return idBuilder.toString();
                    }
                }
            }
        }
    }

}
