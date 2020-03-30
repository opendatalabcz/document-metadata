package org.elasticsearch.plugin.extractor;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetadataExtractorTest {

    @Test
    void testNotValidJSONContent() throws IOException {
        HttpPost post = new HttpPost("http://localhost:9200/_extract_metadata");
        StringEntity no_content = new StringEntity("", ContentType.APPLICATION_JSON);
        StringEntity not_valid_json = new StringEntity("\"index\":\"test\",\"path\":\"file:///adr/file.pdf\"", ContentType.APPLICATION_JSON);
        StringEntity missing_field = new StringEntity("{\"path\":\"file:///adr/file.pdf\"}", ContentType.APPLICATION_JSON);
        StringEntity not_filled_field = new StringEntity("{\"index\":\"\",\"path\":\"file:///adr/file.pdf\"}", ContentType.APPLICATION_JSON);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response;

        post.setEntity(no_content);
        response = httpClient.execute(post);
        assertEquals(400,response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("MISSING REQUEST CONTENT"));

        post.setEntity(not_valid_json);
        response = httpClient.execute(post);
        assertEquals(400,response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("FAILED PARSING JSON INPUT"));

        post.setEntity(missing_field);
        response = httpClient.execute(post);
        assertEquals(400,response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("MISSING MANDATORY FIELD (index,path) IN CONTENT"));

        post.setEntity(not_filled_field);
        response = httpClient.execute(post);
        assertEquals(400,response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("MANDATORY FIELD (index,path) NOT FILLED"));
        httpClient.close();
    }

    @Test
    void testNotValidFile() throws IOException {
        HttpPost post = new HttpPost("http://localhost:9200/_extract_metadata");
        StringEntity not_valid_extention = new StringEntity("{\"index\":\"test\",\"path\":\"file:///adr/file.pdfnot\"}", ContentType.APPLICATION_JSON);
        StringEntity no_extention = new StringEntity("{\"index\":\"test\",\"path\":\"file:///adr/file\"}", ContentType.APPLICATION_JSON);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response;

        post.setEntity(not_valid_extention);
        response = httpClient.execute(post);
        assertEquals(400,response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("NOT VALID FILE EXTENTION"));

        post.setEntity(no_extention);
        response = httpClient.execute(post);
        assertEquals(400,response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("FILE EXTENTION NOT FOUND"));

        httpClient.close();

    }

    @Test
    void testValidPDFs() throws IOException {
        HttpPost post = new HttpPost("http://localhost:9200/_extract_metadata");
        StringEntity online_pdf = new StringEntity("{\"index\":\"test\",\"path\":\"https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf\"}", ContentType.APPLICATION_JSON);
        StringEntity local_pdf_linux = new StringEntity("{\"index\":\"test\",\"path\":\"file:///mnt/c/skola/Diplomka/document-metadata/metadata-extractor/src/tests/resources/test.pdf\"}", ContentType.APPLICATION_JSON);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response;

        post.setEntity(online_pdf);
        response = httpClient.execute(post);
        assertEquals(201,response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("\"result\":\"created\""));

        post.setEntity(local_pdf_linux);
        response = httpClient.execute(post);
        assertEquals(201,response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("\"result\":\"created\""));

        httpClient.close();
    }

}
