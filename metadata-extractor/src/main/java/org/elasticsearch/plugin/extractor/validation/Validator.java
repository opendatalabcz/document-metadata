package org.elasticsearch.plugin.extractor.validation;

import org.elasticsearch.plugin.extractor.commons.Common;
import org.elasticsearch.plugin.extractor.modules.ModuleController;
import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Validator {

    /**
     * Function check basic validy of request (required fields, validity of json content)
     * @param request RestRequest object containing the content
     * @return InfoHolder object with validation status
     */
    public static InfoHolder validateRequest(RestRequest request){
        InfoHolder val_info = new InfoHolder();
        if(!request.hasContent()){
            val_info.setStatus(RestStatus.BAD_REQUEST);
            val_info.setValid(false);
            val_info.setValidation_message("MISSING REQUEST CONTENT");
        }else{
            JSONObject json = null;
            try {
                json = new JSONObject(request.getHttpRequest().content().utf8ToString());
                if((json.getString("index").isEmpty())||(json.getString("path").isEmpty())){
                    val_info.setStatus(RestStatus.BAD_REQUEST);
                    val_info.setValid(false);
                    val_info.setValidation_message("MISSING MANDATORY ARGUMENT (index,path) IN CONTENT");
                }else{
                    val_info.setValid(true);
                }
            } catch (JSONException e) {
                val_info.setStatus(RestStatus.BAD_REQUEST);
                val_info.setValid(false);
                val_info.setValidation_message("FAILED PARSING JSON INPUT: "+e.getMessage());
            }

        }
        return val_info;
    }

    /**
     * Function check basic validy of file (extention support, valid URL).
     * @param url path to the file
     * @return InfoHolder object with validation status
     */
    public static InfoHolder validateFile(URL url){
        InfoHolder val_info = new InfoHolder();
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            if(url.getPath().contains(".")) {
                String extention = Common.getInstance().getFileExtention(url);
                if (!ModuleController.getInstance().containsModuleForExtention(extention)) {
                    val_info.setValid(false);
                    val_info.setStatus(RestStatus.BAD_REQUEST);
                    val_info.setValidation_message("NOT VALID FILE EXTENTION");
                } else {
                    val_info.setValid(true);
                }
            }else {
                val_info.setValid(false);
                val_info.setStatus(RestStatus.BAD_REQUEST);
                val_info.setValidation_message("FILE EXTENTION NOT FOUND");
            }
            return null;
        });
        return val_info;
    }
}
