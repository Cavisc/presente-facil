package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.indexes.InvertedListElement;

public class InvertedListCalculator {
    public static Map<String, Float> calculateTermFrequency(List<String> tokens) {
        Map<String, Integer> termFrequency = new HashMap<>();
        Map<String, Float> results = new HashMap<>();

        for (String token : tokens) {
            termFrequency.put(token, termFrequency.getOrDefault(token, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
            String term = entry.getKey();
            int frequency = entry.getValue();
            float tf = (float) frequency / tokens.size();
            
            results.put(term, tf);
        }

        return results;
    }

    public static float calculateInverseDocumentFrequency(int total, int documentFrequency) {
        float idf = (float) (Math.log(total / (double) documentFrequency) + 1);

        return idf;
    }

    public static Map<Integer, Float> calculateTFxIDF(Map<Integer, Float> productScores, InvertedListElement[] termResults, float idf) {
        for (InvertedListElement element : termResults) {
            float tfidf = element.getFrequency() * idf;
            int productId = element.getId();
            
            productScores.put(productId, productScores.getOrDefault(productId, 0f) + tfidf);
        }

        return productScores;
    }
}