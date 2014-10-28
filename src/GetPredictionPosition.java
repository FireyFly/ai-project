
import java.io.*;
import java.util.*;

class GetPredictionPosition {
    public static void main(String[] args) {
        try (BufferedReader b = new BufferedReader(new InputStreamReader(System.in))) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("chains.dat"));
            String taggerModel = (String) ois.readObject();
            Markov<String> wordChain = (Markov<String>) ois.readObject();
            Markov<String> posChain  = (Markov<String>) ois.readObject();
            Map<String, FrequencyList<String>> frequencyMap =
                (Map<String, FrequencyList<String>>) ois.readObject();

            Model model = new Model.GrammarModel(wordChain, posChain, frequencyMap, taggerModel);
            Model simpleModel = new Model.SimpleModel(wordChain);
            System.err.println("---");
            for (String line; (line = b.readLine()) != null; ) {
                String[] words = line.toLowerCase().split(" ");
                String joined = String.join(" ", Arrays.copyOfRange(words, 0, words.length - 1));
                String nextWord = words[words.length - 1];

                System.out.format("%s %d %d%n", nextWord,
                                  getPredictionIndex(model, joined, nextWord),
                                  getPredictionIndex(simpleModel, joined, nextWord));
            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
    }

    public static int getPredictionIndex(Model model, String line, String nextWord) {
        Iterator<String> it = model.predictNext(line)
                .map(pair -> pair.e1)
                .filter(sym -> !sym.isSpecial())
                .map(sym -> sym.getValue())
                .iterator();
        for (int i = 0; it.hasNext(); i++) {
            if (nextWord.equals(it.next())) {
                return i;
            }
        }
        return -1;
    }
}
