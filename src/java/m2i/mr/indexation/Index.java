package m2i.mr.indexation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.Collator;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import m2i.mr.model.Dist;
import m2i.mr.model.FileRanking;
import m2i.mr.model.Resultat;
import m2i.mr.normalization.ArabicNormalizer;
import m2i.mr.normalization.EnglishNormalizer;
import m2i.mr.normalization.FrenchNormalizer;
import m2i.mr.model.Stemming;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author imac
 */
public class Index {

    private static final String MAX_VALUE = "" + Character.MAX_VALUE;
    public static final String AR = "ar", FR = "fr", EN = "en";
    public static final String STEM = "stem", LEMME = "lemme", WORD = "word", ALL = "all";
    public static final String TALK = "Talk";
    public static final String SEGMENT = "Segment";
    public static final String ID = "id";
    public static final String ORIGINAL = "Original_text";
    public static final String TRANSLATION = "Translation";
    public static final String LANG = "lang";
    public static final String SPACE = " ";
    public static final String SPACE1 = ",";
    public static final String SPACE2 = ";";
    public static final String SPACE3 = ":";
    public static final String SPACE4 = "::";
    public static final String SPACE5 = "::::";
    public static final String POINT = ".";
    public static final String SLASH = "\n";
    public static String root = "web" + File.separator + "MultedCorpus" + File.separator;
    public static String corpus = "Corpus";
    public static String dictionaryARFR = "Dictionary" + File.separator + AR + "-" + FR + ".csv";
    public static String dictionaryAREN = "Dictionary" + File.separator + AR + "-" + EN + ".csv";
    public static String dictionaryFRAR = "Dictionary" + File.separator + FR + "-" + AR + ".csv";
    public static String dictionaryENAR = "Dictionary" + File.separator + EN + "-" + AR + ".csv";
    public static String stopWordsAr = "StopWords" + File.separator + AR + ".txt";
    public static String stopWordsFr = "StopWords" + File.separator + FR + ".txt";
    public static String stopWordsEn = "StopWords" + File.separator + EN + ".txt";
    public static String indexWordAr = "Index" + File.separator + WORD + File.separator + AR + ".txt";
    public static String indexWordFr = "Index" + File.separator + WORD + File.separator + FR + ".txt";
    public static String indexWordEn = "Index" + File.separator + WORD + File.separator + EN + ".txt";
    public static String indexLemmeAr = "Index" + File.separator + LEMME + File.separator + AR + ".txt";
    public static String indexStemAr = "Index" + File.separator + STEM + File.separator + AR + ".txt";
    public static String indexStemFr = "Index" + File.separator + STEM + File.separator + FR + ".txt";
    public static String indexStemEn = "Index" + File.separator + STEM + File.separator + EN + ".txt";
    public static String filesIndex = "Index" + File.separator + "filesIndex.txt";

    public static final TreeMap<String, TreeMap<Integer, FileRanking>> indexAr = new TreeMap();
    public static final TreeMap<String, TreeMap<Integer, FileRanking>> indexFr = new TreeMap();
    static final TreeMap<String, TreeMap<Integer, FileRanking>> indexEn = new TreeMap();
    static final TreeMap<String, Set<String>> indexArStem = new TreeMap();
    static final TreeMap<String, Set<String>> indexArLemme = new TreeMap();
    static final TreeMap<String, Set<String>> indexFrStem = new TreeMap();
    static final TreeMap<String, Set<String>> indexEnStem = new TreeMap();
    static final Set<String> stopWordAr = new HashSet();
    static final Set<String> stopWordFr = new HashSet();
    static final Set<String> stopWordEn = new HashSet();
    static final TreeMap<String, Set<Dist>> dictionaryArToFr = new TreeMap();
    static final TreeMap<String, Set<Dist>> dictionaryArToEn = new TreeMap();
    static final TreeMap<String, Set<Dist>> dictionaryFrToAr = new TreeMap();
    static final TreeMap<String, Set<Dist>> dictionaryEnToAr = new TreeMap();

    static final Set<String> tokenRemoveAr = new HashSet();
    static final Set<String> tokenRemoveFr = new HashSet();
    static final Set<String> tokenRemoveEn = new HashSet();

    public static final Map<String, Integer> index = new HashMap();
    public static final TreeMap<Integer, String> indexFiles = new TreeMap();
    public static boolean check = false;
    public static final Map<Integer, Map<String, Map<String, String>>> map = new HashMap();
    private static boolean buildIndex = false;
    private static boolean indexReader = false;

