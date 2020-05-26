package odlgui.elasticsearch;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import odlgui.backend.model.History;
import odlgui.backend.objects.ESDocument;
import odlgui.backend.objects.Query;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Main function of this class is to maintain connection to elasticsearch, send and receive data.
 */

public class ElasticClient {
    private String metadata_index;
    private String hostname;
    private int port;
    private String protocol;
    private String username;
    private String password;
    private RestHighLevelClient client = null;
    private static ElasticClient elasticClient = null;
    public static ElasticClient getInstance(){
        if(elasticClient==null){
            elasticClient = new ElasticClient();
        }
        return  elasticClient;
    }
    private ElasticClient(){
        setToDefault();
        createElasticSearchClient();
    }

    public String getMetadata_index() {
        return metadata_index;
    }

    public void setMetadata_index(String metadata_index) {
        this.metadata_index = metadata_index;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RestHighLevelClient getClient() {
        return client;
    }



    public boolean pingElasticSearch(){
        try{
            if(client.ping(RequestOptions.DEFAULT)){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    /**
     * Creates client for communication to elasticsearch
     * @param password
     * @param username
     * @param hostname
     * @param protocol
     * @param port
     * @return RestHighLevelClient created from provided values, if creation encounter problem, function returns null
     */
    public RestHighLevelClient createElasticsearchClient(String password, String username, String hostname, String protocol, int port){
        RestHighLevelClient tmpClient = null;
        try {
            RestClientBuilder builder = null;
            if (password.isEmpty() || username.isEmpty()) {
                builder = RestClient.builder(new HttpHost(hostname, port, protocol));
            } else {
                final CredentialsProvider credentialsProvider =
                        new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(username, password));

                builder = RestClient.builder(
                        new HttpHost(hostname, port, protocol))
                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                            @Override
                            public HttpAsyncClientBuilder customizeHttpClient(
                                    HttpAsyncClientBuilder httpClientBuilder) {
                                return httpClientBuilder
                                        .setDefaultCredentialsProvider(credentialsProvider);
                            }
                        });
            }
            tmpClient = new RestHighLevelClient(builder);
        }catch (Exception e){
            return tmpClient;
        }
        return tmpClient;
    }

    /**
     * creates elastic client for this instance of class
     * @return true if created, false if not
     */
    public boolean createElasticSearchClient() {
        try {
            RestClientBuilder builder = null;
            if (password.isEmpty() || username.isEmpty()) {
                builder = RestClient.builder(new HttpHost(hostname, port, protocol));
            } else {
                final CredentialsProvider credentialsProvider =
                        new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(username, password));

                builder = RestClient.builder(
                        new HttpHost(hostname, port, protocol))
                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                            @Override
                            public HttpAsyncClientBuilder customizeHttpClient(
                                    HttpAsyncClientBuilder httpClientBuilder) {
                                return httpClientBuilder
                                        .setDefaultCredentialsProvider(credentialsProvider);
                            }
                        });
            }
            client = new RestHighLevelClient(builder);
        }catch (Exception e){
            return false;
        }
        return true;

    }

    public String toString(){
        return "ElasticClient: \nusername: "+username+"\npassword: "+password+"\nprotocol: "+protocol+"\nhostname: "+hostname+"\nport: "+port+"\n";
    }

    /**
     * function change settings back to default
     */
    public void setToDefault(){
        username = "";
        password = "";
        protocol = "http";
        hostname = "localhost";
        metadata_index = "test";
        port = 9200;
    }

    /**
     *
     * @param restMethod REST method for elasticsearch
     * @param endPoint endpoint path
     * @param requestBody body of request for elasticsearch
     * @return elasticsearch response in String representation
     */
    public String doRequest(String restMethod, String endPoint, String requestBody) {
        if(pingElasticSearch()){
            try {
                Request request = new Request(restMethod,endPoint);
                request.setEntity(new NStringEntity(requestBody,ContentType.APPLICATION_JSON));
                Response response = client.getLowLevelClient().performRequest(request);
                return EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                return e.getMessage();
            }
        }else{
            return "Could not regain connection to elasticsearch, check settings.";
        }
    }

    /**
     * function returns non build in indices (indices which starts with dot)
     * @return non build in indices (indices which starts with dot)
     */
    public ArrayList<String> getIndices(){
        ArrayList<String> arr = new ArrayList<String>();
        try {
            Request request = new Request("GET","/_cat/indices");
            Response response = client.getLowLevelClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity()).replaceAll("\t"," ").replaceAll("  "," ");
            for(String tmp : responseBody.split("\n")){
                String[] tmparr = tmp.split(" ");
                if(tmparr.length>2){
                    if(!tmparr[2].contains(".")) {
                        arr.add(tmparr[2]);
                    }
                }
            }
            return arr;
        } catch (Exception e) {
            return arr;
        }
    }

    /**
     * Perfom call on elasticsearch metadata extractor plugin
     * @param path path which will be specified for metadata extractor plugin
     * @param filename filename of extracted document
     * @return response code
     */
    public int extractMetadataRequest(String path, String filename) {
        String serverName = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getServerName();
        int serverPort = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getServerPort();
        String protocol = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getProtocol();
        if(protocol.toLowerCase().contains("https")){
            protocol = "https";
        }else{
            protocol = "http";
        }
        String url = protocol + "://" + serverName + ":" +serverPort +  path;

        Request request = new Request("PUT","/_extract_metadata");
        request.setEntity(new NStringEntity("{\"index\":\""+metadata_index+"\",\"filename\":\""+filename+"\",\"path\":\""+url+"\",\"_id\":\""+filename.replaceAll("[^.a-zA-Z0-9_-]","")+"\"}",ContentType.APPLICATION_JSON));
        try {
            Response response = client.getLowLevelClient().performRequest(request);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    /**
     * function lists all available fields for metadata_index
     * @return listed fields
     */
    public Set<String> getFields() {
        Set<String> fields = new HashSet<>();
        Request request = new Request("GET","/"+metadata_index+"/_mapping");
        try {
            Response response = client.getLowLevelClient().performRequest(request);
            JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
            Map<String, Object> flattenedJsonMap = null;
            if(obj.getJSONObject(metadata_index).getJSONObject("mappings").has("properties")){
                flattenedJsonMap = JsonFlattener.flattenAsMap(obj.getJSONObject(metadata_index).getJSONObject("mappings").getJSONObject("properties").toString());

            }else{
                for(String key : obj.getJSONObject(metadata_index).getJSONObject("mappings").keySet()){
                    flattenedJsonMap = JsonFlattener.flattenAsMap(obj.getJSONObject(metadata_index).getJSONObject("mappings").getJSONObject(key).getJSONObject("properties").toString());
                }
            }
            flattenedJsonMap.forEach((k,v)->{
                String tmp = k.substring(0,k.lastIndexOf("."));
                fields.add(tmp.replaceAll(".properties.",".").replaceAll(".fields.","."));
            });
        } catch (IOException e) {
        }
        return fields;
    }

    /**
     * Creates and performs search query on elasticsearch
     * @param id user_session id
     * @return top 10 searched documents from elasticsearch
     */
    public ArrayList<ESDocument> perfomSearch(String id) {
        ArrayList<ESDocument> results = new ArrayList<>();
        JSONObject query_j = new JSONObject();
        JSONObject bool_j = new JSONObject();
        JSONArray must_j = new JSONArray();
        JSONArray should_j = new JSONArray();
        JSONObject final_query = new JSONObject();

        for(Query query : History.getInstance().getLayouts().get(id).values()){
            JSONObject mm_j = new JSONObject();
            JSONObject mm_j_info = new JSONObject();
            mm_j_info.put("query",query.getVal());
            JSONArray fields_j = new JSONArray();
            fields_j.put(query.getField()+"^"+query.getFactor());
            mm_j_info.put("fields",fields_j);
            mm_j.put("multi_match",mm_j_info);
            if(query.getOperand().equals("or")){
                should_j.put(mm_j);
            }else{
                must_j.put(mm_j);
            }
        }
        if(!must_j.isEmpty()){
            bool_j.put("must",must_j);
        }
        if(!should_j.isEmpty()){
            bool_j.put("should",should_j);
        }
        query_j.put("bool",bool_j);
        final_query.put("query",query_j);
        Request request = new Request("POST","/"+metadata_index+"/_search?");
        request.setEntity(new NStringEntity(final_query.toString(),ContentType.APPLICATION_JSON));
        try {
            Response response = client.getLowLevelClient().performRequest(request);
            JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
            JSONArray hits = obj.getJSONObject("hits").getJSONArray("hits");
            for(int i=0;i<hits.length();i++){
                JSONObject doc = (JSONObject) hits.get(i);
                ESDocument esDocument = new ESDocument();
                esDocument.setDoc_id(doc.getString("_id"));
                if(doc.getJSONObject("_source").has("filename")){
                    esDocument.setFilename(doc.getJSONObject("_source").getString("filename"));
                }
                esDocument.setScore(doc.getDouble("_score"));
                esDocument.setSource_fields(doc.getJSONObject("_source"));
                esDocument.setRaw_data(doc);
                results.add(esDocument);
            }
        } catch (IOException e) {
        }
        return results;
    }
}
