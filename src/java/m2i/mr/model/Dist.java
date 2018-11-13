package m2i.mr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author imac
 */
public class Dist {

    private String word;
    private String explination;
    private String searchWord;
    private String root;
    private String meaning;

    public Dist(String word, String explination, String searchWord, String root, String meaning) {
        this.word = word;
        this.explination = explination;
        this.searchWord = searchWord;
        this.root = root.trim();
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public String getExplination() {
        return explination;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public Set<String> getSearchWordList() {
        return new HashSet(Arrays.asList(searchWord.split(" ")));
    }

    public String getRoot() {
        return root;
    }

    public Set<String> getMeaning() {
        return new HashSet(Arrays.asList(meaning.split(";")));
    }
}
