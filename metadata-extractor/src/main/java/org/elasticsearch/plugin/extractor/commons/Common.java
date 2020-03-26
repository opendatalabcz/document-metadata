package org.elasticsearch.plugin.extractor.commons;

import java.io.File;
import java.net.URL;

/**
 * Singleton class containing common functions needed in multiple classes.
 */
public class Common {
    private static Common common;
    private Common(){
    }
    public static Common getInstance(){
        if(common==null){
            common = new Common();
        }
        return common;
    }

    /**
     * Returns the file extention in String form.
     * @param url path of file
     * @return String representation of file extention
     */
    public String getFileExtention(URL url){
        return url.getPath().substring(url.getPath().lastIndexOf(".") + 1);
    }
}
