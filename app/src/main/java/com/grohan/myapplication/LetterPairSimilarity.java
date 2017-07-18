package com.grohan.myapplication;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rg on 26-Jul-15.
 *  Dice's Coefficient
 */
public class LetterPairSimilarity {

    /** @return lexical similarity value in the range [0,1] */
    public static double compareStrings(String str1, String str2) {
        ArrayList pairs1 = wordLetterPairsUnique(str1.toUpperCase());
        ArrayList pairs2 = wordLetterPairsUnique(str2.toUpperCase());
        int intersection = 0;
        int union = pairs1.size() + pairs2.size();
        for (int i=0; i<pairs1.size(); i++) {
            Object pair1=pairs1.get(i);
            for(int j=0; j<pairs2.size(); j++) {
                Object pair2=pairs2.get(j);
                if (pair1.equals(pair2)) {
                    intersection++;
                    pairs2.remove(j);
                    break;
                }
            }
        }
        return (2.0*intersection)/union;
    }

    /** @return an ArrayList of 2-character Strings. */
    private static ArrayList wordLetterPairs(String str) {
        str = ((str.replaceAll("[-+.:,)(&]|\\[|\\]","")).trim());
        ArrayList allPairs = new ArrayList();
        // Tokenize the string and put the tokens/words into an array
        String[] words = str.split("\\s+");
        // For each word
        for (int w=0; w < words.length; w++) {
            // Find the pairs of characters
            String[] pairsInWord = letterPairs(words[w]);
            for (int p=0; p < pairsInWord.length; p++) {
                allPairs.add(pairsInWord[p]);
            }
        }
        return allPairs;
    }

    /** @return an ArrayList of 2-character Strings.
     *  Additionally removed repeated words in a string
     * */
    private static ArrayList wordLetterPairsUnique(String str) {
        str = ((str.replaceAll("[-+.:,)(&]|\\[|\\]","")).trim());
        ArrayList allPairs = new ArrayList();
        // Tokenize the string and put the tokens/words into an array
        String[] wordsWithDuplicates = str.split("\\s+");

        Set<String> wordset = new HashSet<String>(Arrays.asList(wordsWithDuplicates));
        String[] words = wordset.toArray(new String[0]);

        //Log.d("LPS::lettersPairs", "words duplicates  : " + ArrayUtils.toString(wordsWithDuplicates));
        //Log.d("LPS::lettersPairs", "words unique  : " + ArrayUtils.toString(words));

        // For each word
        for (int w=0; w < words.length; w++) {
            // Find the pairs of characters
            String[] pairsInWord = letterPairs(words[w]);
            for (int p=0; p < pairsInWord.length; p++) {
                allPairs.add(pairsInWord[p]);
            }
        }
        return allPairs;
    }


    /** @return an array of adjacent letter pairs contained in the input string */
    private static String[] letterPairs(String str) {
        //Log.d("LPS::lettersPairs", "pairing letter pairs for word : " + str);
        int numPairs = str.length()-1;
        String[] pairs = new String[numPairs];
        for (int i=0; i<numPairs; i++) {
            pairs[i] = str.substring(i,i+2);
        }
        return pairs;
    }

}
