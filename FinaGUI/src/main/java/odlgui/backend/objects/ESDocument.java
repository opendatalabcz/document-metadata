package odlgui.backend.objects;

import com.github.wnameless.json.flattener.JsonFlattener;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Class represents an elasticsearch document.
 */
public class ESDocument {
    private String doc_id;
    private Double score;
    private HashMap<String,Object> source_fields;
    private JSONObject raw_data;
    private String filename;
    public ESDocument(){
        source_fields = new HashMap<>();
    }

    public JSONObject getRaw_data() {
        return raw_data;
    }

    public void setRaw_data(JSONObject raw_data) {
        this.raw_data = raw_data;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public HashMap<String, Object> getSource_fields() {
        return source_fields;
    }

    public Object getFieldVal(String field){
        return source_fields.get(field);
    }

    /**
     * This function process the composite JSONObject and adds fields into hashmap of fields.
     * @param source JSONObject cointaining source fields
     */
    public void setSource_fields(JSONObject source) {
        Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(source.toString());
        HashMap<String,Object> arrays = new HashMap<>();
        flattenedJsonMap.forEach((k,v)->{
            if(k.contains("[")){
                String key = k.replaceAll("\\[[0-9]+\\]","");
                if(arrays.containsKey(key)){
                    arrays.put(key,v+","+arrays.get(key));
                }else{
                    arrays.put(key, v);
                }
            }else{
                source_fields.put(k, v);
            }
        });
        for(String key : arrays.keySet()){
            source_fields.put(key,"["+arrays.get(key)+"]");
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
