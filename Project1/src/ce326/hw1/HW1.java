package ce326.hw1;

import java.util.*;

public class HW1 {

  public static void main(String []args) {
    if(args.length < 2) {
      System.out.println("Insufficient arguments!");
      return;
    }
    
    String []words = Utilities.readFile(args[0]);
    Trie trie = new Trie(words);
    String word;
    
    switch(args[1]) {
      case "size":
        System.out.println(trie.size());
        break;
      case "contains_word":
        if(args.length < 3) {
          System.out.println("Insufficient arguments!");
          return;
        }
        word = args[2];
        if(trie.contains(word))
          System.out.println(word + " found");
        else
          System.out.println(word + "not found");
        break;
      case "print_preorder":
        System.out.println(trie);
        break;
      case "print_dot":
        String dotStr = trie.toDotString();
        Utilities.writeFile(dotStr, "Trie.dot");
        Utilities.dot2png("Trie");
        System.out.println("DOT print finished!");
        break;
      case "distant_words":
        if(args.length < 4) {
          System.out.println("Insufficient arguments!");
          return;
        }
        int max_diff = Integer.parseInt(args[2]);
        if(max_diff <= 0) {
          System.out.println("Invalid 3rd argument!");
          return;
        }
        word = args[3];
        String []diff_words;
        if(max_diff == 1) {
          diff_words = trie.differByOne(word);
        }
        else {
          diff_words = trie.differBy(word, max_diff);
        }
        if(diff_words != null && diff_words.length>0) {
            for(String diff_word : diff_words)
              if(diff_word!=null)
                System.out.println(diff_word);
        }
        else {
          System.out.println("No words found!");
        }
        break;
      case "prefixed_words":
        if(args.length < 3) {
          System.out.println("Insufficient arguments!");
          return;
        }
        String prefix = args[2];
        String []prefixed_words = trie.wordsOfPrefix(prefix);
        if(prefixed_words!=null && prefixed_words.length>0) {
          for(String prefixed_word : prefixed_words)
            if(prefixed_word!=null)
              System.out.println(prefixed_word);
        }
        else {
          System.out.println("No words found!");
        }
        break;
      default:
        System.out.println("Invalid command line option!");
    }
  }
    
}
