import java.io.*;
import java.util.*;

/** Represents a frequency distribution of T's by mapping T's to their number
 *  of occurrences. */
public class FrequencyList<T> implements Serializable {
    private int totalCount = 0;
    private Map<T, Integer> counts = new HashMap<>();

    public void add(T t) {
        this.totalCount++;
        Integer count = this.counts.get(t);
        this.counts.put(t, count == null ? 1 : count + 1);
    }

    public double get(T t) {
        Integer count = this.counts.get(t);
        return count == null? 0 : (double) count / this.totalCount;
    }
}
