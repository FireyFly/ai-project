
import java.io.*;
import java.util.*;

import cmu.arktweetnlp.*;

public class Learner {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ./train.sh <learning-data> [model]");
            System.exit(1);
        }

        String model = "model.20120919";
        if (args.length >= 2) {
            model = args[1];
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(args[0]), "latin1"))) {
            Tagger tagger = new Tagger();
            tagger.loadModel(model);

            Markov<String> wordModel = new Markov<>(1);
            Markov<String> posModel = new Markov<>(3);
            Map<String, FrequencyList<String>> frequencyMap = new HashMap<>();

            int i = 0;
            for (String tweet; (tweet = reader.readLine()) != null; ) {
                System.err.printf("\u001B[G\u001B[K%d", ++i);
                List<Tagger.TaggedToken> tagged = tagger.tokenizeAndTag(tweet.toLowerCase());
                wordModel.feed(Utils.map(tagged, token -> token.token));
                posModel.feed(Utils.map(tagged, token -> token.tag));

                for (Tagger.TaggedToken tt : tagged) {
                    if (!frequencyMap.containsKey(tt.token)) {
                        frequencyMap.put(tt.token, new FrequencyList<String>());
                    }

                    frequencyMap.get(tt.token).add(tt.tag);
                }
            }
            System.err.printf("\n");

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("chains.dat"));
            oos.writeObject(model);
            oos.writeObject(wordModel);
            oos.writeObject(posModel);
            oos.writeObject(frequencyMap);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
