import java.io.*;
import java.util.*;

public class Markov<T> implements Serializable {
  private Map<List<Symbol<T>>, FrequencyList<Symbol<T>>> map = new HashMap<>();
  private int n;

  public final Symbol<T> START = new Symbol.Special<>("START", 1),
                         END   = new Symbol.Special<>("END",   2);

  public Markov(int n) {
    this.n = n;
  }

  /** Returns the `n` of this Markov model. */
  public int getN() {
    return this.n;
  }

  /** Feed the given text to the Markov model, altering the probabilities with
   *  new information from `input`.
   */
  public void feed(List<T> input) {
    List<Symbol<T>> symbols = new ArrayList<>();

    for (int i = 0; i < n; i++) symbols.add(START);
    for (T t : input) symbols.add(new Symbol<>(t));
    symbols.add(END);

    for (int i = n; i < symbols.size(); i++) {
      List<Symbol<T>> ngram = new ArrayList<>(symbols.subList(i - n, i));
      Symbol<T> current = symbols.get(i);

      if (!map.containsKey(ngram)) {
        map.put(ngram, new FrequencyList<Symbol<T>>());
      }

      map.get(ngram).add(current);
    }
  }

  public FrequencyList<Symbol<T>> getNexts(List<Symbol<T>> ngram) {
    return map.containsKey(ngram)? map.get(ngram) : new FrequencyList<>();
  }

  /** Represents a single symbol (i.e. node) in a Markov chain. */
  public static class Symbol<T> implements Serializable {
    private final T value;

    public Symbol(T value) {
      this.value = value;
    }

    public int hashCode() {
      return value.hashCode();
    }

    public boolean equals(Object other) {
      if (other == null) return false;
      if (!(other instanceof Symbol)) return false;
      Symbol<T> other_ = (Symbol<T>) other;
      return value.equals(other_.value);
    }

    public String toString() {
      return "sym(" + this.value + ")";
    }

    public T getValue() {
      return this.value;
    }

    public boolean isSpecial() {
      return false;
    }

    private static class Special<T> extends Symbol<T> {
      private final String name;
      private final int hashCode;

      public Special(String name, int hashCode) {
        super(null);
        this.name = name;
        this.hashCode = hashCode;
      }

      public String toString() {
        return this.name;
      }

      public int hashCode() {
        return this.hashCode;
      }

      public boolean isSpecial() {
        return true;
      }

      public boolean equals(Object other) {
        return this == other;
      }
    }
  }
}
