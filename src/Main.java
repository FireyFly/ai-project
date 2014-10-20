import java.io.*;
import java.util.*;

public class Main {

  public static void main(String[] args) throws ClassNotFoundException {
    if (args.length != 1) {
      System.err.println("Usage: java Main <model>  -- use model <model> ('simple' or 'grammar')");
      System.exit(1);
    }

    try {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream("chains.dat"));
      Markov<String> wordChain = (Markov<String>) ois.readObject();
      Markov<String> posChain  = (Markov<String>) ois.readObject();
      Map<String, FrequencyList<String>> frequencyMap =
          (Map<String, FrequencyList<String>>) ois.readObject();

      Model model = args[0].equals("simple")?  new Model.SimpleModel(wordChain)
                  : args[0].equals("grammar")? new Model.GrammarModel(wordChain, posChain, frequencyMap)
                  :                            null;

      if (model == null) {
        System.err.println("Couldn't construct model '" + args[0] + "'.");
        System.exit(1);
      }

      for (Scanner sc = new Scanner(System.in); sc.hasNextLine(); ) {
        List<Markov.Symbol<String>> predictions = model.predictNext(sc.nextLine());
        if (predictions.isEmpty()) {
            continue;
        }

        System.out.println(Utils.map(predictions, sym -> sym.getValue()));
      }

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
