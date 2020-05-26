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
        int back_slash = 0;
        int front_slash = 0;
        back_slash = url.toString().lastIndexOf("/");
        front_slash = url.toString().lastIndexOf("\\");
        String tmp = url.toString().substring(Math.max(back_slash,front_slash)+1);
        if(tmp.contains(".")){
            return tmp.substring(tmp.lastIndexOf(".") + 1);
        }
        return null;
    }
}
