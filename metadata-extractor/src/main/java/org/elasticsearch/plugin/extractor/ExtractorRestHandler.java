package org.elasticsearch.plugin.extractor;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.elasticsearch.rest.*;
import org.elasticsearch.common.inject.Inject;

import java.io.IOException;

public class ExtractorRestHandler extends BaseRestHandler {
    private final String NAME = "_extract_metadata";
    @Inject
    public ExtractorRestHandler(Settings settings, RestController restController) {
        super();
        restController.registerHandler(RestRequest.Method.POST, "/"+NAME, this);
        restController.registerHandler(RestRequest.Method.PUT, "/"+NAME, this);
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Rest handler for metadata extraction plugin.
     * @param request RestRequest containing the content
     * @param client nodeclient of elasticsearch
     * @return channel with validation information
     * @throws IOException from XContentBuilder
     */
    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        InfoHolder val_info = ExtractionController.extract(request,client);


        return channel -> {
            if(val_info.getResponse()!=null){
                    channel.sendResponse(new BytesRestResponse(val_info.getResponse().status(), val_info.getResponse().toXContent(XContentBuilder.builder(XContentType.JSON.xContent()),ToXContent.EMPTY_PARAMS)));
                }else{
                    XContentBuilder builder = XContentBuilder.builder(XContentType.JSON.xContent()).startObject().field("message",val_info.getValidation_message()).endObject();
                    channel.sendResponse(new BytesRestResponse(val_info.getStatus(),builder));
                }
        };
    }
}