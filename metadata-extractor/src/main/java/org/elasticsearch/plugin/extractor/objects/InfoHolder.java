package org.elasticsearch.plugin.extractor.objects;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.rest.RestStatus;

/**
 * Class holding information about validation, request content and response from elasticsearch.
 */
public class InfoHolder {
    private RestStatus status;
    private boolean valid;
    private String validation_message;
    private DocWriteResponse response;


    public InfoHolder(){
        response = null;
        status = null;
        validation_message = null;
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

    public DocWriteResponse getResponse() {
        return response;
    }

    public void setResponse(DocWriteResponse response) {
        this.response = response;
    }





    /**
     * @return custom String representation of InfoHolder object
     */
    @Override
    public String toString(){
        return "Rest_status: "+status+"\nvalid: "+valid+"\nvalidation_message: "+validation_message+"\n";
    }
}
