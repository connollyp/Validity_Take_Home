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
        detectDuplicates("/Users/patrickconnolly/Documents/Job Search/Validity_Take_Home/simple-app-starter/test-files/normal.csv");
        createJson("/Users/patrickconnolly/Documents/Job Search/Validity_Take_Home/simple-app-starter/test-files/normal.csv");
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

    //Generic implementation of the Levenshtein algorithm to find the difference between two strings
    // Based off of this implementation https://people.cs.pitt.edu/~kirk/cs1501/Pruhs/Spring2006/assignments/editdistance/Levenshtein%20Distance.htm
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


    //Gets the conetents of the specified file and returns it as a string
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
                    System.out.printf("%d is duplicates with %d\n", i+1, j+1);
                }else{
                    duplicates[i][0] = -1;
                }
            }
        }

        return duplicates;
    }

    public static JSONObject createJson(String filepath){
        JSONObject json = new JSONObject();

        json.put("test1", "value1");

        String test = json.toString();

        System.out.println(test);

        return json;
    }


}