    static {
        try {
            //buildIndex();
            //index();
        } catch (Exception ex) {
            Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setRootPathCorpus(String rootPath) {
        root = rootPath + File.separator;
    }

    private static void writeFilesCorpus() throws Exception {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filesIndex))) {
            File[] listFiles = new File(root + corpus).listFiles();
            StringBuilder builder = new StringBuilder();
            Integer i = 0;
            for (File file : listFiles) {
                if (!file.getName().startsWith(POINT)) {
                    index.put(file.getName(), i);
                    indexFiles.put(i, file.getName());
                    builder.append(file.getName()).append(SPACE5).append(i).append(SLASH);
                    i++;
                }
            }
            out.write(builder.toString());
        }
    }

    private static void readFilesCorpus() throws Exception {
        try (BufferedReader in = new BufferedReader(new FileReader(root + filesIndex))) {
            String ligne;
            while ((ligne = in.readLine()) != null) {
                String[] elements = ligne.split(SPACE5);
                if (elements != null && elements.length == 2) {
                    index.put(elements[0], Integer.parseInt(elements[1]));
                    indexFiles.put(Integer.parseInt(elements[1]), elements[0]);
                }
            }
        }
    }

    private static void cleanIndex() {
        indexAr.entrySet().stream().map((entry) -> entry.getKey()).filter((key) -> (stopWordAr.contains(key))).forEachOrdered((key) -> {
            tokenRemoveAr.add(key);
        });
        indexFr.entrySet().stream().map((entry) -> entry.getKey()).filter((key) -> (stopWordFr.contains(key))).forEachOrdered((key) -> {
            tokenRemoveFr.add(key);
        });
        indexEn.entrySet().stream().map((entry) -> entry.getKey()).filter((key) -> (stopWordEn.contains(key))).forEachOrdered((key) -> {
            tokenRemoveEn.add(key);
        });
        for (Iterator<String> iterator = tokenRemoveAr.iterator(); iterator.hasNext();) {
            indexAr.remove(iterator.next());
        }
        for (Iterator<String> iterator = tokenRemoveFr.iterator(); iterator.hasNext();) {
            indexFr.remove(iterator.next());
        }
        for (Iterator<String> iterator = tokenRemoveEn.iterator(); iterator.hasNext();) {
            indexEn.remove(iterator.next());
        }
        tokenRemoveAr.clear();
        tokenRemoveFr.clear();
        tokenRemoveEn.clear();
    }

    public static void buildXMLFile() {
        map.clear();
        File[] listFiles = new File(root + corpus).listFiles();
        SAXBuilder sxb = new SAXBuilder();
        for (File file : listFiles) {
            if (!file.getName().startsWith(POINT)) {
                try {
                    Element racine = sxb.build(file).getRootElement();
                    List<Element> listeTalk = racine.getChildren(TALK);
                    for (Element talk : listeTalk) {
                        String speaker = talk.getAttributeValue("Speaker");
                        String title = talk.getAttributeValue("Title");
                        Map<String, Map<String, String>> map11 = map.getOrDefault(Index.index.get(file.getName()), new HashMap());
                        Map<String, String> map22 = map11.getOrDefault(0, new HashMap());
                        map22.put("Speaker", speaker);
                        map22.put("Title", title);
                        map11.put("0", map22);
                        map.put(Index.index.get(file.getName()), map11);
                        List<Element> segments = talk.getChildren(SEGMENT);
                        for (Element segment : segments) {
                            String id = segment.getAttributeValue(ID).trim();
                            List<Element> originalTexts = segment.getChildren(ORIGINAL);
                            List<Element> translations = segment.getChildren(TRANSLATION);
                            for (Element tran : translations) {
                                String motTalk = tran.getText();
                                Map<String, Map<String, String>> map1 = map.getOrDefault(Index.index.get(file.getName()), new HashMap());
                                Map<String, String> map2 = map1.getOrDefault(id, new HashMap());
                                map2.put(tran.getAttributeValue(LANG), motTalk);
                                map1.put(id, map2);
                                map.put(Index.index.get(file.getName()), map1);
                            }
                        }
                    }
                } catch (IOException | JDOMException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void buildIndex() throws Exception {
        if (buildIndex) {
            return;
        }
        buildIndex = true;
        map.clear();
        writeFilesCorpus();
        readStopWords();
        File[] listFiles = new File(root + corpus).listFiles();
        SAXBuilder sxb = new SAXBuilder();
        for (File file : listFiles) {
            if (!file.getName().startsWith(POINT)) {
                try {
                    Element racine = sxb.build(file).getRootElement();
                    List<Element> listeTalk = racine.getChildren(TALK);
                    for (Element talk : listeTalk) {
                        String speaker = talk.getAttributeValue("Speaker");
                        String title = talk.getAttributeValue("Title");
                        Map<String, Map<String, String>> map11 = map.getOrDefault(Index.index.get(file.getName()), new HashMap());
                        Map<String, String> map22 = map11.getOrDefault(0, new HashMap());
                        map22.put("Speaker", speaker);
                        map22.put("Title", title);
                        map11.put("0", map22);
                        map.put(Index.index.get(file.getName()), map11);
                        List<Element> segments = talk.getChildren(SEGMENT);
                        for (String token : getAllTokensEn(title)) {
                            TreeMap<Integer, FileRanking> map = indexEn.getOrDefault(token, new TreeMap());
                            FileRanking set = map.getOrDefault(index.get(file.getName()), new FileRanking(index.get(file.getName())));
                            set.add();
                            map.put(index.get(file.getName()), set);
                            indexEn.put(token, map);
                        }
                        for (Element segment : segments) {
                            String id = segment.getAttributeValue(ID).trim();
                            List<Element> originalTexts = segment.getChildren(ORIGINAL);
                            List<Element> translations = segment.getChildren(TRANSLATION);
                            originalTexts.stream().map((element) -> getAllTokensEn(element.getText())).forEachOrdered((text) -> {
                                for (String token : text) {
                                    TreeMap<Integer, FileRanking> map = indexEn.getOrDefault(token, new TreeMap());
                                    FileRanking set = map.getOrDefault(index.get(file.getName()), new FileRanking(index.get(file.getName())));
                                    set.add(id);
                                    map.put(index.get(file.getName()), set);
                                    indexEn.put(token, map);
                                }
                            });
                            for (Element tran : translations) {
                                String motTalk = tran.getText();
                                Map<String, Map<String, String>> map1 = map.getOrDefault(Index.index.get(file.getName()), new HashMap());
                                Map<String, String> map2 = map1.getOrDefault(id, new HashMap());
                                map2.put(tran.getAttributeValue(LANG), motTalk);
                                map1.put(id, map2);
                                map.put(Index.index.get(file.getName()), map1);
                                switch (tran.getAttributeValue(LANG)) {
                                    case AR: {
                                        getAllTokensAr(motTalk).forEach((token) -> {
                                            TreeMap<Integer, FileRanking> map = indexAr.getOrDefault(token, new TreeMap());
                                            FileRanking set = map.getOrDefault(index.get(file.getName()), new FileRanking(index.get(file.getName())));
                                            set.add(id);
                                            map.put(index.get(file.getName()), set);
                                            indexAr.put(token, map);
                                        });
                                        break;
                                    }
                                    case FR: {
                                        for (String token : getAllTokensFr(motTalk)) {
                                            TreeMap<Integer, FileRanking> map = indexFr.getOrDefault(token, new TreeMap());
                                            FileRanking set = map.getOrDefault(index.get(file.getName()), new FileRanking(index.get(file.getName())));
                                            set.add(id);
                                            map.put(index.get(file.getName()), set);
                                            indexFr.put(token, map);
                                        }
                                        break;
                                    }
                                    case EN: {
                                        for (String token : getAllTokensEn(motTalk)) {
                                            TreeMap<Integer, FileRanking> map = indexEn.getOrDefault(token, new TreeMap());
                                            FileRanking set = map.getOrDefault(index.get(file.getName()), new FileRanking(index.get(file.getName())));
                                            set.add(id);
                                            map.put(index.get(file.getName()), set);
                                            indexEn.put(token, map);
                                        }
                                        break;
                                    }
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                } catch (IOException | JDOMException e) {
                    e.printStackTrace();
                }
            }
        }
        cleanIndex();
        saveWord();
        buildStemAndLemme();
        saveStemAndLemme();
    }

    public static void index() throws Exception {
        if (indexReader) {
            return;
        }
        indexReader = true;
        readFilesCorpus();
        readStopWords();
        readWord();
        readStemAndLemme();
        readDictionary();
        check = true;
        buildXMLFile();
    }

    private static long readStopWords() throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(root + stopWordsAr));
        String ligne = in.readLine().toLowerCase().trim();
        in.close();
        stopWordAr.clear();
        stopWordAr.addAll(Arrays.asList(ligne.split(SPACE1)));

        in = new BufferedReader(new FileReader(root + stopWordsFr));
        ligne = in.readLine().toLowerCase().trim();
        in.close();
        stopWordFr.clear();
        stopWordFr.addAll(Arrays.asList(ligne.split(SPACE1)));

        in = new BufferedReader(new FileReader(root + stopWordsEn));
        ligne = in.readLine().toLowerCase().trim();
        in.close();
        stopWordEn.clear();
        stopWordEn.addAll(Arrays.asList(ligne.split(SPACE1)));
        return stopWordEn.size();
    }

    private static void saveWord() throws Exception {
        BufferedWriter ar = new BufferedWriter(new FileWriter(indexWordAr));
        BufferedWriter fr = new BufferedWriter(new FileWriter(indexWordFr));
        BufferedWriter en = new BufferedWriter(new FileWriter(indexWordEn));

        StringBuilder builderAr = new StringBuilder();
        StringBuilder builderFr = new StringBuilder();
        StringBuilder builderEn = new StringBuilder();

        for (Map.Entry<String, TreeMap<Integer, FileRanking>> entry : indexAr.entrySet()) {
            builderAr.append(entry.getKey()).append(SPACE3);
            for (FileRanking map : entry.getValue().values()) {
                builderAr.append(SPACE3).append(map.toString());
            }
            builderAr.append(SLASH);
        }

        for (Map.Entry<String, TreeMap<Integer, FileRanking>> entry : indexFr.entrySet()) {
            builderFr.append(entry.getKey()).append(SPACE3);
            for (FileRanking map : entry.getValue().values()) {
                builderFr.append(SPACE3).append(map.toString());
            }
            builderFr.append(SLASH);
        }

        for (Map.Entry<String, TreeMap<Integer, FileRanking>> entry : indexEn.entrySet()) {
            builderEn.append(entry.getKey()).append(SPACE3);
            for (FileRanking map : entry.getValue().values()) {
                builderEn.append(SPACE3).append(map.toString());
            }
            builderEn.append(SLASH);
        }

        ar.write(builderAr.toString());
        fr.write(builderFr.toString());
        en.write(builderEn.toString());

        ar.close();
        fr.close();
        en.close();
    }

    private static void readWord() throws Exception {
        BufferedReader ar = new BufferedReader(new FileReader(root + indexWordAr));
        BufferedReader fr = new BufferedReader(new FileReader(root + indexWordFr));
        BufferedReader en = new BufferedReader(new FileReader(root + indexWordEn));

        String ligne;
        Integer id;
        while ((ligne = ar.readLine()) != null) {
            String[] elements = ligne.split(SPACE4);
            TreeMap<Integer, FileRanking> map = indexAr.getOrDefault(elements[0], new TreeMap());
            String[] files = elements[1].split(SPACE3);
            for (String file : files) {
                String[] fs = file.split(SPACE2);
                id = Integer.parseInt(fs[0]);
                FileRanking set = map.getOrDefault(id, new FileRanking(id));
                set.setRank(Integer.parseInt(fs[1]));
                set.addAll(Arrays.asList(fs[2].split(SPACE1)));
                map.put(id, set);
                indexAr.put(elements[0], map);
            }
        }
        while ((ligne = fr.readLine()) != null) {
            String[] elements = ligne.split(SPACE4);
            TreeMap<Integer, FileRanking> map = indexFr.getOrDefault(elements[0], new TreeMap());
            String[] files = elements[1].split(SPACE3);
            for (String file : files) {
                String[] fs = file.split(SPACE2);
                id = Integer.parseInt(fs[0]);
                FileRanking set = map.getOrDefault(id, new FileRanking(id));
                set.setRank(Integer.parseInt(fs[1]));
                set.addAll(Arrays.asList(fs[2].split(SPACE1)));
                map.put(id, set);
                indexFr.put(elements[0], map);
            }
        }
        while ((ligne = en.readLine()) != null) {

            String[] elements = ligne.split(SPACE4);
            TreeMap<Integer, FileRanking> map = indexEn.getOrDefault(elements[0], new TreeMap());
            String[] files = elements[1].split(SPACE3);
            for (String file : files) {
                String[] fs = file.split(SPACE2);
                id = Integer.parseInt(fs[0]);
                FileRanking set = map.getOrDefault(id, new FileRanking(id));
                set.setRank(Integer.parseInt(fs[1]));
                set.addAll(Arrays.asList(fs[2].split(SPACE1)));
                map.put(id, set);
                indexEn.put(elements[0], map);
            }
        }
        ar.close();
        fr.close();
        en.close();
    }

    private static void buildStemAndLemme() throws IOException {
        for (Map.Entry<String, TreeMap<Integer, FileRanking>> entry : indexAr.entrySet()) {
            Map<String, Set<String>> liste = Stemming.stemmer(entry.getKey());
            for (String stem : liste.get(STEM)) {
                Set<String> stems = indexArStem.getOrDefault(stem, new HashSet());
                stems.add(entry.getKey());
                indexArStem.put(stem, stems);
            }

            for (String lemme : liste.get(LEMME)) {
                Set<String> lemmes = indexArLemme.getOrDefault(lemme, new HashSet());
                lemmes.add(entry.getKey());
                indexArLemme.put(lemme, lemmes);
            }
        }

        for (Map.Entry<String, TreeMap<Integer, FileRanking>> entry : indexFr.entrySet()) {
            String stem = Stemming.stemmerFr(entry.getKey());
            Set<String> stems = indexFrStem.getOrDefault(stem, new HashSet());
            stems.add(entry.getKey());
            indexFrStem.put(stem, stems);
        }

        for (Map.Entry<String, TreeMap<Integer, FileRanking>> entry : indexEn.entrySet()) {
            String stem = Stemming.stemmerEn(entry.getKey());
            Set<String> stems = indexEnStem.getOrDefault(stem, new HashSet());
            stems.add(entry.getKey());
            indexEnStem.put(stem, stems);
        }
    }

    private static void saveStemAndLemme() throws Exception {
        BufferedWriter ar = new BufferedWriter(new FileWriter(indexStemAr));
        BufferedWriter arLemme = new BufferedWriter(new FileWriter(indexLemmeAr));
        BufferedWriter fr = new BufferedWriter(new FileWriter(indexStemFr));
        BufferedWriter en = new BufferedWriter(new FileWriter(indexStemEn));

        StringBuilder builderAr = new StringBuilder();
        StringBuilder builderArLemme = new StringBuilder();
        StringBuilder builderFr = new StringBuilder();
        StringBuilder builderEn = new StringBuilder();

        for (Map.Entry<String, Set<String>> entry : indexArStem.entrySet()) {
            builderAr.append(entry.getKey()).append(SPACE3).append(String.join(SPACE1, entry.getValue())).append(SLASH);
        }
        for (Map.Entry<String, Set<String>> entry : indexArLemme.entrySet()) {
            builderArLemme.append(entry.getKey()).append(SPACE3).append(String.join(SPACE1, entry.getValue())).append(SLASH);
        }

        for (Map.Entry<String, Set<String>> entry : indexFrStem.entrySet()) {
            builderFr.append(entry.getKey()).append(SPACE3).append(String.join(SPACE1, entry.getValue())).append(SLASH);
        }

        for (Map.Entry<String, Set<String>> entry : indexEnStem.entrySet()) {
            builderEn.append(entry.getKey()).append(SPACE3).append(String.join(SPACE1, entry.getValue())).append(SLASH);
        }

        ar.write(builderAr.toString());
        arLemme.write(builderArLemme.toString());
        fr.write(builderFr.toString());
        en.write(builderEn.toString());

        ar.close();
        arLemme.close();
        fr.close();
        en.close();
    }

    private static void readStemAndLemme() throws Exception {
        indexArStem.clear();
        indexFrStem.clear();
        indexEnStem.clear();
        BufferedReader ar = new BufferedReader(new FileReader(root + indexStemAr));
        BufferedReader arLemme = new BufferedReader(new FileReader(root + indexLemmeAr));
        BufferedReader fr = new BufferedReader(new FileReader(root + indexStemFr));
        BufferedReader en = new BufferedReader(new FileReader(root + indexStemEn));
        String ligne;

        while ((ligne = ar.readLine()) != null) {
            if (!ligne.isEmpty()) {
                String[] key = ligne.split(SPACE3);
                Set<String> stems = indexArStem.getOrDefault(key[0], new HashSet());
                stems.addAll(Arrays.asList(key[1].split(SPACE1)));
                indexArStem.put(key[0], stems);
            }
        }
        while ((ligne = arLemme.readLine()) != null) {
            if (!ligne.isEmpty()) {
                String[] key = ligne.split(SPACE3);
                Set<String> lemmes = indexArLemme.getOrDefault(key[0], new HashSet());
                lemmes.addAll(Arrays.asList(key[1].split(SPACE1)));
                indexArLemme.put(key[0], lemmes);
            }
        }
        while ((ligne = fr.readLine()) != null) {
            if (!ligne.isEmpty()) {
                String[] key = ligne.split(SPACE3);
                Set<String> stems = indexFrStem.getOrDefault(key[0], new HashSet());
                stems.addAll(Arrays.asList(key[1].split(SPACE1)));
                indexFrStem.put(key[0], stems);
            }
        }
        while ((ligne = en.readLine()) != null) {
            if (!ligne.isEmpty()) {
                String[] key = ligne.split(SPACE3);
                Set<String> stems = indexEnStem.getOrDefault(key[0], new HashSet());
                stems.addAll(Arrays.asList(key[1].split(SPACE1)));
                indexEnStem.put(key[0], stems);
            }
        }

        ar.close();
        arLemme.close();
        fr.close();
        en.close();
    }

    private static void readDictionary() throws Exception {
        BufferedReader arFr = new BufferedReader(new FileReader(root + dictionaryARFR));
        BufferedReader arEn = new BufferedReader(new FileReader(root + dictionaryAREN));
        BufferedReader frAr = new BufferedReader(new FileReader(root + dictionaryFRAR));
        BufferedReader enAr = new BufferedReader(new FileReader(root + dictionaryENAR));
        String ligne;

        while ((ligne = arFr.readLine()) != null) {
            if (!ligne.isEmpty()) {
                String[] elements = ligne.split(SPACE1);
                String key = ArabicNormalizer.cleanString(elements[1]);
                Set<Dist> dists = dictionaryArToFr.getOrDefault(key, new HashSet());
                dists.add(new Dist(key, elements[2], ArabicNormalizer.cleanString(elements[3]),
                        ArabicNormalizer.cleanString(elements[4]), FrenchNormalizer.normalizeSemicolon(elements[5])));
                dictionaryArToFr.put(key, dists);
            }
        }
        while ((ligne = arEn.readLine()) != null) {
            if (!ligne.isEmpty()) {
                String[] elements = ligne.split(SPACE1);
                String key = ArabicNormalizer.cleanString(elements[1]);
                Set<Dist> dists = dictionaryArToEn.getOrDefault(key, new HashSet());
                dists.add(new Dist(key, elements[2], ArabicNormalizer.cleanString(elements[3]),
                        ArabicNormalizer.cleanString(elements[4]), EnglishNormalizer.normalizeSemicolon(elements[5])));
                dictionaryArToEn.put(key, dists);
            }
        }
        while ((ligne = frAr.readLine()) != null) {
            if (!ligne.isEmpty()) {
                String[] elements = ligne.split(SPACE1);
                String key = FrenchNormalizer.cleanString(elements[1]);
                Set<Dist> dists = dictionaryFrToAr.getOrDefault(key, new HashSet());
                dists.add(new Dist(key, elements[2], FrenchNormalizer.cleanString(elements[3]),
                        FrenchNormalizer.cleanString(elements[4]), ArabicNormalizer.normalizeSemicolon(elements[5])));
                dictionaryFrToAr.put(key, dists);
            }
        }
        while ((ligne = enAr.readLine()) != null) {
            if (!ligne.isEmpty()) {
                String[] elements = ligne.split(SPACE1);
                String key = EnglishNormalizer.cleanString(elements[1]);
                Set<Dist> dists = dictionaryEnToAr.getOrDefault(key, new HashSet());
                dists.add(new Dist(key, elements[2], EnglishNormalizer.cleanString(elements[3]),
                        EnglishNormalizer.cleanString(elements[4]), ArabicNormalizer.normalizeSemicolon(elements[5])));
                dictionaryEnToAr.put(key, dists);
            }
        }
        arFr.close();
        arEn.close();
        frAr.close();
        enAr.close();
    }

    public static void get(String word, String l1, String l2, String modeSearch) {
        Resultat search = search(word, l1, l2, modeSearch);
        List<String> files = search.getFiles();
        System.out.println("Words : " + search.getSearchWords().size() + " : " + search.getSearchWords());
        TreeMap<String, Integer> filesNameAndScore = search.getFilesNameAndScore();
        TreeMap<String, TreeMap<String, List<FileRanking>>> resultatByFiles = search.getResultatByFiles();
        TreeMap<String, TreeMap<Integer, FileRanking>> resultatByWords = search.getResultatByWords();
        Set<String> searchWords = search.getSearchWords();
        System.out.println();
        System.out.println();
        System.out.println("Files : " + resultatByWords);
        System.out.println();
        System.out.println();
        for (String file : files) {
            System.out.println("File : " + file + " , Score : " + filesNameAndScore.get(file));
            for (Map.Entry<String, List<FileRanking>> entry : resultatByFiles.get(file).entrySet()) {
                String key = entry.getKey();
                List<FileRanking> value = entry.getValue();
                System.out.println("Word : " + entry.getKey() + " , Position : " + value);//.getIds() + " , Rank : " + value.getRank());
            }
        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        double start = System.currentTimeMillis();
        /*get("designer machine", "en", "ar", "word");
        get("designer machine", "en", "fr", "word");
        get("designer machine", "en", "en", "word");
        get("architecture", "fr", "ar", "word");
        get("architecture", "fr", "fr", "word");
        get("architecture", "fr", "en", "word");
        get("فالمهندسون", "ar", "ar", "word");
        get("فالمهندسون", "ar", "fr", "word");
        get("فالمهندسون", "ar", "en", "word");
        get("مهندس", "ar", "ar", "word");
        get("مهندس", "ar", "fr", "word");
        get("مهندس", "ar", "en", "word");
        get("designer", "en", "ar", "word");
        get("designer", "en", "fr", "word");
        get("designer", "en", "en", "word");*/
        get("exemple", "fr", "ar", "word");

        System.out.println(/*"size Ar -> Fr : " + dictionaryArToFr.size() + SLASH
                + "size Ar -> En : " + dictionaryArToEn.size() + SLASH
                + "size Fr -> Ar : " + dictionaryFrToAr.size() + SLASH
                + "size En -> Ar : " + dictionaryEnToAr.size() + SLASH + SLASH
                + "size Ar -> Ar : " + search("فالمهندسون", "ar", "ar") + SLASH + SLASH
                + "size Ar -> Fr : " + search("مهندس", "ar", "fr") + SLASH + SLASH
                + "size Ar -> En : " + search("مهندس", "ar", "en") + SLASH + SLASH
                + "size Fr -> Ar : " + search("architecture", "fr", "ar") + SLASH + SLASH
                + "size Fr -> Fr : " + search("architecture", "fr", "fr") + SLASH + SLASH
                + "size Fr -> En : " + search("architecture", "fr", "en") + SLASH + SLASH
                + "size En -> Ar : " + search("designer", "en", "ar") + SLASH + SLASH
                + "size En -> En : " + search("designer", "en", "en") + SLASH + SLASH
                + "size En -> Fr : " + search("designer", "en", "fr") + SLASH + SLASH
                + "size En -> Ar : " + search("designer machine", "en", "ar", "all") + SLASH + SLASH
                + "size stem : " + Stemming.stemmerEn("designers") + SLASH + SLASH
                + */"fin           : " + (System.currentTimeMillis() - start));
        /*
        SortedMap<String, Set<String>> map1 = new TreeMap();
        NavigableMap<String, String> mapHttpStatus = new TreeMap<>();
        mapHttpStatus.put("a", TALK);
        mapHttpStatus.put("aa", TALK);
        mapHttpStatus.put("aaa", TALK);
        mapHttpStatus.put("aaaa", TALK);
        mapHttpStatus.put("aaaaa", TALK);
        mapHttpStatus.put("aaaaaa", TALK);
        mapHttpStatus.put("aaaaaaa", TALK);
        mapHttpStatus.put("aaaaaaaa", TALK);
        mapHttpStatus.put("aaaaab", TALK);
        mapHttpStatus.put("b", TALK);
        mapHttpStatus.put("bb", TALK);
        mapHttpStatus.put("bbb", TALK);
        mapHttpStatus.put("bbbb", TALK);
        mapHttpStatus.put("bbbbb", TALK);
        mapHttpStatus.put("bbbbbb", TALK);
        mapHttpStatus.put("bbbbbbb", TALK);
        mapHttpStatus.put("bbbbbbbb", TALK);

        System.err.println(mapHttpStatus.lowerKey("a"));
        System.err.println(mapHttpStatus.floorKey("aa"));
        System.err.println(mapHttpStatus.subMap("aa", "aa" + MAX_VALUE));
        System.err.println(mapHttpStatus.higherKey("b"));
        System.err.println(mapHttpStatus.ceilingKey("aa"));
        System.err.println("fin           : " + (System.currentTimeMillis() - start));*/
    }

    private static List<String> getAllTokensAr(String word) {
        word = ArabicNormalizer.normalizeString(word);
        AlKhalil2.text.tokenization.Tokenization text = new AlKhalil2.text.tokenization.Tokenization();
        text.setTokenizationString(word);
        return text.getAllTokens();
    }

    private static List<String> getAllTokensFr(String word) {
        return Arrays.asList(FrenchNormalizer.normalizeString(word).split(SPACE));
    }

    private static List<String> getAllTokensEn(String word) {
        return Arrays.asList(EnglishNormalizer.normalizeString(word).split(SPACE));
    }

    public static Resultat search(String words, String languageSource, String languageTarget, String modeSearch) {
        TreeMap<String, TreeMap<Integer, FileRanking>> map = new TreeMap();
        if (words != null && !words.isEmpty()) {
            List<String> tokens = new ArrayList();
            switch (languageSource) {
                case AR:
                    tokens = getAllTokensAr(words);
                    break;
                case FR:
                    tokens = getAllTokensFr(words);
                    break;
                case EN:
                    tokens = getAllTokensEn(words);
                    break;
                default:
                    break;
            }
            for (String token : tokens) {
                map.putAll(searchOriginal(token, languageSource, languageTarget, modeSearch));
            }
        }
        return new Resultat(map);
    }

    private static SortedMap<String, TreeMap<Integer, FileRanking>> searchOriginal(String word, String l1, String l2, String modeSearch) {
        final SortedMap<String, TreeMap<Integer, FileRanking>> map = new TreeMap();
        Set<Dist> dists;
        modeSearch = modeSearch.toLowerCase().trim();
        final String mode = (modeSearch.equals(STEM) || modeSearch.equals(LEMME) || modeSearch.equals(ALL)) ? modeSearch : ALL;
        switch (l1.toLowerCase().trim() + '-' + l2.toLowerCase().trim()) {
            case "ar-ar":
                word = ArabicNormalizer.cleanString(word);
                if ((mode.equals(WORD) || mode.equals(ALL)) && indexAr.containsKey(word)) {
                    map.putAll(indexAr.subMap(word, word + MAX_VALUE));
                }
                if ((mode.equals(STEM) || mode.equals(ALL)) && indexArStem.containsKey(word)) {
                    for (String e : indexArStem.get(word)) {
                        map.putAll(indexAr.subMap(e, e + MAX_VALUE));
                    }
                }
                if ((mode.equals(LEMME) || mode.equals(ALL)) && indexArLemme.containsKey(word)) {
                    for (String e : indexArLemme.get(word)) {
                        map.putAll(indexAr.subMap(e, e + MAX_VALUE));
                    }
                }
                break;
            case "ar-fr":
                word = ArabicNormalizer.cleanString(word);
                dists = dictionaryArToFr.get(word);
                if (dists != null && !dists.isEmpty()) {
                    Set<String> elements = new HashSet();
                    dists.forEach((dist) -> {
                        elements.addAll(dist.getMeaning());
                    });
                    Set<String> elementsAr = new HashSet();
                    for (String element : elements) {
                        if ((!stopWordFr.contains(element))) {
                            elementsAr.add(element);
                            Set<Dist> distAr = dictionaryFrToAr.get(element);
                            /*if (distAr != null) {
                                distAr.forEach((dist) -> {
                                    elementsAr.addAll(dist.getSearchWordList());
                                });
                            }*/
                        }
                    }
                    for (String elementAr : elementsAr) {
                        if ((mode.equals(WORD) || mode.equals(ALL)) && indexFr.containsKey(elementAr)) {
                            map.putAll(indexFr.subMap(elementAr, elementAr + MAX_VALUE));
                        }
                        if ((mode.equals(STEM) || mode.equals(ALL)) && indexFrStem.containsKey(elementAr)) {
                            indexFrStem.get(elementAr).forEach((e) -> {
                                map.putAll(indexFr.subMap(e, e + MAX_VALUE));
                            });
                        }
                    }
                }
                break;
            case "ar-en":
                word = ArabicNormalizer.cleanString(word);
                dists = dictionaryArToEn.get(word);
                if (dists != null && !dists.isEmpty()) {
                    Set<String> elements = new HashSet();
                    dists.forEach((dist) -> {
                        elements.addAll(dist.getMeaning());
                    });
                    Set<String> elementsAr = new HashSet();
                    elements.stream().map((element) -> {
                        if ((!stopWordEn.contains(element))) {
                            elementsAr.add(element);
                            Set<Dist> distAr = dictionaryEnToAr.get(element);
                            /*if (distAr != null) {
                                distAr.forEach((dist) -> {
                                    elementsAr.addAll(dist.getSearchWordList());
                                });
                            }*/
                        }
                        return element;
                    }).forEachOrdered((_item) -> {
                        elementsAr.stream().map((elementAr) -> {
                            if ((mode.equals(WORD) || mode.equals(ALL)) && indexEn.containsKey(elementAr)) {
                                map.putAll(indexEn.subMap(elementAr, elementAr + MAX_VALUE));
                            }
                            return elementAr;
                        }).filter((elementAr) -> ((mode.equals(STEM) || mode.equals(ALL)) && indexEnStem.containsKey(elementAr))).forEachOrdered((elementAr) -> {
                            indexEnStem.get(elementAr).forEach((e) -> {
                                map.putAll(indexEn.subMap(e, e + MAX_VALUE));
                            });
                        });
                    });
                }
                break;
            case "fr-ar":
                word = FrenchNormalizer.cleanString(word);
                dists = dictionaryFrToAr.get(word);
                if (dists != null && !dists.isEmpty()) {
                    Set<String> elements = new HashSet();
                    dists.forEach((dist) -> {
                        elements.addAll(dist.getMeaning());
                    });
                    elements.stream().filter((element) -> (!stopWordAr.contains(element))).forEachOrdered((element) -> {
                        Set<String> elementsAr = new HashSet();
                        elementsAr.add(element);
                        Set<Dist> distAr = dictionaryArToFr.get(element);
                        elementsAr.stream().map((elementAr) -> {
                            if ((mode.equals(WORD) || mode.equals(ALL)) && indexAr.containsKey(elementAr)) {
                                map.putAll(indexAr.subMap(elementAr, elementAr + MAX_VALUE));
                            }
                            if ((mode.equals(STEM) || mode.equals(ALL)) && indexArStem.containsKey(elementAr)) {
                                indexArStem.get(elementAr).forEach((e) -> {
                                    map.putAll(indexAr.subMap(e, e + MAX_VALUE));
                                });
                            }
                            return elementAr;
                        }).filter((elementAr) -> ((mode.equals(LEMME) || mode.equals(ALL)) && indexArLemme.containsKey(elementAr))).forEachOrdered((String elementAr) -> {
                            indexArLemme.get(elementAr).forEach((e) -> {
                                map.putAll(indexAr.subMap(e, e + MAX_VALUE));
                            });
                        });
                    });
                }
                break;
            case "fr-fr":
                word = FrenchNormalizer.cleanString(word);
                if ((mode.equals(WORD) || mode.equals(ALL)) && indexFr.containsKey(word)) {
                    map.putAll(indexFr.subMap(word, word + MAX_VALUE));
                }
                if ((mode.equals(STEM) || mode.equals(ALL)) && indexFrStem.containsKey(word)) {
                    indexFrStem.get(word).forEach((e) -> {
                        map.putAll(indexFr.subMap(e, e + MAX_VALUE));
                    });
                }
                break;
            case "fr-en":
                word = FrenchNormalizer.cleanString(word);
                dists = dictionaryFrToAr.get(word);
                if (dists != null && !dists.isEmpty()) {
                    Set<String> elements = new HashSet();
                    dists.forEach((dist) -> {
                        elements.addAll(dist.getMeaning());
                    });
                    Set<String> elementsEn = new HashSet();
                    for (String element : elements) {
                        if (!stopWordAr.contains(element)) {
                            dists = dictionaryArToEn.get(element);
                            /*if (dists != null && !dists.isEmpty()) {
                                dists.forEach((dist) -> {
                                    elementsEn.addAll(dist.getMeaning());
                                });
                            }*/
                        }
                    }
                    for (String elementEn : elementsEn) {
                        if ((mode.equals(WORD) || mode.equals(ALL)) && indexEn.containsKey(elementEn)) {
                            map.putAll(indexEn.subMap(elementEn, elementEn + MAX_VALUE));
                        }
                        if ((mode.equals(STEM) || mode.equals(ALL)) && indexEnStem.containsKey(elementEn)) {
                            indexEnStem.get(elementEn).forEach((e) -> {
                                map.putAll(indexEn.subMap(e, e + MAX_VALUE));
                            });
                        }
                    }
                }
                break;
            case "en-ar":
                word = EnglishNormalizer.cleanString(word);
                dists = dictionaryEnToAr.get(word);
                if (dists != null && !dists.isEmpty()) {
                    Set<String> elements = new HashSet();
                    dists.forEach((dist) -> {
                        elements.addAll(dist.getMeaning());
                    });
                    elements.stream().filter((element) -> (!stopWordAr.contains(element))).forEachOrdered((element) -> {
                        Set<String> elementsAr = new HashSet();
                        elementsAr.add(element);
                        Set<Dist> distAr = dictionaryArToEn.get(element);
                        elementsAr.stream().map((elementAr) -> {
                            if ((mode.equals(WORD) || mode.equals(ALL)) && indexAr.containsKey(elementAr)) {
                                map.putAll(indexAr.subMap(elementAr, elementAr + MAX_VALUE));
                            }
                            if ((mode.equals(STEM) || mode.equals(ALL)) && indexArStem.containsKey(elementAr)) {
                                indexArStem.get(elementAr).forEach((e) -> {
                                    map.putAll(indexAr.subMap(e, e + MAX_VALUE));
                                });
                            }
                            return elementAr;
                        }).filter((elementAr) -> ((mode.equals(LEMME) || mode.equals(ALL)) && indexArLemme.containsKey(elementAr))).forEachOrdered((elementAr) -> {
                            indexArLemme.get(elementAr).forEach((e) -> {
                                map.putAll(indexAr.subMap(e, e + MAX_VALUE));
                            });
                        });
                    });
                }
                break;
            case "en-fr":
                word = EnglishNormalizer.cleanString(word);
                dists = dictionaryEnToAr.get(word);
                if (dists != null && !dists.isEmpty()) {
                    Set<String> elements = new HashSet();
                    dists.forEach((dist) -> {
                        elements.addAll(dist.getMeaning());
                    });
                    final Set<String> elementsFr = new HashSet();
                    for (String element : elements) {
                        if (!stopWordAr.contains(element)) {
                            dists = dictionaryArToFr.get(element);
                            /*if (dists != null && !dists.isEmpty()) {
                                dists.forEach((dist) -> {
                                    elementsFr.addAll(dist.getMeaning());
                                });
                            }*/
                        }
                    }
                    for (String elementFr : elementsFr) {
                        if ((mode.equals(WORD) || mode.equals(ALL)) && indexFr.containsKey(elementFr)) {
                            map.putAll(indexFr.subMap(elementFr, elementFr + MAX_VALUE));
                        }
                        if ((mode.equals(STEM) || mode.equals(ALL)) && indexFrStem.containsKey(elementFr)) {
                            indexFrStem.get(elementFr).forEach((e) -> {
                                map.putAll(indexFr.subMap(e, e + MAX_VALUE));
                            });
                        }
                    }
                }
                break;
            case "en-en":
                word = EnglishNormalizer.cleanString(word);
                if ((mode.equals(WORD) || mode.equals(ALL)) && indexEn.containsKey(word)) {
                    map.putAll(indexEn.subMap(word, word + MAX_VALUE));
                }
                if ((mode.equals(STEM) || mode.equals(ALL)) && indexEnStem.containsKey(word)) {
                    indexEnStem.get(word).forEach((e) -> {
                        map.putAll(indexEn.subMap(e, e + MAX_VALUE));
                    });
                }
                break;
        }
        return map;
    }
}
