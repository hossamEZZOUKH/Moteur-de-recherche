package m2i.mr.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import m2i.mr.indexation.Index;

/**
 *
 * @author imac
 */
public class Resultat {

    // files : list of FileId
    private final List<Integer> files = new ArrayList();
    // filesContent : key = FileId , value = list of Word
    private final TreeMap<Integer, Set<String>> filesContent = new TreeMap();
    // filesContent : key = FileId , value = Score
    private final TreeMap<Integer, Integer> filesScore = new TreeMap();
    // filesContent : key = Filea Name , value = Score
    private final TreeMap<String, Integer> filesNameScore = new TreeMap();
    // filesContent : key = Score , value = list of FileId
    private final TreeMap<Integer, List<Integer>> ScoreFiles = new TreeMap();
    // values : key = Word , value = Map(key = FileId , value = FileRanking(Objet contents : List of Position Words, Id File and Score of File))
    private final TreeMap<String, TreeMap<Integer, FileRanking>> values;

    public Resultat(TreeMap<String, TreeMap<Integer, FileRanking>> values) {
        this.values = values;
        this.values.entrySet().forEach((e) -> {
            e.getValue().entrySet().forEach((e1) -> {
                Set<String> labels = filesContent.getOrDefault(e1.getKey(), new HashSet());
                labels.add(e.getKey());
                filesContent.put(e1.getKey(), labels);
                Integer v = filesScore.getOrDefault(e1.getKey(), 0);
                v = v + e1.getValue().getRank();
                filesScore.put(e1.getKey(), v);
                filesNameScore.put(Index.indexFiles.get(e1.getKey()), v);
            });
        });
        filesScore.entrySet().forEach((e) -> {
            final List<Integer> labels = ScoreFiles.getOrDefault(e.getValue(), new ArrayList());
            labels.add(e.getKey());
            ScoreFiles.put(e.getValue(), labels);
        });
        ScoreFiles.descendingMap().entrySet().forEach((listFileRanking) -> {
            files.addAll(listFileRanking.getValue());
        });
    }

    public final List<String> getFiles() {
        List<String> fs = new ArrayList();
        files.forEach((id) -> {
            fs.add(Index.indexFiles.get(id));
        });
        return fs;
    }

    public final String getFile(Integer id) {
        return Index.indexFiles.get(id);
    }

    public final Integer getFileId(String file) {
        return Index.index.get(file);
    }

    public Set<String> getSearchWords() {
        return values.keySet();
    }

    public Set<String> getSearchWordsInFile(String file) {
        return filesContent.get(Index.index.get(file));
    }

    public Set<String> getSearchWordsInFile(Integer id) {
        return filesContent.get(id);
    }

    public TreeMap<String, TreeMap<Integer, FileRanking>> getResultatByWords() {
        return values;
    }

    public TreeMap<String, TreeMap<String, List<FileRanking>>> getResultatByFiles() {
        TreeMap<String, TreeMap<String, List<FileRanking>>> map = new TreeMap();
        /*for (Map.Entry<String, TreeMap<Integer, FileRanking>> entry : values.entrySet()) {
            String word = entry.getKey();
            for (Map.Entry<Integer, FileRanking> entry1 : entry.getValue().entrySet()) {
                Integer keyFile = entry1.getKey();
                FileRanking valueFile = entry1.getValue();
                
                System.out.println("Word : " + word + " , File : " + Index.indexFiles.get(keyFile) + " , Position : " + valueFile.getIds() + " , Rank : " + valueFile.getRank());
                //System.out.println("Word : " + word + " , Position : " + value.getIds() + " , Rank : " + value.getRank());
            }
        }*/
        values.entrySet().forEach((entry) -> {
            entry.getValue().entrySet().forEach((file) -> {
                TreeMap<String, List<FileRanking>> orDefault = map.getOrDefault(Index.indexFiles.get(file.getKey()), new TreeMap());
                List<FileRanking> e = orDefault.getOrDefault(entry.getKey(), new ArrayList());
                e.add(file.getValue());
                orDefault.put(entry.getKey(), e);
                map.put(Index.indexFiles.get(file.getKey()), orDefault);
            });
        });
        return map;
    }

    public TreeMap<String, FileRanking> getResultatByFile(String file) {
        TreeMap<String, FileRanking> map = new TreeMap();
        values.entrySet().forEach((entry) -> {
            Integer id = getFileId(file);
            if (entry.getValue().containsKey(id)) {
                map.put(entry.getKey(), entry.getValue().get(id));
            }
        });
        return map;
    }

    public TreeMap<String, Integer> getFilesNameAndScore() {
        return filesNameScore;
    }
}
