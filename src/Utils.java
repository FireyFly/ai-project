import java.util.*;
import java.util.stream.*;

public class Utils {
    //-- Mapping functions ------------------------------------------
    public static interface Mapper<F,T> {
        public T mapOne(F elem);
    }

    /** Maps a function over a list. */
    public static <F,T> List<T> map(List<F> list, Mapper<F,T> mapper) {
        List<T> newList = new ArrayList<>();
        for (F elem : list) {
            newList.add(mapper.mapOne(elem));
        }
        return newList;
    }

    public static class MappedIterator<F,T> implements Iterator<T> {
        private Iterator<F> it;
        private Mapper<F,T> mapper;

        public MappedIterator(Iterator<F> it, Mapper<F,T> mapper) {
            this.it = it;
            this.mapper = mapper;
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public T next() {
            return this.mapper.mapOne(it.next());
        }

        public void remove() {
            it.remove();
        }
    }

    /** Maps a function over elements of an iterator. */
    public static <F,T> Iterator<T> mapIterator(Iterator<F> it, Mapper<F,T> mapper) {
        return new MappedIterator<F,T>(it, mapper);
    }


    //-- Tuples -----------------------------------------------------
    /** Represents a 2-tuple. */
    public static class Pair<A,B> {
        public A e1;
        public B e2;

        public Pair(A e1, B e2) {
            this.e1 = e1;
            this.e2 = e2;
        }

        public String toString() {
            return "(" + this.e1 + ", " + this.e2 + ")";
        }
    }

    public static <A,B> Pair<A,B> pair(A fst, B snd) {
        return new Pair<>(fst, snd);
    }

    /** Represents a 3-tuple. */
    public static class Triplet<A,B,C> {
        public A e1;
        public B e2;
        public C e3;

        public Triplet(A e1, B e2, C e3) {
            this.e1 = e1;
            this.e2 = e2;
            this.e3 = e3;
        }

        public String toString() {
            return "(" + this.e1 + ", " + this.e2 + ", " + this.e3 + ")";
        }
    }

    public static <A,B,C> Triplet<A,B,C> triplet(A e1, B e2, C e3) {
      return new Triplet<>(e1, e2, e3);
    }


    //-- List utilities ---------------------------------------------
    /** Extracts the last `n` elements of a list. */
    public static <T> List<T> lastN(List<T> list, int n) {
        List<T> res = new ArrayList<>();
        if (list.size() < n) {
            throw new IllegalArgumentException("Fewer than " + n + " values in list");
        }

        for (int i = 0; i < n; i++) {
            res.add(list.get(list.size() - n + i));
        }
        return res;
    }

    /** Collects a stream into a list. */
    public static <T> ArrayList<T> toList(Stream<T> stream) {
      return stream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
