import java.io.*;

import java.util.*;
import java.util.stream.Stream;

public class SentenceGenerator {
    
    private static class Interval {
       final double start, end;

        public Interval(double start, double end) {
            if(end < start || Double.isNaN(start) || Double.isNaN(end)){
                throw new IllegalArgumentException();
            }
            this.start = start;
            this.end = end;
        }
        
        public boolean isInInterval(double d){
            return d >= start && d <= end;
        }
       
        
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
            String start = "";
            StringBuilder line = new StringBuilder(start);
            while (true) {
                Stream<Utils.Pair<Markov.Symbol<String>, Double>> next = model.predictNext(line.toString());
                double sum = next.mapToDouble(x -> x.e2).sum();
                
                Iterator<Utils.Pair<Markov.Symbol<String>, Double>> iter = next.iterator();
                
                double rand = Math.random();
                Markov.Symbol<String> found = null;
                while(iter.hasNext()){
                    Utils.Pair<Markov.Symbol<String>, Double> p = iter.next();
                    if(rand <= p.e2/sum){
                        found = p.e1;
                        break;
                    }
                    rand -= (p.e2/sum);                 
                }
                if(found == null){
                    throw new IllegalStateException();
                } else if(found.isSpecial()){
                    break;
                } else {
                    if(line.length() != 0){
                        line.append(" ");
                    }
                    line.append(found.getValue());
                }                  
            }

            System.out.println(line);
            

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
