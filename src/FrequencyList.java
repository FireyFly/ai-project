import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * Represents a frequency distribution of T's by mapping T's to their number of
 * occurrences.
 */
public class FrequencyList<T> implements Serializable {

    private int totalCount = 0;
    private Map<T, Integer> counts = new HashMap<>();

    public void add(T t) {
        this.totalCount++;
        Integer count = this.counts.getOrDefault(t, 0);
        this.counts.put(t, count + 1);
    }

    public double get(T t) {
        if (totalCount == 0) {
            return Double.NaN;
        }
        Integer count = this.counts.getOrDefault(t, 0);
        return (double) count / this.totalCount;
    }

    public Map<T, Double> getProbabilityMap() {
        return new AbstractMap<T, Double>() {

            @Override
            public Double get(Object key) {
                if (totalCount == 0) {
                    return Double.NaN;
                }
                double d = counts.getOrDefault(key, 0);
                return d / totalCount;
            }

            @Override
            public Set<Map.Entry<T, Double>> entrySet() {
                return new AbstractSet<Map.Entry<T, Double>>() {

                    @Override
                    public Iterator<Map.Entry<T, Double>> iterator() {

                        return new Iterator<Entry<T, Double>>() {
                            Iterator<Map.Entry<T, Integer>> iter1 = counts.entrySet().iterator();

                            @Override
                            public boolean hasNext() {
                                return iter1.hasNext();
                            }

                            @Override
                            public Map.Entry<T, Double> next() {
                                Entry<T, Integer> entry1 = iter1.next();
                                int i = entry1.getValue();
                                double r;
                                if(totalCount == 0){
                                    r = Double.NaN;
                                } else {
                                    r = ((double) i) / totalCount;
                                }
                                return new SimpleImmutableEntry<>(entry1.getKey(), r);
                            }
                        };

                    }

                    @Override
                    public int size() {
                        return counts.size();
                    }

                };
            }
        };

    }

    public Stream<Utils.Pair<T, Double>> stream() {
        return counts.entrySet().stream()
                .map(entry -> new Utils.Pair<>(entry.getKey(),
                                (double) entry.getValue() / totalCount));
    }

    public ArrayList<Utils.Pair<T, Double>> toList() {
      return this.stream()
                 .sorted((x,y) -> Double.compare(y.snd, x.snd))
                 .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
