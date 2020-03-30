package org.elasticsearch.plugin.extractor;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.plugin.extractor.modules.ModuleController;
import org.elasticsearch.plugin.extractor.validation.Validator;
import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.elasticsearch.rest.RestRequest;
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
                InfoHolder val_info = Validator.getInstance().validateRequestAndExtractContentData(request);
                    if (val_info.isValid()) {
                        val_info.mergeFromExtractor(ModuleController.getInstance().extractMetadata(val_info.getFile()));
                        if (val_info.isValid()) {
                            IndexRequest index_request = new IndexRequest(val_info.getOutput_index());
                            index_request.source(val_info.getMetadata().toString(), XContentType.JSON);
                            val_info.setResponse(client.index(index_request).actionGet());
                        }
                }
                my_info.setValidation_message(val_info.getValidation_message());
                my_info.setValid(val_info.isValid());
                my_info.setStatus(val_info.getStatus());
                my_info.setResponse(val_info.getResponse());
                my_info.setMetadata(val_info.getMetadata());

            return null;
        });
        return my_info;
    }
}
