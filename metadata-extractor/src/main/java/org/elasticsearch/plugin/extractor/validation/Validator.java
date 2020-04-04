package org.elasticsearch.plugin.extractor.validation;

import org.elasticsearch.plugin.extractor.commons.Common;
import org.elasticsearch.plugin.extractor.modules.ModuleController;
import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.elasticsearch.plugin.extractor.objects.ValidationException;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
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
     * Function check basic validy of request (required fields, validity of json content attributes).
     * @param request RestRequest object containing the content
     * @return true if request is valid
     * @throws ValidationException holding the error message and response code
     */
    public boolean isValidRequest(RestRequest request) throws ValidationException {
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
                    val_info.setValid(isValidFile(url));
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
        if(!val_info.isValid()){
            throw new ValidationException(val_info);
        }
        return true;
    }

    /**
     * Function check basic validy of file (extention support).
     * @param url path to the file
     * @return true if file is valid
     */
    private boolean isValidFile(URL url) throws ValidationException {
        InfoHolder val_info = new InfoHolder();
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            if(url.getPath().contains(".")) {
                String extention = Common.getInstance().getFileExtention(url);
                try {
                    if (!ModuleController.getInstance().containsModuleForExtention(extention)) {
                        val_info.setValid(false);
                        val_info.setStatus(RestStatus.BAD_REQUEST);
                        val_info.setValidation_message("NOT VALID FILE EXTENTION");
                    } else {
                        val_info.setValid(true);
                    }
                } catch (NoSuchMethodException e) {
                    val_info.setValid(false);
                    val_info.setStatus(RestStatus.INTERNAL_SERVER_ERROR);
                    val_info.setValidation_message("MODULE_CONTROLLER: "+e.getMessage());
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    val_info.setValid(false);
                    val_info.setStatus(RestStatus.INTERNAL_SERVER_ERROR);
                    val_info.setValidation_message("MODULE_CONTROLLER: "+e.getMessage());
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    val_info.setValid(false);
                    val_info.setStatus(RestStatus.INTERNAL_SERVER_ERROR);
                    val_info.setValidation_message("MODULE_CONTROLLER: "+e.getMessage());
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    val_info.setValid(false);
                    val_info.setStatus(RestStatus.INTERNAL_SERVER_ERROR);
                    val_info.setValidation_message("MODULE_CONTROLLER: "+e.getMessage());
                    e.printStackTrace();
                }
            }else {
                val_info.setValid(false);
                val_info.setStatus(RestStatus.BAD_REQUEST);
                val_info.setValidation_message("FILE EXTENTION NOT FOUND");
            }
            return null;
        });
        if(!val_info.isValid()){
            throw new ValidationException(val_info);
        }
        return true;
    }
}
