package org.elasticsearch.plugin.extractor;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.plugin.extractor.modules.ModuleController;
import org.elasticsearch.plugin.extractor.objects.DataHolder;
import org.elasticsearch.plugin.extractor.objects.ValidationException;
import org.elasticsearch.plugin.extractor.validation.Validator;
import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
public class ExtractionController {

    /**
     * Function is responsible for controlling proccess of validating request, validating file,
     * extracting metadata and indexing extracted metadata to elasticsearch.
     * @param request RestRequest object
     * @param client NodeClient
     * @return InfoHolder object with validation status and extracted metadata
     */
    public static InfoHolder extract(RestRequest request, NodeClient client){
        InfoHolder my_info = new InfoHolder();
        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                DataHolder data;
                InfoHolder val_info = new InfoHolder();
            try {
                if (Validator.getInstance().isValidRequest(request)) {
                    data = prepareDataFromContent(request.content().utf8ToString());
                    data.setMetadata(ModuleController.getInstance().extractMetadata(data.getFile()));
                        if(data.getDocument_id()==null||data.getDocument_id().isEmpty()){
                            IndexRequest index_request = new IndexRequest(data.getOutput_index());
                            index_request.source(data.getFinalSource().toString(), XContentType.JSON);
                            val_info.setResponse(client.index(index_request).actionGet());
                        }else{
                            UpdateRequest update_request = new UpdateRequest();
                            update_request.docAsUpsert(true);
                            update_request.id(data.getDocument_id());
                            update_request.index(data.getOutput_index());
                            update_request.doc(data.getFinalSource().toString(), XContentType.JSON);
                            val_info.setResponse(client.update(update_request).actionGet());
                        }

            }
            } catch (ValidationException e) {
                val_info.setValid(false);
                val_info.setValidation_message(e.getMessage());
                val_info.setStatus(e.getRest_status());
            } catch (Exception e){
                e.printStackTrace();
                val_info.setValid(false);
                val_info.setValidation_message("MODULE_EXCEPTION: "+e.getMessage());
                val_info.setStatus(RestStatus.INTERNAL_SERVER_ERROR);
            }
            my_info.setValidation_message(val_info.getValidation_message());
                my_info.setValid(val_info.isValid());
                my_info.setStatus(val_info.getStatus());
                my_info.setResponse(val_info.getResponse());

            return null;
        });
        return my_info;
    }

    private static DataHolder prepareDataFromContent(String content) throws ValidationException {

        DataHolder data = new DataHolder();
        JSONObject obj = new JSONObject(content);
        try {
            data.setFile(new URL(obj.getString("path")));
        } catch (MalformedURLException e) {
            InfoHolder info = new InfoHolder();
            info.setValid(false);
            info.setValidation_message("FAILED CREATING VALID URL FROM: "+obj.getString("path"));
            throw new ValidationException(info);
        }
        data.setOutput_index(obj.getString("index"));
        if(obj.has("extras")){
            data.setDocument_extra_source(obj.getJSONObject("extras"));
        }
        if(obj.has("_id")&&!obj.getString("_id").isEmpty()){
            data.setDocument_id(obj.getString("_id"));
        }
        return data;
    }
}
