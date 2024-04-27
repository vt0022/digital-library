package com.major_project.digital_library.yake;

import com.major_project.digital_library.yake.Yake.KeywordExtractorOutput;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TagExtractor {
    public List<String> findKeywords(String text) {
        String language = "vi";
        int max_ngram_size = 3;
        double deduplication_thresold = 0.9;
        Yake.DedupAlg deduplication_algo = Yake.DedupAlg.jaro;
        int windowSize = 1;
        int numOfKeywords = 30;
        Yake.KeywordExtractor kw_extractor = new Yake.KeywordExtractor(language, max_ngram_size, deduplication_thresold, deduplication_algo, windowSize, numOfKeywords, null);
        List<KeywordExtractorOutput> keywords2 = kw_extractor.extract_keywords(text);

        List<String> excludedKeywords = Arrays.asList("cảm ơn", "đội ơn", "anh chị", "mọi người", "thank", "thanks", "chào", "hello", "xin chào");
        List<String> tags = new ArrayList<>();
        for (KeywordExtractorOutput kw : keywords2) {
            if (kw.key().contains("cảm ơn")
                    || kw.key().contains("anh chị")
                    || kw.key().contains("chào")
                    || kw.key().contains("hello"))
                continue;

            tags.add(kw.key().replace("_", " "));
        }

        return tags;
    }

    public static void main(String[] args) {
        String language = "vi";
        int max_ngram_size = 3;
        double deduplication_threshold = 0.9;
        Yake.DedupAlg deduplication_algo = Yake.DedupAlg.jaro;
        int windowSize = 1;
        int numOfKeywords = 20;
        Yake.KeywordExtractor kw_extractor2 = new Yake.KeywordExtractor(language, max_ngram_size, deduplication_threshold, deduplication_algo, windowSize, numOfKeywords, null);
        List<KeywordExtractorOutput> keywords2 = kw_extractor2.extract_keywords("Tài liệu nghiên cứu AI/ML: ngôn ngữ tự nhiên" + ". " + "Xin chào mọi người.\n" +
                "Hiện tại mình đang thực hiện đồ án cuối kỳ, mình cần một số tài liệu tham khảo về xử lý ngôn ngữ tự nhiên trong AI/ML.\n" +
                "Bạn nào có cho mình xin với hoặc cho mình tên tài liệu cũng được.\n" +
                "Cảm ơn mọi người nhiều.");

        List<String> tags = new ArrayList<>();
        for (KeywordExtractorOutput kw : keywords2) {
            if (kw.key().contains("cảm ơn")
                    || kw.key().contains("anh chị")
                    || kw.key().contains("chào")
                    || kw.key().contains("hello")
                    || kw.key().contains("anh")
                    || kw.key().contains("chị")
                    || kw.key().contains("mọi người"))
                continue;

            tags.add(kw.key().replace("_", " "));
        }

        tags.forEach(tag -> System.out.println(tag));
    }
}
