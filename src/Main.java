import java.io.*;
import java.util.*;

public class Main {
  private static List<String> tokenize(Scanner sc) {
    List<String> tokens = new ArrayList<>();
    while (sc.hasNext()) tokens.add(sc.next());
    return tokens;
  }

  public static void main(String[] args) throws ClassNotFoundException {
    try {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream("chains.dat"));
      Markov<String> wordChain = (Markov<String>) ois.readObject();
      Markov<String> posChain  = (Markov<String>) ois.readObject();
      Map<String, FrequencyList<String>> frequencyMap =
          (Map<String, FrequencyList<String>>) ois.readObject();

      Model model = new Model.SimpleModel(wordChain);

      Scanner sc = new Scanner(System.in);
      while (sc.hasNextLine()) {
        List<String> tokens = tokenize(new Scanner(sc.nextLine()));
        if (tokens.isEmpty()) continue;

        List<String> ngram = lastN(tokens, wordChain.getN());
        List<Markov.Symbol<String>> predictions = model.predictNext(ngram);

        System.out.println(predictions);
      }

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static List<String> lastN(List<String> list, int n) {
    List<String> res = new ArrayList<>();
    if (list.size() < n) {
      throw new IllegalArgumentException("Fewer than " + n + " values in list");
    }

    for (int i = 0; i < n; i++) {
      res.add(list.get(list.size() - n + i));
    }
    return res;
  }
}
