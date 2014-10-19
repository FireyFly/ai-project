
import java.io.*;
import java.util.*;

import cmu.arktweetnlp.*;

public class Tag {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ./train.sh <learning-data>");
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(args[0]), "latin1"))) {
            Tagger tagger = new Tagger();
            tagger.loadModel("model.20120919");

            Markov<String> wordModel = new Markov<>(1);
            Markov<String> posModel = new Markov<>(3);

            for (String tweet; (tweet = reader.readLine()) != null; ) {
                List<Tagger.TaggedToken> tagged = tagger.tokenizeAndTag(tweet);
                wordModel.feed(map(tagged, token -> token.token));
                posModel.feed(map(tagged, token -> token.tag));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

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
}
