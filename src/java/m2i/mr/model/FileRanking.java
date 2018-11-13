package m2i.mr.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static m2i.mr.indexation.Index.SPACE1;
import static m2i.mr.indexation.Index.SPACE2;

/**
 *
 * @author imac
 */
public class FileRanking {

    private Integer id;
    private Integer rank = 0;
    private Set<String> ids = new HashSet();

    public FileRanking(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Set<String> getIds() {
        return ids;
    }

    public void setIds(Set<String> ids) {
        this.ids = ids;
    }

    public void addAll(List<String> ids) {
        this.ids.addAll(ids);
    }

    public void add(String id) {
        this.rank++;
        if(!ids.contains(id)) {
            ids.add(id);
        }
    }

    public void add() {
        this.rank = this.rank + 7;
        ids.add("0");
    }

    @Override
    public String toString() {
        return id + SPACE2 + rank + SPACE2 + String.join(SPACE1, Arrays.copyOf(this.ids.toArray(), this.ids.toArray().length, String[].class));
    }
}
