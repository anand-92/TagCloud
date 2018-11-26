import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;

/**
 * Put a short phrase describing the program here.
 *
 * @author Hudson Arledge
 *
 */
public final class TagCloud {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloud() {
    }

    /**
     * A {@code Comparator}) class that compares Map.Pair<String, Integer> using
     * the {@code compareTo(String)}) java.util method.
     *
     * @author Hudson Arledge and Nik Anand
     *
     */
    private static class Alphabetize
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public int compare(Map.Pair<String, Integer> o1,
                Map.Pair<String, Integer> o2) {
            return o1.key().compareTo(o2.key());
        }
    }

    /**
     * A {@code Comparator}) class that compares Map.Pair<String, Integer> using
     * the {@code compareTo(Integer)}) java.util method.
     *
     * @author Hudson Arledge and Nik Anand
     *
     */
    private static class CountComparator
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public int compare(Map.Pair<String, Integer> o1,
                Map.Pair<String, Integer> o2) {
            return o1.value().compareTo(o2.value());
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        String output = "";
        StringBuilder outputBuilder = new StringBuilder();
        int index = position;
        boolean loop = true;
        //case for separator string
        if (separators.contains(text.charAt(index))) {
            //keeps adding characters to the StringBuilder until either a
            //non-separator is read or the end of the string is reached
            while (loop) {
                outputBuilder.append(text.charAt(index));
                index++;
                if (index >= text.length()) {
                    loop = false;
                } else if (!separators.contains(text.charAt(index))) {
                    loop = false;
                }
            }
            //case for non-separator string
        } else {
            //keeps adding characters to the StringBuilder until either a
            //separator is read or the end of the string is reached
            while (loop) {
                outputBuilder.append(text.charAt(index));
                index++;
                if (index >= text.length()) {
                    loop = false;
                } else if (separators.contains(text.charAt(index))) {
                    loop = false;
                }
            }
        }
        output = outputBuilder.toString();
        //return the resulting word or separator
        return output;
    }

    /**
     * Put a short phrase describing the static method myMethod here.
     */
    private static Map<String, Integer> generateWordCountMap(SimpleReader in) {
        //Create set of separators
        Set<Character> separators = new Set1L<>();
        separators.add(' ');
        separators.add(',');
        separators.add('/');
        separators.add('.');
        separators.add('-');
        separators.add('!');
        separators.add('?');
        separators.add('_');
        //declare map to be generated
        Map<String, Integer> wordCountMap = new Map1L<String, Integer>();
        //generate map
        while (!in.atEOS()) {
            String text = in.nextLine().toLowerCase();
            int i = 0;
            //read each word or separator in the line, and store any non-separators
            //in the queue.
            while (i < text.length()) {
                String key = nextWordOrSeparator(text, i, separators);
                if (key.contains(" ") || key.contains(",") || key.contains("/")
                        || key.contains(".") || key.contains("-")
                        || key.contains("!") || key.contains("?")
                        || key.contains("_")) {
                    i += key.length();
                } else {
                    //store all non-separators and their respective counts in
                    //the map
                    if (wordCountMap.hasKey(key)) {
                        int count = wordCountMap.value(key) + 1;
                        wordCountMap.replaceValue(key, count);
                    } else {
                        wordCountMap.add(key, 1);
                    }
                }
            }
        }
        return wordCountMap;
    }

    private static SortingMachine<Map.Pair<String, Integer>> SortingMachineByCount(
            Map<String, Integer> map, int n, Comparator C) {
        return null;
    }

    private static SortingMachine<Map.Pair<String, Integer>> SortingMachineByAlphabet(
            Map<String, Integer> map, Comparator C) {
        return null;
    }

    private static void outputTagCloud(SimpleWriter out,
            SortingMachine<Map.Pair<String, Integer>> countSorted,
            Queue<String> alphabeticSorted, int n) {
        //TODO - output header

        //TODO - output tag-cloud in alphabetical order, with n words and the font size of each word corresponding to its relative count
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }

}
