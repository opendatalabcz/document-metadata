package org.elasticsearch.plugin.extractor.objects;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONObject;

public class InfoHolder {
    private RestStatus status;
    private boolean valid;
    private String validation_message;
    private IndexResponse response;
    private JSONObject metadata;

    public InfoHolder(){
        response = null;
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

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString(){
        return "Rest_status: "+status+"\nvalid: "+valid+"\nvalidation_message: "+validation_message+"\n";
    }
}
