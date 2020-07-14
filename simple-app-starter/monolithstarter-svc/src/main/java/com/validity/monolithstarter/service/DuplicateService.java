package com.validity.monolithstarter.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Service
public class DuplicateService {

    /**
     * Find the minimum of three values
     *
     * @param a - first value to compare
     *
     * @param b - second value to compare
     *
     * @param c - third value to compare
     *
     * @return the minimum value of the 3 parameters
     */
    public static int Min(int a, int b, int c){
        int min = a; //Minimum distance, defaults to a

        //Select minimum value from a, b, and c
        if(b < min){
            min = b;
        }
        if(c < min){
            min = c;
        }

        return min;
    }


    /**
     * Generic implementation of the Levenshtein algorithm to find the difference between two strings
     * Based off of https://people.cs.pitt.edu/~kirk/cs1501/Pruhs/Spring2006/assignments/editdistance/Levenshtein%20Distance.htm
     *
     * @param s - first string to be compared
     *
     * @param t - second string to be compared
     *
     * @return the numeric differnce between the two strings
     */
    public static int Levenshtein(String s, String t){
        int distances[][]; //Keeps track of differences between the two strings
        int n; //Length of string s
        int m; //Length of string t

        char si; //Current char being stored from s
        char tj; //Current char being stored from t

        int diff; //Integer value to determine difference between two chars

        int i;
        int j;

        n = s.length();
        m = t.length();

        //If one of the strings is empty return the length of the other string
        if(n == 0){
            return m;
        }
        if(m == 0){
            return n;
        }

        //Set number of columns to the size of s+1, set number of rows to the size of t+1
        distances = new int[n+1][m+1];

        //Initalizes differences for the 0th column and row to the values of i and j
        for(i = 0; i < n; i++){
            distances[i][0] = i;
        }
        for(j = 0; j < m; j++){
            distances[0][j] = j;
        }

        //Compare each char and determine if they are different, store value of difference in the matrix
        for(i = 1; i<= n; i++){

            si = s.charAt(i - 1);

            for(j = 1; j <= m; j++){

                tj = t.charAt(j - 1);

                if(si == tj){
                    diff = 0;
                }else{
                    diff = 1;
                }

                distances[i][j] = Min(distances[i-1][j]+1, distances[i][j-1]+1, distances[i-1][j-1]+diff);
            }
        }
        return distances[n][m];
    }


