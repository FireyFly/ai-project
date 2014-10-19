import java.util.*;

/** A model for text prediction. */
public interface Model {

  /** Given N preceding words of context, predict the next word.  Returns a
   *  list of predictions ordered by likelihood. */
  public List<Markov.Symbol<String>> predictNext(List<String> context);


  /** A Markov chain model on only the preceding words and the likelihood of
   *  the next word.  That is, a n-gram model on words (as strings). */
  public static class SimpleModel implements Model {
    private Markov<String> wordChain;

    public SimpleModel(Markov<String> wordChain) {
      this.wordChain = wordChain;
    }

    public List<Markov.Symbol<String>> predictNext(List<String> context) {
      List<Markov.Symbol<String>> symbols =
          Utils.map(context, word -> new Markov.Symbol(word));
      FrequencyList<Markov.Symbol<String>> freq = wordChain.getNexts(symbols);

      PriorityQueue<Utils.Pair<Markov.Symbol<String>, Double>> queue =
          new PriorityQueue<>(10, (x, y) -> Double.compare(x.snd, y.snd));

      for (Utils.Pair<Markov.Symbol<String>, Double> pair : freq) {
        queue.add(pair);
      }

      return Utils.map(new ArrayList<>(queue), pair -> pair.fst);
    }
  }
}
