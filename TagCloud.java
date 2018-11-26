import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine1L;
import components.utilities.Reporter;

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
     * the {@code compareTo(String)}) java.util method to compare keys.
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
     * the {@code compareTo(Integer)}) java.util method to compare values.
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
     * Generates a {@code Map<String, Integer>} containing each word in the text
     * file {@code in} as keys and their number of occurrences in {@code in} as
     * values.
     *
     * @param in
     *            the input text file from which the map is generated
     * @return a {@code Map<String, Integer>} containing each word in {@code in}
     *         as keys and their number of occurrences in {@code in} as values
     * @ensures [the returned map contains all the words in the input file as
     *          keys, and each key's value is the number of times that key
     *          appears in the text file]
     */
    private static Map<String, Integer> generateMapWithCount(SimpleReader in) {
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
        separators.add('\'');
        separators.add('\"');
        separators.add('`');
        separators.add('*');
        separators.add('(');
        separators.add(')');
        separators.add('[');
        separators.add(']');
        separators.add('{');
        separators.add('}');
        separators.add('\\');
        separators.add('|');
        separators.add('<');
        separators.add('>');
        separators.add('~');
        separators.add('^');
        separators.add('@');
        separators.add('#');
        separators.add('$');
        separators.add('&');
        separators.add('+');
        separators.add('=');
        separators.add(';');
        separators.add(':');
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
                        || key.contains("_") || key.contains("\'")
                        || key.contains("\"") || key.contains("`")
                        || key.contains("*") || key.contains("(")
                        || key.contains(")") || key.contains("[")
                        || key.contains("]") || key.contains("{")
                        || key.contains("}") || key.contains("\\")
                        || key.contains("|") || key.contains("<")
                        || key.contains(">") || key.contains("~")
                        || key.contains("^") || key.contains("@")
                        || key.contains("#") || key.contains("$")
                        || key.contains("&") || key.contains("+")
                        || key.contains("=") || key.contains(";")
                        || key.contains(":")) {
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
                    i += key.length();
                }
            }
        }
        return wordCountMap;
    }

    /**
     * Put a short phrase describing the static method myMethod here.
     */
    private static SortingMachine<Map.Pair<String, Integer>> countSortingMachine(
            Map<String, Integer> map, CountComparator c) {
        SortingMachine<Map.Pair<String, Integer>> sorter = new SortingMachine1L<Map.Pair<String, Integer>>(
                c);
        Map<String, Integer> temp = map.newInstance();
        while (map.size() > 0) {
            Map.Pair<String, Integer> pair = map.removeAny();
            sorter.add(pair);
            temp.add(pair.key(), pair.value());
        }
        map.transferFrom(temp);
        return sorter;
    }

    /**
     * Put a short phrase describing the static method myMethod here.
     */
    private static SortingMachine<Map.Pair<String, Integer>> alphabeticSortingMachine(
            Map<String, Integer> map, Alphabetize c) {
        SortingMachine<Map.Pair<String, Integer>> sorter = new SortingMachine1L<Map.Pair<String, Integer>>(
                c);
        Map<String, Integer> temp = map.newInstance();
        while (map.size() > 0) {
            Map.Pair<String, Integer> pair = map.removeAny();
            sorter.add(pair);
            temp.add(pair.key(), pair.value());
        }
        map.transferFrom(temp);
        return sorter;
    }

    /**
     * Put a short phrase describing the static method myMethod here.
     */
    private static Map<String, Integer> generateShortenedMap(
            SortingMachine<Map.Pair<String, Integer>> sorter, int n) {
        Map<String, Integer> map = new Map1L<String, Integer>();
        sorter.changeToExtractionMode();
        int i = 0;
        while (i < n) {
            Map.Pair<String, Integer> pair = sorter.removeFirst();
            map.add(pair.key(), pair.value());
            i++;
        }
        return map;
    }

    /**
     * Put a short phrase describing the static method myMethod here.
     */
    private static void outputTagCloud(SimpleWriter out,
            SortingMachine<Map.Pair<String, Integer>> countSorter,
            SortingMachine<Map.Pair<String, Integer>> alphaSorter, int n) {
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
        //declare comparator objects
        Alphabetize alphabetize = new Alphabetize();
        CountComparator countCompare = new CountComparator();

        //create console input and output streams
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        //prompt user for name of input file
        out.println("Input File: ");
        String fileName = in.nextLine();
        SimpleReader fileIn = new SimpleReader1L(fileName);

        //prompt user for name of output file
        out.println("Output File: ");
        SimpleWriter fileOut = new SimpleWriter1L(in.nextLine());

        //prompt user for number of words in cloud tag
        out.println("Number of words in cloud tag: ");
        int n = in.nextInteger();

        //generate map of all terms and their respective counts from input file
        Map<String, Integer> bigMap = generateMapWithCount(fileIn);

        //check for user error on the value of n
        boolean a = n >= 0;
        Reporter.assertElseFatalError(a, "Error: n is a negative number.");
        boolean b = n <= bigMap.size();
        Reporter.assertElseFatalError(b,
                "Error: n is larger than the number of words in the file.");

        //generate a sorting machine sorted by count with the big map
        SortingMachine<Map.Pair<String, Integer>> countSorter1 = countSortingMachine(
                bigMap, countCompare);

        //generate a map with n words, using up the sorting machine
        Map<String, Integer> smallMap = generateShortenedMap(countSorter1, n);

        //generate another sorting machine sorted by count with the new map
        SortingMachine<Map.Pair<String, Integer>> countSorter2 = countSortingMachine(
                smallMap, countCompare);

        //generate a sorting machine sorted alphabetically with the new map
        SortingMachine<Map.Pair<String, Integer>> alphaSorter = alphabeticSortingMachine(
                smallMap, alphabetize);

        //output HTML code for the tag cloud to the output file
        outputTagCloud(fileOut, countSorter2, alphaSorter, n);

        /*
         * Close input and output streams
         */
        in.close();
        out.close();
        fileIn.close();
        fileOut.close();
    }

}
