import java.util.*;
import java.util.stream.*;

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

      return wordChain.getNexts(Utils.lastN(symbols, wordChain.getN()))
                      .stream()
                      .sorted((x,y) -> Double.compare(x.snd, y.snd))
                      .map(x -> x.fst)
                      .collect(ArrayList::new, ArrayList::add,
                               ArrayList::addAll);
    }
  }

  /** A Markov chain model making use of a n-gram model on the part-of-speech
   *  to model grammar, in addition to the n-gram model on words.  The
   *  predicted words are weighted by the likelihood of the corresponding
   *  part-of-speech. */
  public static class GrammarModel implements Model {
    private Markov<String> wordChain,
                           posChain;
    private Map<String, FrequencyList<String>> posMap;

    public GrammarModel(Markov<String> wordChain, Markov<String> posChain,
                        Map<String, FrequencyList<String>> posMap) {
      this.wordChain = wordChain;
      this.posChain = posChain;
      this.posMap = posMap;
    }

    public List<Markov.Symbol<String>> predictNext(List<String> context) {
      List<Markov.Symbol<String>> symbols =
          Utils.map(context, word -> new Markov.Symbol(word));

      FrequencyList<Markov.Symbol<String>> nextPosFreq =
          posChain.getNexts(Utils.lastN(symbols, posChain.getN()));

      return wordChain
               .getNexts(Utils.lastN(symbols, wordChain.getN()))
               .stream()
               .filter(x -> !x.fst.isSpecial()) // ignore special symbols
            // .peek(e -> System.err.println(e))
               .map(x -> x.setSecond(
                             posMap.get(x.fst.getValue())
                               .stream()
                               .mapToDouble(y -> nextPosFreq.get(new Markov.Symbol(y.fst)) * y.snd)
                               .sum()))
               .sorted((x,y) -> Double.compare(x.snd, y.snd))
               .map(x -> x.fst)
               .collect(ArrayList::new, ArrayList::add,
                        ArrayList::addAll);
    }
  }
}
