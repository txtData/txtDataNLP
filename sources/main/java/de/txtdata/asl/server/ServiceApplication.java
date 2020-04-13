package de.txtdata.asl.server;

import de.txtdata.asl.examples.ChunkerPipeline;
import de.txtdata.asl.nlp.models.Language;
import de.txtdata.asl.server.endpoints.api.APIService;
import de.txtdata.asl.server.endpoints.html.HTMLDemo;
import de.txtdata.asl.server.endpoints.opennlp.ServiceOpenNLP;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class ServiceApplication extends Application<ServiceConfiguration> {

    private static boolean production = false;
    private static boolean offline = true;

    public static ChunkerPipeline chunker;

    public static void main(String[] args) throws Exception {
        if (args.length==0){
            String[] defaultArgs = {"server", "config.yml"};
            new ServiceApplication().run(defaultArgs);
        }else {
            new ServiceApplication().run(args);
        }
    }

    @Override
    public String getName() {
        return "NLPAPI";
    }

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap){
        ServiceApplication.chunker = new ChunkerPipeline(Language.ENGLISH);
        ServiceApplication.chunker.initialize();
    }

    @Override
    public void run(ServiceConfiguration configuration,
                    Environment environment) {

        final APIService entityAPI = new APIService();
        environment.jersey().register(entityAPI);

        final HTMLDemo demo = new HTMLDemo();
        environment.jersey().register(demo);

        final ServiceOpenNLP openNLP = new ServiceOpenNLP();
        environment.jersey().register(openNLP);

        final ServiceHealthCheck healthCheck = new ServiceHealthCheck();
        environment.healthChecks().register("default", healthCheck);


        if ("true".equalsIgnoreCase(configuration.getOfflineMode())){
            this.offline = true;
        }else{
            this.offline = false;
        }
        if (offline){
            //this.keywordIndexingPipeline.setLocalMode(offline);
        }

        if (production){
            System.out.println("\nNLP API Ready.");
            redirectSystemOut();
        }
    }

    public void redirectSystemOut(){
        try {
            final PrintStream original = System.out;
            final PrintStream logSteam = new PrintStream(new FileOutputStream(".//log.log", true)){
                public void print(String s){
                    //do nothing
                }
                public void println(String s){
                    //do nothing
                }
                public void println(){
                    //do nothing
                }
            };
            System.setOut(logSteam);
            System.setErr(new PrintStream(new OutputStream() {
                public void write(int b) {
                    logSteam.write(b);
                }
            }));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
