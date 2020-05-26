package odlgui.backend.model;

import odlgui.backend.objects.ESDocument;
import odlgui.backend.objects.Query;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main purpose of this class is to store session queries and ES response data.
 */
public class History {
    private static History history = null;
    private static HashMap<String, HashMap<String, Query>> layouts;
    private static HashMap<String,ArrayList<ESDocument>> results;
    private History(){
        layouts = new HashMap<>();
        results = new HashMap<>();
    }
    public static History getInstance(){
        if(history==null){
            history = new History();
        }
        return history;
    }
    public HashMap<String, HashMap<String,Query>> getLayouts(){
        return layouts;
    }
    public HashMap<String, ArrayList<ESDocument>> getResults(){
        return results;
    }

}
