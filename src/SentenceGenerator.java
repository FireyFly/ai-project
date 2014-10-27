import java.io.*;

import java.util.*;
import java.util.stream.Stream;

public class SentenceGenerator {

    private static class Interval {

        final double start, end;

        public Interval(double start, double end) {
            if (end < start || Double.isNaN(start) || Double.isNaN(end)) {
                throw new IllegalArgumentException();
            }
            this.start = start;
            this.end = end;
        }

        public boolean isInInterval(double d) {
            return d >= start && d <= end;
        }

    }

    public static void generateSentence(Model model, String start) {
        System.out.print(start + "");
        StringBuilder line = new StringBuilder(start);
        int dotcount = 0;
        while (true) {
            List<Utils.Pair<Markov.Symbol<String>, Double>> list = Utils.toList(model.predictNext(line.toString()));
            double sum = 0;
            for (Utils.Pair<Markov.Symbol<String>, Double> p : list) {
                sum += p.e2;
            }
            double r = Math.random();
            Markov.Symbol<String> found = null;
            for (Utils.Pair<Markov.Symbol<String>, Double> p : list) {
                double prop = p.e2 / sum;
                if (r <= prop) {
                    found = p.e1;
                    break;
                }
                r -= prop;
            }
            if (found == null) {
                break;
            } else if (found.isSpecial()) {
                break;
            } else {
                if (line.length() != 0) {
                    line.append(" ");
                    System.out.print(" ");
                }
                line.append(found.getValue());
                System.out.print(found.getValue());
                
                if(found.getValue().equals(".") && ++dotcount == 3){
                    break;
                }
            }
        }
        System.out.println();
    }

    public static void main(String[] args) throws ClassNotFoundException, RuntimeException {
        if (args.length != 1) {
            System.err.println("Usage: java SentenceGenerator <model>  -- use model <model> ('simple' or 'grammar')");
            System.exit(1);
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("chains.dat"));
            String taggerModel = (String) ois.readObject();
            Markov<String> wordChain = (Markov<String>) ois.readObject();
            Markov<String> posChain = (Markov<String>) ois.readObject();
            Map<String, FrequencyList<String>> frequencyMap
                    = (Map<String, FrequencyList<String>>) ois.readObject();

            Model model = args[0].equals("simple") ? new Model.SimpleModel(wordChain)
                    : args[0].equals("grammar") ? new Model.GrammarModel(wordChain, posChain, frequencyMap, taggerModel)
                            : null;

            if (model == null) {
                System.err.println("Couldn't construct model '" + args[0] + "'.");
                System.exit(1);
            }
            for(int i=0;i<100;i++){
                generateSentence(model, "how");
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
