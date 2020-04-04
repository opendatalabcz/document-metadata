package org.elasticsearch.plugin.extractor.objects;

import org.json.JSONObject;

import java.net.URL;

/**
 * This class is responsible for holding information about
 */
public class DataHolder {
    private JSONObject metadata;
    private String output_index;
    private String document_id;
    private JSONObject document_extra_source;
    private URL file;

    public DataHolder(){
        output_index = null;
        file = null;
        document_id = null;
        document_extra_source = null;
        metadata = null;
    }
    public JSONObject getMetadata() {
        return metadata;
    }

    public String getOutput_index() {
        return output_index;
    }

    public void setOutput_index(String output_index) {
        this.output_index = output_index;
    }

    public URL getFile() {
        return file;
    }

    public void setFile(URL file) {
        this.file = file;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public JSONObject getDocument_extra_source() {
        return document_extra_source;
    }

    public void setDocument_extra_source(JSONObject document_extra_source) {
        this.document_extra_source = document_extra_source;
    }

    /**
     * Function is resposinble for creating final JSONOjbect, which is then sended to elastic.
     * @return JSONObject from extracted metadata and user extra input source
     */
    public JSONObject getFinalSource(){
        JSONObject tmp = new JSONObject();
        tmp.put("metadata",metadata);
        tmp.put("extras",document_extra_source);
        return tmp;
    }
}
