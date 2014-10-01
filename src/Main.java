import java.io.*;
import java.util.*;

public class Main {
  private static List<String> tokenize(Scanner sc) {
    List<String> tokens = new ArrayList<>();
    while (sc.hasNext()) tokens.add(sc.next());
    return tokens;
  }

  public static void main(String[] args) throws FileNotFoundException {
    Markov<String> markov = new Markov<>(1);

    if (args.length != 2) {
      System.err.printf("Usage: %s <file> <count>\n", "java Main");
      System.exit(1);
    }

 // markov.feed(tokenize(new Scanner(new File(args[0]))));

    Scanner sc = new Scanner(new File(args[0]));
    while (sc.hasNextLine()) {
      markov.feed(tokenize(new Scanner(sc.nextLine())));
    }

    int count = Integer.parseInt(args[1]);

    for (int i = 0; i < count; i++) {
      LinkedList<Markov.Symbol<String>> context = new LinkedList<>();
      Markov.Symbol<String> next = markov.START;
      context.add(next);

      System.out.print("");
      while (next != markov.END) {
        if (next != markov.START) System.out.printf("%s ", next.getValue());
        context.addLast(next);
        context.removeFirst();
        next = markov.randomNext(context);
      }
      System.out.println();
    }
  }
}
