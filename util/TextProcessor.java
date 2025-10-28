package util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class TextProcessor {
    private static Set<String> stopWords = new HashSet<>(Arrays.asList(
        "de", "a", "o", "que", "e", "do", "da", "em", "um", "para", "é", "com", 
        "não", "uma", "os", "no", "se", "na", "por", "mais", "as", "dos", "como", 
        "mas", "foi", "ao", "ele", "das", "tem", "à", "seu", "sua", "ou", "ser", 
        "quando", "muito", "há", "nos", "já", "está", "eu", "também", "só", "pelo", 
        "pela", "até", "isso", "ela", "entre", "era", "depois", "sem", "mesmo", 
        "aos", "ter", "seus", "quem", "nas", "me", "esse", "eles", "estão", "você", 
        "tinha", "foram", "essa", "num", "nem", "suas", "meu", "às", "minha", "têm", 
        "numa", "pelos", "elas", "havia", "seja", "qual", "será", "nós", "tenho", 
        "lhe", "deles", "essas", "esses", "pelas", "este", "fosse", "dele", "tu", 
        "te", "vocês", "vos", "lhes", "meus", "minhas", "teu", "tua", "teus", 
        "tuas", "nosso", "nossa", "nossos", "nossas", "dela", "delas", "esta", 
        "estes", "estas", "aquele", "aquela", "aqueles", "aquelas", "isto", "aquilo"
    ));

    public static String normalize(String text) {
        if (text == null) return "";
        
        // Remove acentos
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        
        // Converte para minúsculas e remove caracteres especiais
        return text.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
    }

    public static List<String> tokenize(String text) {
        String normalized = normalize(text);
        String[] words = normalized.split("\\s+");
        
        return Arrays.stream(words)
                .filter(word -> !word.isEmpty() && !stopWords.contains(word) && word.length() > 2)
                .toList();
    }
}