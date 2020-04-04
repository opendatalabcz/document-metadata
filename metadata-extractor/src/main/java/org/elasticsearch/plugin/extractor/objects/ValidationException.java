package org.elasticsearch.plugin.extractor.objects;

import org.elasticsearch.rest.RestStatus;

public class ValidationException extends Exception {
    private String message;
    private RestStatus rest_status;


    public ValidationException(InfoHolder val_info){
        this.rest_status = val_info.getStatus();
        this.message = val_info.getValidation_message();
    }
    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RestStatus getRest_status() {
        return rest_status;
    }

    public void setRest_status(RestStatus rest_status) {
        this.rest_status = rest_status;
    }
}
