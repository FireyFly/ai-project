import java.io.*;
import java.util.*;

public class Markov<T> implements Serializable {
  private Map<List<Symbol<T>>, WeightedList<T>> map = new HashMap<>();
  private int n;

  public final Symbol<T> START = new Symbol.Special<>("START", 1),
                         END   = new Symbol.Special<>("END", 2);

  public Markov(int n) {
    this.n = n;
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
        map.put(ngram, new WeightedList<T>());
      }

      map.get(ngram).add(current);
    }
  }

  /** Choose a (weighted) random next node given an n-gram.
    * Returns `null` if the n-gram isn't found in the chain.
    */
  public Symbol<T> randomNext(List<Symbol<T>> ngram) {
    if (!map.containsKey(ngram)) return null;
    return map.get(ngram).weightedRandomNext();
  }

  /** Represents a single symbol (i.e. node) in a Markov chain. */
  public static class Symbol<T> implements Serializable {
    private final T value;

    private Symbol(T value) {
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

      public boolean equals(Object other) {
        return this == other;
      }
    }
  }

  private static class WeightedList<T> implements Serializable {
    private Map<Symbol<T>, Counter> weights = new HashMap<>();
    private int size = 0;

    private static Random rand = new Random();

    private void add(Symbol<T> symbol) {
      if (!weights.containsKey(symbol)) weights.put(symbol, new Counter());
      weights.get(symbol).increment();
      size++;
    }

    private int getSize() {
      return size;
    }

    private int getWeight(Symbol<T> symbol) {
      return weights.containsKey(symbol)? weights.get(symbol).n : 0;
    }

    private double getProbability(Symbol<T> symbol) {
      if (size == 0) return 0;
      return (double) this.getWeight(symbol) / (double) size;
    }

    private Symbol<T> weightedRandomNext() {
      // TODO: use more cleverness?
      Iterator<Map.Entry<Symbol<T>, Counter>> it = weights.entrySet().iterator();
      Map.Entry<Symbol<T>, Counter> entry = null;

      for (int v = rand.nextInt(this.size); v >= 0; v -= entry.getValue().n) {
        // Guaranteed at least one iteration
        entry = it.next();
      }

      return entry.getKey();
    }

    private class Counter implements Serializable {
      public int n = 0;

      public void increment() {
        this.n++;
      }
    }
  }
}
