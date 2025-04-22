import java.util.*;

public class TfidfFromScratch {

    public static void main(String[] args) {
        String[] documents = {
                "ao thun cotton tay ngan",
                "ao so mi tay dai vai lua",
                "ao thun form rong tay ngan co tron"
        };

        List<List<String>> tokenizedDocs = new ArrayList<>();
        Set<String> vocabulary = new HashSet<>();

        // 1. Tách từ và xây dựng từ vựng
        for (String doc : documents) {
            List<String> tokens = Arrays.asList(doc.toLowerCase().split(" "));
            tokenizedDocs.add(tokens);
            vocabulary.addAll(tokens);
        }

        List<String> vocabList = new ArrayList<>(vocabulary);

        // 2. Tính TF
        double[][] tfMatrix = new double[documents.length][vocabList.size()];
        for (int i = 0; i < documents.length; i++) {
            List<String> doc = tokenizedDocs.get(i);
            for (int j = 0; j < vocabList.size(); j++) {
                String term = vocabList.get(j);
                tfMatrix[i][j] = Collections.frequency(doc, term) / (double) doc.size();
            }
        }

        // 3. Tính IDF
        double[] idf = new double[vocabList.size()];
        for (int j = 0; j < vocabList.size(); j++) {
            String term = vocabList.get(j);
            int count = 0;
            for (List<String> doc : tokenizedDocs) {
                if (doc.contains(term))
                    count++;
            }
            idf[j] = Math.log((double) documents.length / (1 + count)); // thêm 1 tránh chia 0
        }

        // 4. Tính TF-IDF
        double[][] tfidf = new double[documents.length][vocabList.size()];
        for (int i = 0; i < documents.length; i++) {
            for (int j = 0; j < vocabList.size(); j++) {
                tfidf[i][j] = tfMatrix[i][j] * idf[j];
            }
        }

        // 5. Tính Cosine Similarity giữa document 0 với các document còn lại
        for (int i = 1; i < documents.length; i++) {
            double sim = cosineSimilarity(tfidf[0], tfidf[i]);
            System.out.printf("Cosine Similarity giữa Document 0 và %d: %.4f\n", i, sim);
        }
    }

    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        if (norm1 == 0 || norm2 == 0)
            return 0.0;
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
