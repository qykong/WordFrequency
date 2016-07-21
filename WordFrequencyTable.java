import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.List;

public class WordFrequencyTable {
    private TreeMap<Word, Integer> zipfTable = new TreeMap<Word, Integer>();
    public static long buildTime;
    public static boolean optimal = false;

    public WordFrequencyTable() {}

    public WordFrequencyTable(String filePath) {
        if (optimal) {Word.optimal = true;}
        long start = System.currentTimeMillis();
        buildTable(filePath);
        long finish = System.currentTimeMillis();
        buildTime += finish - start;
        System.out.print("built time is "+(finish-start)+"(ms)..");
    }

    private void buildTable(String filePath) {

        try {
            Path p = Paths.get(filePath);
            List<String> lines = Files.readAllLines(p, StandardCharsets.ISO_8859_1);

            for (String line: lines) {
                String[] words = line.split(" |--");//remove -- in tests
                for (String word: words) {
                    Word w = new Word(word);
                    if (w.isEmpty()) {continue;}
                    if (zipfTable.containsKey(w)) {
                        zipfTable.put(w, zipfTable.get(w)+1);
                    } else {
                        zipfTable.put(w, 1);
                    }
                }
            }
        } catch (IOException e){
            System.out.println("Can't open the file.");
            System.out.println(e);
            System.exit(1);
        }
    }

    public TreeMap<Word, Integer> getMap() {
         return zipfTable;
    }
    // use this method to sort the whole map in accordance with
    // the frequency of different words and print out the frequency
    // table.
    public void getRank(int number) {
        System.out.println("The total build time is "+WordFrequencyTable.buildTime+".");
        int i=1;
        long start = System.currentTimeMillis();
        compareByValue comparator = new compareByValue(zipfTable);
        TreeMap<Word, Integer> sortedMap = new TreeMap<Word, Integer>(comparator);
        sortedMap.putAll(zipfTable);
        zipfTable = sortedMap;
        Word next = zipfTable.firstKey();

        System.out.println("The cumulative rank-frequency table (fist "+number+" entries) is as follows");
        while (number >= i) {
            System.out.printf("%-6d%-13s%d\n",i,"("+next+")",zipfTable.get(next));
            next = zipfTable.higherKey(next);
            i++;
        }
        long finish = System.currentTimeMillis();
        System.out.println("The sort time is "+(finish-start)+"(ms).");
    }
    // combine frequency tables of different files
    public void putAll(WordFrequencyTable w) {
        TreeMap<Word, Integer> m = w.getMap();
        Map.Entry<Word, Integer> k;

        while (m.isEmpty() == false) {
            k = m.pollFirstEntry();
            if (zipfTable.containsKey(k.getKey())) {
                zipfTable.put(k.getKey(),k.getValue()+zipfTable.get(k.getKey()));
            } else {
                zipfTable.put(k.getKey(),k.getValue());
            }
        }
    }
}

// use this class as a comparator for treemap to sort
// the frequency table.
class compareByValue
    implements Comparator<Word> {
    private Map<Word, Integer> zipfTable = new HashMap<Word, Integer>();

    public compareByValue(Map<Word, Integer> m) {
         zipfTable = m;
    }

    @Override
    public int compare(Word o1, Word o2) {
        if (o1.equals(o2)) { return 0; }
        if (zipfTable.get(o1) > zipfTable.get(o2)) { return -1; }
        else if (zipfTable.get(o1) < zipfTable.get(o2)) { return 1; }
        else { return o1.compareTo(o2); }
    }
}
