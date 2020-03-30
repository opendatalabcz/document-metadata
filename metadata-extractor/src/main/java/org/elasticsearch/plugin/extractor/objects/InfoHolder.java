package org.elasticsearch.plugin.extractor.objects;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONObject;

import java.net.URL;

/**
 * Class holding information about validation, request content, response from elasticsearch and metadata object.
 */
public class InfoHolder {
    private RestStatus status;
    private boolean valid;
    private String validation_message;
    private IndexResponse response;
    private JSONObject metadata;
    private String output_index;
    private URL file;

    public InfoHolder(){
        response = null;
        metadata = null;
        status = null;
        validation_message = null;
        output_index = null;
        file = null;
    }

    public void mergeFromExtractor(InfoHolder new_info){
        valid = new_info.isValid();
        if(valid){
            metadata = new_info.getMetadata();
        }else{
            status = new_info.getStatus();
            validation_message = new_info.getValidation_message();
        }
    }

    public RestStatus getStatus() {
        return status;
    }

    public void setStatus(RestStatus status) {
        this.status = status;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getValidation_message() {
        return validation_message;
    }

    public void setValidation_message(String validation_message) {
        this.validation_message = validation_message;
    }

    public IndexResponse getResponse() {
        return response;
    }

    public void setResponse(IndexResponse response) {
        this.response = response;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public String getOutput_index() {
        return output_index;
    }

    public void setOutput_index(String output_index) {
        this.output_index = output_index;
    }

    public URL getFile() {
        return file;
    }

    public void setFile(URL file) {
        this.file = file;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    /**
     * @return custom String representation of InfoHolder object
     */
    @Override
    public String toString(){
        return "Rest_status: "+status+"\nvalid: "+valid+"\nvalidation_message: "+validation_message+"\n";
    }
}
