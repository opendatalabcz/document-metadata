package org.elasticsearch.plugin.extractor.validation;

import org.elasticsearch.plugin.extractor.commons.Common;
import org.elasticsearch.plugin.extractor.modules.ModuleController;
import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class Validator {
    private static Validator validator;

    private Validator(){
    }

    public static Validator getInstance(){
        if(validator==null){
            validator = new Validator();
        }
        return validator;
    }

    /**
     * Function check basic validy of request (required fields, validity of json content attributes)
     * @param request RestRequest object containing the content
     * @return InfoHolder object with validation status and extracted attributes
     */
    public InfoHolder validateRequestAndExtractContentData(RestRequest request){
        InfoHolder val_info = new InfoHolder();
        if(!request.hasContent()){
            val_info.setStatus(RestStatus.BAD_REQUEST);
            val_info.setValid(false);
            val_info.setValidation_message("MISSING REQUEST CONTENT");
        }else{
            JSONObject json = null;
            try {
                json = new JSONObject(request.getHttpRequest().content().utf8ToString());
                if((!json.has("index"))||(!json.has("path"))){
                    val_info.setStatus(RestStatus.BAD_REQUEST);
                    val_info.setValid(false);
                    val_info.setValidation_message("MISSING MANDATORY FIELD (index,path) IN CONTENT");
                }else if((json.getString("index").isEmpty())||(json.getString("path").isEmpty())){
                    val_info.setStatus(RestStatus.BAD_REQUEST);
                    val_info.setValid(false);
                    val_info.setValidation_message("MANDATORY FIELD (index,path) NOT FILLED");
                }else{
                    URL url = new URL(json.getString("path"));
                    val_info = validateFile(url);
                    if(val_info.isValid()){
                        val_info.setOutput_index(json.getString("index"));
                        val_info.setFile(url);
                    }
                }
            } catch (JSONException e) {
                val_info.setStatus(RestStatus.BAD_REQUEST);
                val_info.setValid(false);
                val_info.setValidation_message("FAILED PARSING JSON INPUT: "+e.getMessage());
            } catch(MalformedURLException e){
                val_info.setStatus(RestStatus.BAD_REQUEST);
                val_info.setValid(false);
                val_info.setValidation_message("FAILED CREATING VALID URL FROM: "+json.getString("path"));
            }

        }
        return val_info;
    }

    /**
     * Function check basic validy of file (extention support).
     * @param url path to the file
     * @return InfoHolder object with validation status
     */
    private InfoHolder validateFile(URL url){
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
