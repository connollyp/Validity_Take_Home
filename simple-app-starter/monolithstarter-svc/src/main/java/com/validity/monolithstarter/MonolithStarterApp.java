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
        d = newint[n+1][m+1];

        //Initalizes differences for the 0th column and row to the values of i and j
        for(int i = 0; i<= n; i++){
            d[i][0] = i;
        }
        for(int j = 0; j <= m; i++){
            d[0][j] = j;
        }

        //Compare each char and determine if they are different, store value of difference in the matrix
        for(int i = 1; i<= n; i++){

            si = s.charAt(i - 1);

            for(int j = 1; j <= m; j++){

                tj = t.charAt(j - 1);

                if(si == tj){
                    diff = 0;
                }else{
                    diff = 1;
                }

                d[i][j] = Min(d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1]+diff);
            }
        }
        return d[n][m];
    }


}
