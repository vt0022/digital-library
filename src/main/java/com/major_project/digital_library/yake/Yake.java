package com.major_project.digital_library.yake;

import com.major_project.digital_library.yake.DataRepresentation.ComposedWord;
import com.major_project.digital_library.yake.DataRepresentation.DataCore;
import com.major_project.digital_library.yake.Wrapper.Levenshtein;
import com.major_project.digital_library.yake.Wrapper.Tuple;
import com.major_project.digital_library.yake.Wrapper.jellyfish;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.major_project.digital_library.yake.Wrapper.max;

public class Yake {

    public static class KeywordExtractorOutput {

        public static KeywordExtractorOutput from(String key, double value) {
            return new KeywordExtractorOutput(key, value);
        }

        public String key() {
            return this.key;
        }

        public double value() {
            return this.value;
        }

        @Override
        public String toString() {
            return String.format("(%s, %f)", this.key, this.value);
        }

        private String key;
        private double value;

        private KeywordExtractorOutput(String key, double value) {
            this.key = key;
            this.value = value;
        }
    }

    public enum DedupAlg {
        jaro,//"jaro_winkler","jaro"
        seqm,//"sequencematcher","seqm"
        levs,//"levenshtein", levs"
    }

    public static class KeywordExtractor {

        public KeywordExtractor(String lan, int n, double dedupLim, DedupAlg dedupFunc, int windowsSize, int top, String[] features) {//def __init__(self, lan="en", n=3, dedupLim=0.9, dedupFunc='seqm', windowsSize=1, top=20, features=None):
            this.lan = lan;
            String resource = String.format("src/main/resources/stopwords/stopwords_%s.txt", this.lan.substring(0, 2).toLowerCase());
            try {
                this.stopword_set = loadFromResource(resource, StandardCharsets.UTF_8);
            } catch (Exception ex) {
                this.stopword_set = loadFromResource(resource, StandardCharsets.ISO_8859_1);
            }
            this.n = n;
            this.top = top;
            this.dedupLim = dedupLim;
            this.features = features;
            this.windowsSize = windowsSize;
            if (dedupFunc == DedupAlg.jaro) {
                this.dedu_function = this::jaro;
            } else if (dedupFunc == DedupAlg.seqm) {
                this.dedu_function = this::seqm;
            } else {
                this.dedu_function = this::levs;
            }
        }

        public KeywordExtractor() {
            this("en", 3, 0.9, DedupAlg.seqm, 1, 20, null);
        }

        public KeywordExtractor(String lan) {
            this(lan, 3, 0.9, DedupAlg.seqm, 1, 20, null);
        }

        public KeywordExtractor(String lan, int n) {
            this(lan, n, 0.9, DedupAlg.seqm, 1, 20, null);
        }

        public KeywordExtractor(String lan, int n, double dedupLim) {
            this(lan, n, dedupLim, DedupAlg.seqm, 1, 20, null);
        }

        public KeywordExtractor(String lan, int n, double dedupLim, DedupAlg dedupFunc) {
            this(lan, n, dedupLim, dedupFunc, 1, 20, null);
        }

        public KeywordExtractor(String lan, int n, double dedupLim, DedupAlg dedupFunc, int windowsSize) {
            this(lan, n, dedupLim, dedupFunc, windowsSize, 20, null);
        }

        public KeywordExtractor(String lan, int n, double dedupLim, DedupAlg dedupFunc, int windowsSize, int top) {
            this(lan, n, dedupLim, dedupFunc, windowsSize, top, null);
        }

        public double jaro(CharSequence cand1, CharSequence cand2) {
            return jellyfish.jaro_winkler(cand1, cand2);
        }

        public double levs(CharSequence cand1, CharSequence cand2) {
            return 1.0 - jellyfish.levenshtein_distance(cand1, cand2) / max(cand1.length(), cand2.length());
        }

        public double seqm(CharSequence cand1, CharSequence cand2) {
            return Levenshtein.ratio(cand1, cand2);
        }

        public List<KeywordExtractorOutput> extract_keywords(String text) {
            text = text.replace("\n\t", " ");
            DataCore dc = new DataCore(text, this.stopword_set, this.windowsSize, this.n, null, null, this.lan);
            dc.build_single_terms_features(this.features);
            dc.build_mult_terms_features(this.features);
            List<Tuple<Object>> resultSet = new ArrayList<>();// []
            //todedup = sorted([cc for cc in dc.candidates.values() if cc.isValid()],key=lambda c: c.H)
            List<ComposedWord> todedup = dc.candidates.values().stream()
                    .filter(ComposedWord::isValid)
                    .sorted((o1, o2) -> Double.compare(o1.H, o2.H))
                    .collect(Collectors.toList());
            if (this.dedupLim >= 1.0) {
                // return ([ (cand.H, cand.unique_kw) for cand in todedup])[:self.top]
                return todedup.stream()
//						.map(cand -> Tuple.from(cand.unique_kw, cand.H))
                        .map(cand -> KeywordExtractorOutput.from(cand.unique_kw, cand.H))
                        .limit(this.top).collect(Collectors.toList());
            }
            for (ComposedWord cand : todedup) {
                boolean toadd = true;
                for (Tuple<Object> tuple : resultSet) {//for (h, candResult) in resultSet:
//					Object h = tuple.value(0);
                    ComposedWord candResult = (ComposedWord) tuple.value(1);
                    double dist = this.dedu_function.apply(cand.unique_kw, candResult.unique_kw);
                    if (dist > this.dedupLim) {
                        toadd = false;
                        break;
                    }
                }
                if (toadd) {
                    resultSet.add(Tuple.from(cand.H, cand));
                }
                if (resultSet.size() == this.top) {
                    break;
                }
            }
            // return [ (cand.unique_kw,h) for (h,cand) in resultSet]
            return resultSet.stream()
//					.map(t -> Tuple.from(((ComposedWord)t.value(1)).unique_kw, t.value(0)))
                    .map(t -> KeywordExtractorOutput.from(((ComposedWord) t.value(1)).unique_kw, (double) t.value(0)))
                    .collect(Collectors.toList());
        }

        private String lan;
        private Set<String> stopword_set;
        private int n;
        private int top;
        private double dedupLim;
        private String[] features;
        private int windowsSize;
        private DedupFunc dedu_function;

        private static interface DedupFunc {
            public double apply(CharSequence cand1, CharSequence cand2);
        }
    }

    private static Set<String> loadFromResource(String resource, Charset charset) {
        Set<String> result = new HashSet<>();
        File file = new File(resource);

        try (FileInputStream inputStream = new FileInputStream(file)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    result.add(line.toLowerCase());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
