
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ac_test {

    static double diff1 = 0;
    static double diff2 = 0;
    static double totalDiff = 0;
    final static int ZERO_ASCII = 48;
    final static int SIX_ASCII = 54;
    final static int ASCII_OFFSET = 49;

    public static void main(String[] args) throws FileNotFoundException, IOException {

        File dictionaryFile = new File("dictionary.txt");
        File userHistoryFile = new File("user-history.txt");
        if (!userHistoryFile.exists()) { //creating file in local directory if it does not exist 
            try {
                userHistoryFile.createNewFile();
            } catch (Exception e) {
                System.out.println("Error creating file: user-history.txt");
                System.exit(0);
            }
        }
        Scanner inScan = new Scanner(System.in);
        Scanner UHFile = new Scanner(userHistoryFile);
        FileWriter UHWriter = new FileWriter(userHistoryFile, true);

        MyLinkedList DLBTrie = new MyLinkedList();
        ArrayList<String> userHistory = new ArrayList<>();
        ArrayList<String> predictions = new ArrayList<>();
        ArrayList<Double> predictionTimes = new ArrayList<>();

        fillDLBTrie(dictionaryFile, DLBTrie);
        while (UHFile.hasNext()) { //reading user history text file and storing it in the userHistory ArrayList
            userHistory.add(UHFile.next());
        }

        char c;
        MyLinkedList currLL = DLBTrie;
        String temp = "";

        System.out.print("Enter your first character: ");
        c = inScan.next().charAt(0);
        while (c != '!') {
            if (c == '$') {
                userHistory.add(temp);
                UHWriter.write(temp + "\n");
                temp = "";
                currLL = DLBTrie;

                System.out.print("\n");
                System.out.print("Enter first character of the next word: ");
            } else if (((c) > ZERO_ASCII) && ((c) < SIX_ASCII)) { //user has chosen a generated prediction 1-5 inclusive (comparing ASCII values)                
                String correctPred = predictions.get(c - ASCII_OFFSET);
                userHistory.add(correctPred);
                UHWriter.write(correctPred + "\n");
                temp = "";
                currLL = DLBTrie;

                System.out.println("\n");
                System.out.println("WORD COMPLETED: " + correctPred);
                System.out.print("\n");
                System.out.print("Enter first character of the next word: ");
            } else { //user entered alphabetical character
                predictions.clear();
                temp += c;

                long start1 = System.nanoTime(); //clock start
                predictions = userHistPred(userHistory, predictions, temp); //get predictions from user history
                long finish1 = System.nanoTime(); //clock stop
                diff1 = (double) (finish1 - start1);

                //if predictions list is not full and predictions are available in the dictionary trie, retrieve them
                if (currLL != null && currLL.getChild(c) != null && predictions.size() < 5) {
                    currLL = currLL.getChild(c);

                    long start2 = System.nanoTime(); //clock start
                    predictions = DLBTrie.getPredictions(currLL, temp, predictions, false); //get predictions from the dictionary DLB trie
                    long finish2 = System.nanoTime(); //clock stop
                    diff2 = (double) (finish2 - start2); //get the difference and cast it to a double
                    totalDiff = diff1 + diff2;
                    totalDiff = totalDiff / 1000000000; //convert from nanoseconds to seconds
                    predictionTimes.add(totalDiff);
                }
                if (predictions.size() > 0) { //if there are predictions to print
                    System.out.print("\n");
                    System.out.println("(" + String.format("%.6f", totalDiff) + " s)");
                    System.out.println("Predictions: ");
                    for (int k = 0; k <= predictions.size() - 1; k++) { //print predictions
                        System.out.print("(" + (k + 1) + ") " + predictions.get(k) + "   ");
                    }
                } else { //no predictions available for the current char from either list
                    currLL = null;
                    System.out.print("\n");
                    System.out.print("No predictions found");
                }
                System.out.println("\n");
                System.out.print("Enter the next character: ");
            }
            c = inScan.next().charAt(0);
        }
        System.out.println("\n");
        System.out.println("Average time: " + String.format("%.6f", average(predictionTimes)) + " s");
        System.out.println("Bye!");
        UHWriter.close(); //done writing to user history file
    }//end main

    private static double average(ArrayList<Double> times) { //computes average time to get predictions
        double sum = 0.0;
        for (int i = 0; i < times.size(); i++) {
            sum += times.get(i);
        }
        double avg = sum / times.size();
        if (avg > 0.0) {
            return avg;
        }
        return 0.0;
    }

    private static void fillDLBTrie(File file, MyLinkedList trie) throws FileNotFoundException {
        Scanner f = new Scanner(file);
        while (f.hasNextLine()) {
            String word = f.nextLine();
            addToDLBTrie(word, trie);
        }
    }

    private static void addToDLBTrie(String s, MyLinkedList trie) { //adds string to dictionary DLB trie
        MyLinkedList currLL = trie;
        char currChar = ' ';
        for (int i = 0; i <= s.length(); i++) {
            if (i < s.length()) { //still have chars to process
                currChar = s.charAt(i);
            }
            if (i == s.length()) { //reached the end of a word, insert ^ to denote
                currLL.add('^');
            } else { //process char
                int indexOfChar = currLL.findChar(currChar);
                if (indexOfChar == -1) { //current char not present in current list, add it
                    currLL.add(currChar);
                }
                if (currLL.getChild(currChar) == null) { //no child list created at current char, create one
                    currLL = currLL.branchDown(currLL.findChar(currChar));
                } else { //child list already created at current char, retrieve it
                    currLL = currLL.getChild(currChar);
                }
            }
        }
    }

    //gets all viable predictions from user history by checking prefix (including duplicates)
    private static ArrayList<String> userHistPred(ArrayList<String> userHist, ArrayList<String> AL, String prefix) {
        ArrayList<String> viablePred = new ArrayList<>();
        for (int i = 0; i < userHist.size(); i++) {
            if (userHist.get(i).startsWith(prefix)) {
                viablePred.add(userHist.get(i));
            }
        }
        AL = getMostFreq(viablePred, AL);
        return AL;
    }

    //fetches the most frequent words in the list and stores them in the predictions ArrayList to be returned
    //Note: can return ArrayList with anywhere from 0 to 5 elements in it
    //Note: if there are no duplicates in the list, words (up to 5) are added to the predictions ArrayList starting from the
    //end of viablePredictions, moving backwards through the list, meaning the most recently entered words by the user will 
    //appear first
    private static ArrayList<String> getMostFreq(ArrayList<String> viaPred, ArrayList<String> AL) {
        int[] freq = new int[viaPred.size()];
        for (int i = 0; i < viaPred.size(); i++) { //calculating the frequency of each word and storing them in a parallel array
            if (viaPred.indexOf(viaPred.get(i)) == i) { //first appear of a word
                freq[i] = 1;
            } else { //duplicate, increment index of first appearance
                freq[i] = -1;
                freq[viaPred.indexOf(viaPred.get(i))]++;
            }
        }
        while (!containsAllNegOnes(freq) && AL.size() < 5) { //gathering the words that appear most frequently
            int maxIndex = freq.length - 1;
            for (int p = freq.length - 1; p >= 0; p--) { //start at the end of the array to get the most recently entered words first
                if (freq[p] > freq[maxIndex]) {
                    maxIndex = p;
                }
            }
            AL.add(viaPred.get(maxIndex));
            freq[maxIndex] = -1; //word has already been added to predictions, do not add it again
        }
        return AL;
    }

    //only returns true if all the frequencies are = -1 meaning that all the possible unique words in
    //viablePredictions AL have been added to the predictions AL, thus we return and allow the dictionary
    //trie to fill in the rest of the predictions
    private static boolean containsAllNegOnes(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != -1) {
                return false;
            }
        }
        return true;
    }
}
