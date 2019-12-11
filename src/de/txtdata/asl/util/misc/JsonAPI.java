package de.txtdata.asl.util.misc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class JsonAPI {

    public boolean quiet = true;
    public String charset = "UTF-8";

    public JsonAPI(){
        Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"));

        for(String log:loggers) {
            Logger logger = (Logger) LoggerFactory.getLogger(log);
            logger.setLevel(Level.INFO);
            logger.setAdditive(false);
        }
    }

    public String get(String uri){
        return get("GET", uri, null, null);
    }

    public String get(String type, String uri, String contentType, String body){
        try{
            HttpResponse response = getHttpResponse(type, uri, contentType, body);
            if (response==null) return null;
            if(response.getStatusLine().getStatusCode() < 300) {
                String responseString= EntityUtils.toString(response.getEntity(), this.charset);
                return responseString;
            }else {
                int code = response.getStatusLine().getStatusCode();
                if (!quiet){
                    System.out.println("\t#JsonAPI Error: "+code+" "+response.getStatusLine().getReasonPhrase()+" / "+EntityUtils.toString(response.getEntity()));
                }
                return "Code "+code;
            }
        }catch(Exception e){
            System.out.println("\t#JsonAPI Error: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponse getHttpResponse(String type, String uri, String contentType, String body){
        try{
            HttpClient client = HttpClientBuilder.create().build();
            HttpRequestBase request = null;
            if (type.equalsIgnoreCase("GET")){
                request = new HttpGet(uri);
            }else if (type.equalsIgnoreCase("POST")){
                HttpPost post = new HttpPost(uri);
                if (body!=null){
                    StringEntity se = new StringEntity(body, this.charset);
                    post.setEntity(se);
                }
                request = post;
            }else if (type.equalsIgnoreCase("PUT")){
                HttpPut put = new HttpPut(uri);
                if (body!=null){
                    StringEntity se = new StringEntity(body, this.charset);
                    put.setEntity(se);
                }
                request = put;
            }
            if (contentType!=null) {
                request.setHeader("Content-Type", contentType);
            }
            HttpResponse response = client.execute(request);
            return response;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String getContentAsString(HttpResponse response){
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("*** "+inputLine);
                result=result+inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String encode(String uri, String name, String value){
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(name, value));
        String encoded = URLEncodedUtils.format(params, this.charset);
        if (!uri.contains("?")) {
            return uri + "?" + encoded;
        }else{
            return uri + "&" + encoded;
        }
    }

    public String encode(String name, String value){
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(name, value));
        String encoded = URLEncodedUtils.format(params, this.charset);
        return encoded;
    }

}
