package m2i.mr.model;

import java.util.List;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.FrenchStemmer;
import AlKhalil2.morphology.analyzer.AnalyzerTokens;
import AlKhalil2.morphology.result.model.Result;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import m2i.mr.normalization.ArabicNormalizer;

public class Stemming {

    private static final AnalyzerTokens analyzer = new AnalyzerTokens();
    private static final FrenchStemmer analyzerFr = new FrenchStemmer();
    private static final EnglishStemmer analyzerEn = new EnglishStemmer();

    public static String stemmerFr(String mot) {
        analyzerFr.setCurrent(mot);
        analyzerFr.stem();
        return analyzerFr.getCurrent().toLowerCase().trim();
    }

    public static String stemmerEn(String mot) {
        analyzerEn.setCurrent(mot);
        analyzerEn.stem();
        return analyzerEn.getCurrent().toLowerCase().trim();
    }

    public static Map<String, Set<String>> stemmer(String mot) {
        mot = mot.trim();
        Map<String, Set<String>> map = new HashMap();
        Set<String> stems;
        Set<String> lemmes;
        stems = map.getOrDefault("stem", new HashSet());
        lemmes = map.getOrDefault("lemme", new HashSet());
        List<Result> liste = analyzer.analyzerToken(mot);
        for (Result result : liste) {
            stems.add(ArabicNormalizer.cleanString(result.getStem()));
            lemmes.add(ArabicNormalizer.cleanString(result.getLemma()));
        }
        map.put("stem", stems);
        map.put("lemme", lemmes);
        return map;
    }

    public static void main(String[] args) {
        System.out.println(stemmer("يمارسونها"));
        System.out.println(stemmer("يستأجر"));
        System.out.println(stemmer("يامتقع"));
        System.out.println("فَنُّ العِمَارَة");
        System.out.println(stemmer("هَنْدَسَةُ"));
        //System.out.println(stemmerFr("meaning"));
        //System.out.println(stemmerEn("meaning"));
    }
}
