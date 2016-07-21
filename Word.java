public class Word implements Comparable<Word> {
    private String word;
    private int hash;
    public static boolean optimal = false;

    public Word(final String s) {
        String res = s.toLowerCase().trim();
        //code to strip punctuation

        //this condition define the situation where words can have ' or -
        if (res.matches(".*[a-z]'[a-z].*")||res.matches(".*[a-z]-[a-z].*")) {
            res = res.replaceFirst("'","A");
            res = res.replaceFirst("-","B");
        }
        res = res.replaceAll("\\W","");
        res = res.replaceFirst("A","'");
        res = res.replaceFirst("B","-");
        if (optimal==false){
            this.word = res;
        } else {
            this.word = res.intern();
        }
        this.hash = res.hashCode();
    }

    //use this method in the Comparator
    //in WordFrequencyTable.java
    @Override
    public int compareTo(final Word w) {
        return this.word.compareTo(w.word);
    }

    @Override
    public String toString() {
        return this.word;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Word)) return false;
        Word w = (Word) o;
        if (optimal==false){
            return this.word.equals(w.word);
        } else {
            return this.word==w.word;
        }
    }

    @Override
    public int hashCode() {
        //return this.word.hashCode();
        return hash;
    }

    public boolean isEmpty() {
        return this.word.isEmpty();
    }
}
