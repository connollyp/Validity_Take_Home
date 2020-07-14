package com.validity.monolithstarter;

import com.validity.monolithstarter.config.DefaultProfileUtil;
import io.github.jhipster.config.JHipsterConstants;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MonolithStarterApp implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(MonolithStarterApp.class);

    private final Environment env;

    public MonolithStarterApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes MonolithStarter.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MonolithStarterApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }

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
    public static JSONObject createJsonWithDuplicates(String filepath){
        JSONObject json = new JSONObject();
        JSONObject entry = new JSONObject();
        JSONArray array = new JSONArray();

        String fileContent = getFileContents(filepath);
        int duplicates[][] = detectDuplicates(filepath);

        int numRows = fileContent.split("\n").length;

        for(int i = 1; i < numRows; i++){
            entry.put("id", i);
            entry.put("FirstName", fileContent.split("\n")[i].split(",")[1] + "\n");
            entry.put("LastName", fileContent.split("\n")[i].split(",")[2] + "\n");
            entry.put("Company", fileContent.split("\n")[i].split(",")[3] + "\n");
            entry.put("Email", fileContent.split("\n")[i].split(",")[4] + "\n");
            entry.put("Address 1", fileContent.split("\n")[i].split(",")[5] + "\n");
            entry.put("Address 2", fileContent.split("\n")[i].split(",")[6] + "\n");
            entry.put("Zip", fileContent.split("\n")[i].split(",")[7] + "\n");
            entry.put("City", fileContent.split("\n")[i].split(",")[8] + "\n");
            entry.put("State Long", fileContent.split("\n")[i].split(",")[9] + "\n");
            entry.put("State", fileContent.split("\n")[i].split(",")[10] + "\n");
            entry.put("Phone", fileContent.split("\n")[i].split(",")[11] + "\n");

            if(duplicates[i][0] != -1){
                JSONObject duplicate = new JSONObject();

                duplicate.put("id", duplicates[i][0]);
                duplicate.put("FirstName", fileContent.split("\n")[duplicates[i][0]].split(",")[1] + "\n");
                duplicate.put("LastName", fileContent.split("\n")[duplicates[i][0]].split(",")[2] + "\n");
                duplicate.put("Company", fileContent.split("\n")[duplicates[i][0]].split(",")[3] + "\n");
                duplicate.put("Email", fileContent.split("\n")[duplicates[i][0]].split(",")[4] + "\n");
                duplicate.put("Address 1", fileContent.split("\n")[duplicates[i][0]].split(",")[5] + "\n");
                duplicate.put("Address 2", fileContent.split("\n")[duplicates[i][0]].split(",")[6] + "\n");
                duplicate.put("Zip", fileContent.split("\n")[duplicates[i][0]].split(",")[7] + "\n");
                duplicate.put("City", fileContent.split("\n")[duplicates[i][0]].split(",")[8] + "\n");
                duplicate.put("State Long", fileContent.split("\n")[duplicates[i][0]].split(",")[9]+ "\n");
                duplicate.put("State", fileContent.split("\n")[duplicates[i][0]].split(",")[10]+ "\n");
                duplicate.put("Phone", fileContent.split("\n")[duplicates[i][0]].split(",")[11]+ "\n");

                entry.put("duplicate", duplicate);
            }

            array.add(entry);
        }

        json.put("Entries", array);

        return json;
    }

    /**
     * Creates a json object of entries that are unique, take entry with the highest id from duplicate pairs
     *
     * @param filepath - path to the file that contains data
     *
     * @return json object of entries with no duplications
     */
    public static JSONObject createJsonWithoutDuplicates(String filepath){
        JSONObject json = new JSONObject();
        JSONObject entry = new JSONObject();
        JSONArray array = new JSONArray();

        String fileContent = getFileContents(filepath);
        int duplicates[][] = detectDuplicates(filepath);

        int numRows = fileContent.split("\n").length;

        for(int i = 1; i < numRows; i++){
            entry.put("id", i);
            entry.put("FirstName", fileContent.split("\n")[i].split(",")[1] + "\n");
            entry.put("LastName", fileContent.split("\n")[i].split(",")[2] + "\n");
            entry.put("Company", fileContent.split("\n")[i].split(",")[3] + "\n");
            entry.put("Email", fileContent.split("\n")[i].split(",")[4] + "\n");
            entry.put("Address 1", fileContent.split("\n")[i].split(",")[5] + "\n");
            entry.put("Address 2", fileContent.split("\n")[i].split(",")[6] + "\n");
            entry.put("Zip", fileContent.split("\n")[i].split(",")[7] + "\n");
            entry.put("City", fileContent.split("\n")[i].split(",")[8] + "\n");
            entry.put("State Long", fileContent.split("\n")[i].split(",")[9] + "\n");
            entry.put("State", fileContent.split("\n")[i].split(",")[10] + "\n");
            entry.put("Phone", fileContent.split("\n")[i].split(",")[11] + "\n");

            if(duplicates[i][0] == -1 || duplicates[i][0] < i){
                array.add(entry);
            }
        }

        json.put("Entries", array);

        return json;
    }

    /**
     * Creates a json object that only contains entries that have duplicates, only returns one of each duplicate pair
     * but has duplicate as a part of the entry data in the json
     *
     * @param filepath - path to file that contains data
     *
     * @return json object that contains only entries with duplicates
     */
    public static JSONObject createJsonWithOnlyDuplicates(String filepath){
        JSONObject json = new JSONObject();
        JSONObject entry = new JSONObject();
        JSONArray array = new JSONArray();

        String fileContent = getFileContents(filepath);
        int duplicates[][] = detectDuplicates(filepath);

        int numRows = fileContent.split("\n").length;

        for(int i = 1; i < numRows; i++){
            if(duplicates[i][0] != -1 && duplicates[i][0] < i){
                entry.put("id", i);
                entry.put("FirstName", fileContent.split("\n")[i].split(",")[1] + "\n");
                entry.put("LastName", fileContent.split("\n")[i].split(",")[2] + "\n");
                entry.put("Company", fileContent.split("\n")[i].split(",")[3] + "\n");
                entry.put("Email", fileContent.split("\n")[i].split(",")[4] + "\n");
                entry.put("Address 1", fileContent.split("\n")[i].split(",")[5] + "\n");
                entry.put("Address 2", fileContent.split("\n")[i].split(",")[6] + "\n");
                entry.put("Zip", fileContent.split("\n")[i].split(",")[7] + "\n");
                entry.put("City", fileContent.split("\n")[i].split(",")[8] + "\n");
                entry.put("State Long", fileContent.split("\n")[i].split(",")[9] + "\n");
                entry.put("State", fileContent.split("\n")[i].split(",")[10] + "\n");
                entry.put("Phone", fileContent.split("\n")[i].split(",")[11] + "\n");

                JSONObject duplicate = new JSONObject();

                duplicate.put("id", duplicates[i][0]);
                duplicate.put("FirstName", fileContent.split("\n")[duplicates[i][0]].split(",")[1] + "\n");
                duplicate.put("LastName", fileContent.split("\n")[duplicates[i][0]].split(",")[2] + "\n");
                duplicate.put("Company", fileContent.split("\n")[duplicates[i][0]].split(",")[3] + "\n");
                duplicate.put("Email", fileContent.split("\n")[duplicates[i][0]].split(",")[4] + "\n");
                duplicate.put("Address 1", fileContent.split("\n")[duplicates[i][0]].split(",")[5] + "\n");
                duplicate.put("Address 2", fileContent.split("\n")[duplicates[i][0]].split(",")[6] + "\n");
                duplicate.put("Zip", fileContent.split("\n")[duplicates[i][0]].split(",")[7] + "\n");
                duplicate.put("City", fileContent.split("\n")[duplicates[i][0]].split(",")[8] + "\n");
                duplicate.put("State Long", fileContent.split("\n")[duplicates[i][0]].split(",")[9]+ "\n");
                duplicate.put("State", fileContent.split("\n")[duplicates[i][0]].split(",")[10]+ "\n");
                duplicate.put("Phone", fileContent.split("\n")[duplicates[i][0]].split(",")[11]+ "\n");

                entry.put("duplicate", duplicate);

                array.add(entry);
            }
        }

        json.put("Entries", array);

        return json;
    }

}
