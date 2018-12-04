import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

import components.map.Map;
import components.map.Map1L;
import components.set.Set;
import components.set.Set1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine1L;

/**
 * Generates a Tag Cloud from a text file. The Tag Cloud lists the most frequent
 * words in the text file in alphabetical order and changes the font size of
 * each word based on its number of occurrences in the file. Each word's count
 * can be seen by hovering the cursor over the word.
 *
 * @author Hudson Arledge and Nik Anand
 *
 */
public final class TagCloud {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloud() {
    }

    /**
     * Global integer for the minimum number of word occurrences in the file.
     */
    private static int min = 0;

    /**
     * Global integer for the maximum number of word occurrences in the file.
     */
    private static int max = 0;

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
            return o2.value().compareTo(o1.value());
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
            HashSet<Character> separators) {
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
     * @param bufferedReader
     *            the input text file from which the map is generated
     * @param n
     *            the number of words in the cloud tag as specified by the user
     * @return a {@code Map<String, Integer>} containing each word in {@code in}
     *         as keys and their number of occurrences in {@code in} as values
     * @throws IOException
     * @ensures [the returned map contains all the words in the input file as
     *          keys, and each key's value is the number of times that key
     *          appears in the text file] and [n is not larger than the number
     *          of words in the file]
     */
    private static HashMap<String, Integer> generateMapWithCount(
            BufferedReader bufferedReader, int n) throws IOException {
        //Create set of separators
        HashSet<Character> separators = new HashSet<>();
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
        HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();
        //generate map
        int count = 0;

        try {
            while (bufferedReader.ready()) {
                String text = bufferedReader.readLine().toLowerCase();
                int i = 0;
                //read each word or separator in the line, and store any non-separators
                //in the queue.
                while (i < text.length()) {
                    String key = nextWordOrSeparator(text, i, separators);
                    if (key.contains(" ") || key.contains(",")
                            || key.contains("/") || key.contains(".")
                            || key.contains("-") || key.contains("!")
                            || key.contains("?") || key.contains("_")
                            || key.contains("\'") || key.contains("\"")
                            || key.contains("`") || key.contains("*")
                            || key.contains("(") || key.contains(")")
                            || key.contains("[") || key.contains("]")
                            || key.contains("{") || key.contains("}")
                            || key.contains("\\") || key.contains("|")
                            || key.contains("<") || key.contains(">")
                            || key.contains("~") || key.contains("^")
                            || key.contains("@") || key.contains("#")
                            || key.contains("$") || key.contains("&")
                            || key.contains("+") || key.contains("=")
                            || key.contains(";") || key.contains(":")) {
                        i += key.length();
                    } else {
                        //store all non-separators and their respective counts in
                        //the map
                        if (wordCountMap.hasKey(key)) {
                            int value = wordCountMap.value(key) + 1;
                            wordCountMap.replaceValue(key, value);
                        } else {
                            wordCountMap.add(key, 1);
                        }
                        i += key.length();
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file");
            return null;
        }
        //print error message if n is too big
        if (count < n) {
            System.out.println(
                    "Error: n is larger than the number of words in the file");
        }
        return wordCountMap;
    }

    /**
     * Returns a {@code SortingMachine<Map.Pair<String, Integer>>} in insertion
     * mode containing the elements of {@code map}. The SortingMachine is
     * ordered based on {@code c}.
     *
     * @param map
     *            the map whose elements are used to create the sorting machine
     * @param c
     *            the ordering for the sorting machine
     * @return a {@code SortingMachine<Map.Pair<String, Integer>>} containing
     *         the elements of {@code map} with ordering from {@code c}
     * @ensures [the sorting machine is in insertion mode with ordering c and it
     *          contains all of the elements of the map]
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
     * Returns a {@code SortingMachine<Map.Pair<String, Integer>>} in insertion
     * mode containing the elements of {@code map}. The SortingMachine is
     * ordered based on {@code c}.
     *
     * @param map
     *            the map whose elements are used to create the sorting machine
     * @param c
     *            the ordering for the sorting machine
     * @return a {@code SortingMachine<Map.Pair<String, Integer>>} in insertion
     *         mode containing the elements of {@code map} with ordering from
     *         {@code c}
     * @ensures [the sorting machine is in insertion mode with ordering c and it
     *          contains all of the elements of the map]
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
     * Generates and returns a {@code Map<String, Integer>} containing the first
     * n elements of {@code sorter}.
     *
     * @param sorter
     *            the sorting machine whose elements are used to make the map
     * @param n
     *            the number of elements to be taken from {@code sorter}
     * @updates sorter
     *
     * @return a {@code Map<String, Integer>} containing the first n elements of
     *         {@code sorter}
     * @requires sorter is in insertion mode
     * @ensures [the sorter is in extraction mode and only contains elements
     *          that were not removed] and [the map being returned contains the
     *          first n elements of the sorter in its original state]
     */
    private static Map<String, Integer> generateShortenedMap(
            SortingMachine<Map.Pair<String, Integer>> sorter, int n) {

        Map<String, Integer> map = new Map1L<String, Integer>();
        sorter.changeToExtractionMode();
        Map.Pair<String, Integer> currentPair = sorter.removeFirst();
        max = currentPair.value();
        map.add(currentPair.key(), currentPair.value());
        int i = 1;
        while (i < n) {
            Map.Pair<String, Integer> pair = sorter.removeFirst();
            map.add(pair.key(), pair.value());
            min = pair.value();
            i++;
        }
        return map;
    }

    /**
     * Outputs to a given file {@code out} HTML code that prints words from
     * {@code alphaSorter} in order, adjusting the word's font size based on
     * occurrences and making the occurrences of a word visible by hovering it
     * with the cursor.
     *
     * @param bufferedWriter
     *            the output text file
     * @param alphaSorter
     *            an alphabetically sorted sorting machine of words with counts
     * @param n
     *            the given number of words in cloud tag
     * @param fileName
     *            the name of the given input file
     * @requires alphaSorter is in insertion mode
     * @updates alphaSorter
     * @ensures [a valid html file is generated to the given output filename]
     *          and [alphaSorter is in extraction mode and has no elements]
     */
    private static void outputTagCloud(BufferedWriter bufferedWriter,
            SortingMachine<Map.Pair<String, Integer>> alphaSorter, int n,
            String fileName) {

        //output header
        try {
            bufferedWriter.write("<html> \n");
        } catch (IOException e) {
            System.err.println("Error writing to file");
            return;
        }
        try {
            bufferedWriter.write("<head> " + "<title> Top " + n + "words in "
                    + fileName + "</title>\n");
        } catch (IOException e) {
            System.err.println("Error writing to file");
            return;
        }
        try {
            bufferedWriter.write(
                    "<link href=\"http://cse.osu.edu/software/2231/web-sw2/"
                            + "assignments/projects/tag-cloud-generator/data/"
                            + "tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">\n");
        } catch (IOException e) {
            System.err.println("Error writing to file");
            return;
        }
        try {
            bufferedWriter.write("</head>\n");
        } catch (IOException e) {
            System.err.println("Error writing to file");
            return;
        }

        //output tag cloud
        try {
            bufferedWriter.write("<body data-gr-c-s-loaded=\"true\">\n");
        } catch (IOException e) {
            System.err.println("Error writing to file");
            return;
        }
        try {
            bufferedWriter.write(
                    "<h2>Top " + n + " words in " + fileName + "</h2><hr>\n");
        } catch (IOException e) {
            System.err.println("Error writing to file");
            return;
        }
        try {
            bufferedWriter
                    .write("<div class = \"cdiv\"> " + "<p class =\"cbox\">\n");
        } catch (IOException e) {
            System.err.println("Error writing to file");
            return;
        }
        alphaSorter.changeToExtractionMode();
        while (alphaSorter.size() != 0) {
            Map.Pair<String, Integer> pair = alphaSorter.removeFirst();
            final int a = 37;
            int font = (a) * (pair.value() - min);
            font /= (max - min);
            final int b = 11;
            font += b;
            try {
                bufferedWriter.write("<span style=\"cursor:default\" class=\"f"
                        + font + "\"" + " title=\"count: " + pair.value()
                        + "\">" + pair.key() + "</span>\n");
            } catch (IOException e) {
                System.err.println("Error writing to file");
                return;
            }

        }

        try {
            bufferedWriter.write("</p> </div> <body> </html>");
        } catch (IOException e) {
            System.err.println("Error writing to file");
            return;
        }
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     *
     */
    public static void main(String[] args)  {
        //declare comparator objects
        Alphabetize alphabetize = new Alphabetize();
        CountComparator countCompare = new CountComparator();

        //create console input and output streams
        Scanner scanner = new Scanner(System.in);

        //prompt user for name of input file
        System.out.println("Input File: ");
        String fileName = scanner.nextLine();

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file");
            scanner.close();
            return;
        }

        //prompt user for name of output file
        System.out.println("Output File: ");
        String fileNameOut = scanner.nextLine();

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileNameOut));
        } catch (IOException e) {
            System.err.println("Error creating file");
            scanner.close();
            try {
                bufferedReader.close();
            } catch (IOException e1) {
                System.err.println("Error closing file");
                return;
            }
            return;
        }

        //prompt user for number of words in cloud tag
        System.out.println("Number of words in cloud tag: ");
        //note that this will report an error if the user does not enter an integer
        int n = scanner.nextInt();

        //generate map of all terms and their respective counts from input file
        Map<String, Integer> bigMap;
        
        bigMap = generateMapWithCount(bufferedReader, n);
        if (bigMap == null) {
            scanner.close();
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                System.err.println("Error closing file");
                return;
            }
            return;
        }

        //check for user error for the value of n
        boolean a = n >= 0;
        if (!a) {
            System.out.println("Error: n is negative.");
        } else {

            //generate a sorting machine sorted by count with the big map
            SortingMachine<Map.Pair<String, Integer>> countSorter = countSortingMachine(
                    bigMap, countCompare);

            //generate a map with n words, using up the sorting machine
            Map<String, Integer> smallMap = bigMap.newInstance();
            if (bigMap.size() > 0) {
                smallMap = generateShortenedMap(countSorter, n);
            }

            //generate a sorting machine sorted alphabetically with the new map
            SortingMachine<Map.Pair<String, Integer>> aSorter = alphabeticSortingMachine(
                    smallMap, alphabetize);

            //output HTML code for the tag cloud to the output file
            outputTagCloud(bufferedWriter, aSorter, n, fileName);
        }

        /*
         * Close input and output streams
         */

        scanner.close();
        try {
            bufferedReader.close();
        } catch (IOException e) {
            System.err.println("Error closing file");
            return;
        }
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing to file");
            return;
        }
        
    }

}
