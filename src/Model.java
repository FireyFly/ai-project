import java.io.*;
import java.util.*;
import java.util.stream.*;

import cmu.arktweetnlp.*;

/** A model for text prediction. */
public interface Model {

  /** Given N preceding words of context, predict the next word.  Returns a
   *  stream of predictions ordered by likelihood. */
  public Stream<Utils.Pair<Markov.Symbol<String>, Double>> predictNext(String context);

  public static List<String> tokenize(String context) {
    Scanner sc = new Scanner(context);
    List<String> tokens = new ArrayList<>();
    while (sc.hasNext()) tokens.add(sc.next());
    return tokens;
  }

  /** A Markov chain model on only the preceding words and the likelihood of
   *  the next word.  That is, a n-gram model on words (as strings). */
  public static class SimpleModel implements Model {
    private Markov<String> wordChain;

    public SimpleModel(Markov<String> wordChain) {
      this.wordChain = wordChain;
    }

    public Stream<Utils.Pair<Markov.Symbol<String>, Double>> predictNext(String context) {
      List<String> tokenizedContext = Model.tokenize(context);
      if (tokenizedContext.isEmpty()) {
        return Stream.of();
      }

      List<Markov.Symbol<String>> symbols =
          Utils.map(tokenizedContext, word -> new Markov.Symbol(word));

      return wordChain.getNexts(Utils.lastN(symbols, wordChain.getN()))
                      .stream()
                      .sorted((x,y) -> Double.compare(y.e2, x.e2));
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
    private Tagger tagger = new Tagger();

    public GrammarModel(Markov<String> wordChain, Markov<String> posChain,
                        Map<String, FrequencyList<String>> posMap) throws IOException {
      this.wordChain = wordChain;
      this.posChain = posChain;
      this.posMap = posMap;

      this.tagger.loadModel("model.20120919");
    }

    public Stream<Utils.Pair<Markov.Symbol<String>, Double>> predictNext(String context) {
      List<Tagger.TaggedToken> tokenizedContext = this.tagger.tokenizeAndTag(context);
      if (tokenizedContext.isEmpty()) {
        return Stream.of();
      }

      List<Markov.Symbol<String>> posSymbols =
          Utils.map(tokenizedContext, token -> new Markov.Symbol(token.tag));
      List<Markov.Symbol<String>> wordSymbols =
          Utils.map(tokenizedContext, token -> new Markov.Symbol(token.token));

      FrequencyList<Markov.Symbol<String>> nextPosFreq =
          posChain.getNexts(Utils.lastN(posSymbols, posChain.getN()));

      // Debug output: token→POS mapping and POS frequency distribution
   // System.err.println(Utils.map(tokenizedContext,
   //     token -> token.token + "(" + token.tag + ")"));
   // System.err.println(nextPosFreq.toList());

      return wordChain
               // Distribution of the word following the context in the word-chain
               .getNexts(Utils.lastN(wordSymbols, wordChain.getN()))
               .stream()                                     // as a stream of pairs
               .filter(x -> !x.e1.isSpecial())               // ignore special next-symbols
               .map(x -> Utils.triplet(x.e1, x.e2,           // compute weight
                             posMap.get(x.e1.getValue())
                               .stream()
                               .mapToDouble(y -> nextPosFreq.get(new Markov.Symbol(y.e1)) * y.e2)
                               .sum() * x.e2))
               .sorted((x,y) -> Double.compare(y.e3, x.e3))  // sort by weight
               // Debug output: probability of a particular symbol to follow the context
            // .peek(x -> System.err.printf("%s (%f) %s %f\n", x.e1, x.e2,
            //                              posMap.get(x.e1.getValue()).toList(),
            //                              x.e3))
               .map(x -> Utils.pair(x.e1, x.e2));            // leave only pairs of (Symbol, weight)
    }
  }
}