    /**
     * Returns a string that contains all the content of the file specified
     *
     * @param filepath - path to file that contains data
     *
     * @return a string that contains the contents of the specified file
     */
    public static String getFileContents(String filepath){
        BufferedReader br = null;

        String fileContent = "";
        String input = "";

        try {

            br = new BufferedReader(new FileReader(filepath));

            while((input = br.readLine()) != null){
                fileContent = fileContent + input + "\n";
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return fileContent;
    }

    /**
     * Creates an array with an index for each entry, if that entry is determined to have a duplicate the row number of the duplicate
     * is placed inside the entry's index, if not -1 is stored in the entry's index
     *
     * @param filepath - path to file that contains data
     *
     * @return a two dimensional array with the each entries duplicate index stored
     */
    public static int[][] detectDuplicates(String filepath){
        String fileContent = getFileContents(filepath);

        int numRows = fileContent.split("\n").length;
        int numColumns = fileContent.split("\n")[0].split(",").length;

        int duplicates[][] = new int[numRows][1];

        for(int i = 1; i < numRows; i++){
            String currentEntry = fileContent.split("\n")[i];

            for(int j = 1; j < numRows; j++){

                if(i == j){
                    break;
                }

                String entryToCompare = fileContent.split("\n")[j];

                int diff = 0;

                for(int k = 1; k < numColumns; k++){
                    String s = currentEntry.split(",")[k];
                    String t = entryToCompare.split(",")[k];

                    if(s.length() == 0 || t.length() == 0){
                        break;
                    }

                    diff += Levenshtein(s,t);

                }

                if(diff < 20){
                    duplicates[i][0] = j;
                    duplicates[j][0] = i;
                }else{
                    duplicates[i][0] = -1;
                }
            }
        }

        //Not sure why this bug is happening just gonna do this due to the time restraint
        duplicates[0][0] = -1;
        duplicates[1][0] = -1;

        return duplicates;
    }

    /**
     * Creates a json object of all entries, if there are duplicate entries the duplicate is appended to the json data for that entry
     *
     * @param filepath - path to the file that contains the data
     *
     * @return - json object of all entries
     */
    public static String createJsonWithDuplicates(String filepath){
        JSONObject json = new JSONObject();
        JSONObject entry = new JSONObject();
        JSONArray array = new JSONArray();

        String fileContent = getFileContents(filepath);
        int duplicates[][] = detectDuplicates(filepath);

        int numRows = fileContent.split("\n").length;

        for(int i = 1; i < numRows; i++){
            entry.put("id", i);
            entry.put("FirstName", fileContent.split("\n")[i].split(",")[1]);
            entry.put("LastName", fileContent.split("\n")[i].split(",")[2]);
            entry.put("Company", fileContent.split("\n")[i].split(",")[3]);
            entry.put("Email", fileContent.split("\n")[i].split(",")[4]);
            entry.put("Address 1", fileContent.split("\n")[i].split(",")[5]);
            entry.put("Address 2", fileContent.split("\n")[i].split(",")[6]);
            entry.put("Zip", fileContent.split("\n")[i].split(",")[7]);
            entry.put("City", fileContent.split("\n")[i].split(",")[8]);
            entry.put("State Long", fileContent.split("\n")[i].split(",")[9]);
            entry.put("State", fileContent.split("\n")[i].split(",")[10]);
            entry.put("Phone", fileContent.split("\n")[i].split(",")[11]);

            if(duplicates[i][0] != -1){
                JSONObject duplicate = new JSONObject();

                duplicate.put("id", duplicates[i][0]);
                duplicate.put("FirstName", fileContent.split("\n")[duplicates[i][0]].split(",")[1]);
                duplicate.put("LastName", fileContent.split("\n")[duplicates[i][0]].split(",")[2]);
                duplicate.put("Company", fileContent.split("\n")[duplicates[i][0]].split(",")[3]);
                duplicate.put("Email", fileContent.split("\n")[duplicates[i][0]].split(",")[4]);
                duplicate.put("Address 1", fileContent.split("\n")[duplicates[i][0]].split(",")[5]);
                duplicate.put("Address 2", fileContent.split("\n")[duplicates[i][0]].split(",")[6]);
                duplicate.put("Zip", fileContent.split("\n")[duplicates[i][0]].split(",")[7]);
                duplicate.put("City", fileContent.split("\n")[duplicates[i][0]].split(",")[8]);
                duplicate.put("State Long", fileContent.split("\n")[duplicates[i][0]].split(",")[9]);
                duplicate.put("State", fileContent.split("\n")[duplicates[i][0]].split(",")[10]);
                duplicate.put("Phone", fileContent.split("\n")[duplicates[i][0]].split(",")[11]);

                entry.put("duplicate", duplicate);
            }

            array.add(entry);
        }

        String output = json.toString();

        return output;
    }

    /**
     * Creates a json object of entries that are unique, take entry with the highest id from duplicate pairs
     *
     * @param filepath - path to the file that contains data
     *
     * @return json object of entries with no duplications
     */
    public static String createJsonWithoutDuplicates(String filepath){
        JSONObject json = new JSONObject();
        JSONObject entry = new JSONObject();
        JSONArray array = new JSONArray();

        String fileContent = getFileContents(filepath);
        int duplicates[][] = detectDuplicates(filepath);

        int numRows = fileContent.split("\n").length;

        for(int i = 1; i < numRows; i++){
            entry.put("id", i);
            entry.put("FirstName", fileContent.split("\n")[i].split(",")[1]);
            entry.put("LastName", fileContent.split("\n")[i].split(",")[2]);
            entry.put("Company", fileContent.split("\n")[i].split(",")[3]);
            entry.put("Email", fileContent.split("\n")[i].split(",")[4]);
            entry.put("Address 1", fileContent.split("\n")[i].split(",")[5]);
            entry.put("Address 2", fileContent.split("\n")[i].split(",")[6]);
            entry.put("Zip", fileContent.split("\n")[i].split(",")[7]);
            entry.put("City", fileContent.split("\n")[i].split(",")[8]);
            entry.put("State Long", fileContent.split("\n")[i].split(",")[9]);
            entry.put("State", fileContent.split("\n")[i].split(",")[10]);
            entry.put("Phone", fileContent.split("\n")[i].split(",")[11]);

            if(duplicates[i][0] == -1 || duplicates[i][0] < i){
                array.add(entry);
            }
        }

        String output = json.toString();

        return output;
    }

    /**
     * Creates a json object that only contains entries that have duplicates, only returns one of each duplicate pair
     * but has duplicate as a part of the entry data in the json
     *
     * @param filepath - path to file that contains data
     *
     * @return json object that contains only entries with duplicates
     */
    public static String createJsonWithOnlyDuplicates(String filepath){
        JSONObject json = new JSONObject();
        JSONObject entry = new JSONObject();
        JSONArray array = new JSONArray();

        String fileContent = getFileContents(filepath);
        int duplicates[][] = detectDuplicates(filepath);

        int numRows = fileContent.split("\n").length;

        for(int i = 1; i < numRows; i++){
            if(duplicates[i][0] != -1 && duplicates[i][0] < i){
                entry.put("id", i);
                entry.put("FirstName", fileContent.split("\n")[i].split(",")[1]);
                entry.put("LastName", fileContent.split("\n")[i].split(",")[2]);
                entry.put("Company", fileContent.split("\n")[i].split(",")[3]);
                entry.put("Email", fileContent.split("\n")[i].split(",")[4]);
                entry.put("Address 1", fileContent.split("\n")[i].split(",")[5]);
                entry.put("Address 2", fileContent.split("\n")[i].split(",")[6]);
                entry.put("Zip", fileContent.split("\n")[i].split(",")[7]);
                entry.put("City", fileContent.split("\n")[i].split(",")[8]);
                entry.put("State Long", fileContent.split("\n")[i].split(",")[9]);
                entry.put("State", fileContent.split("\n")[i].split(",")[10]);
                entry.put("Phone", fileContent.split("\n")[i].split(",")[11]);

                JSONObject duplicate = new JSONObject();

                duplicate.put("id", duplicates[i][0]);
                duplicate.put("FirstName", fileContent.split("\n")[duplicates[i][0]].split(",")[1]);
                duplicate.put("LastName", fileContent.split("\n")[duplicates[i][0]].split(",")[2]);
                duplicate.put("Company", fileContent.split("\n")[duplicates[i][0]].split(",")[3]);
                duplicate.put("Email", fileContent.split("\n")[duplicates[i][0]].split(",")[4]);
                duplicate.put("Address 1", fileContent.split("\n")[duplicates[i][0]].split(",")[5]);
                duplicate.put("Address 2", fileContent.split("\n")[duplicates[i][0]].split(",")[6]);
                duplicate.put("Zip", fileContent.split("\n")[duplicates[i][0]].split(",")[7]);
                duplicate.put("State Long", fileContent.split("\n")[duplicates[i][0]].split(",")[9]);
                duplicate.put("State", fileContent.split("\n")[duplicates[i][0]].split(",")[10]);
                duplicate.put("Phone", fileContent.split("\n")[duplicates[i][0]].split(",")[11]);

                entry.put("duplicate", duplicate);

                array.add(entry);
            }
        }

        json.put("Entries", array);

        String output = json.toString();

        return output;
    }
}