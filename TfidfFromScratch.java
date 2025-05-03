import java.util.*;

public class TfidfFromScratch {

    public static void main(String[] args) {
        String[] documents = {
                "ao thun cotton tay ngan co tron",
                "ao so mi tay dai vai lua",
                "ao thun form rong tay ngan",
                "ao so mi form slimfit tay ngan",
                "ao thun tay dai chat lieu thoang mat",
                "ao so mi tay ngan vai chiffon",
                "ao thun cotton co tru tay ngan",
                "ao khoac jean form vua",
                "ao thun oversized tay dai co tron",
                "ao so mi vai lanh tay dai",
                "ao thun basic cotton tay ngan",
                "ao crop top vai thun tay ngan",
                "ao hoodie form rong tay dai",
                "ao polo tay ngan vai thoang",
                "ao cardigan mong tay dai"    
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

        // 5. Tính ma trận Cosine Similarity
        double[][] cosineMatrix = new double[documents.length][documents.length];
        for (int i = 0; i < documents.length; i++) {
            for (int j = 1; j < documents.length; j++) {
                cosineMatrix[i][j] = cosineMatrix[j][i] = cosineSimilarity(tfidf[i], tfidf[j]);
            }
        }
        for (int i = 0; i < documents.length; i++) {
        // Tạo mảng chứa chỉ số các tài liệu khác
            Integer[] indices = new Integer[documents.length];
            for (int j = 0; j < documents.length; j++) {
                indices[j] = j;
            }
        
            // Sắp xếp các chỉ số theo giá trị cosine giảm dần, bỏ qua chính nó (i == j nếu muốn)
            final int currentIndex = i;
            Arrays.sort(indices, (a, b) -> Double.compare(cosineMatrix[currentIndex][b], cosineMatrix[currentIndex][a]));
            System.out.println("Sản phẩm giống với: " + documents[i]);
            for (int k = 0; k < 5; k++) { // In top 5 sản phẩm giống nhất
                int j = indices[k];
                if (j == i) continue; // Bỏ qua chính nó
                System.out.printf("  - %s (%.4f)\n", documents[j], cosineMatrix[i][j]);
            }
            System.out.println();
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
