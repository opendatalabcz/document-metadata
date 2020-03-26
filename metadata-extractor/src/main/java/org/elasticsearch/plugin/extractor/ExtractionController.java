package org.elasticsearch.plugin.extractor;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.plugin.extractor.modules.ModuleController;
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
            try {
                InfoHolder val_info = Validator.validateRequest(request);
                if (val_info.isValid()) {
                    JSONObject json = new JSONObject(request.content().utf8ToString());
                    URL url = new URL(json.getString("path"));
                    val_info = Validator.validateFile(url);
                    if (val_info.isValid()) {
                        val_info = ModuleController.getInstance().extractMetadata(url);
                        if (val_info.isValid()) {
                            IndexRequest index_request = new IndexRequest(json.getString("index"));
                            index_request.source(val_info.getMetadata().toString(), XContentType.JSON);
                            val_info.setResponse(client.index(index_request).actionGet());
                        }
                    }
                }
                my_info.setValidation_message(val_info.getValidation_message());
                my_info.setValid(val_info.isValid());
                my_info.setStatus(val_info.getStatus());
                my_info.setResponse(val_info.getResponse());
                my_info.setMetadata(val_info.getMetadata());
            }catch (MalformedURLException e) {
                my_info.setValid(false);
                my_info.setStatus(RestStatus.BAD_REQUEST);
                my_info.setValidation_message("MALFORMED URL");
            }

            return null;
        });
        return my_info;
    }
}
