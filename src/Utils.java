import java.util.*;

public class Utils {
    public static interface Mapper<F,T> {
        public T mapOne(F elem);
    }

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

    public static <F,T> Iterator<T> mapIterator(Iterator<F> it, Mapper<F,T> mapper) {
        return new MappedIterator<F,T>(it, mapper);
    }

    public static class Pair<A,B> {
        public A fst;
        public B snd;

        public Pair(A fst, B snd) {
            this.fst = fst;
            this.snd = snd;
        }
    }
}
