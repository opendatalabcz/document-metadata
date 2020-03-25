package org.elasticsearch.plugin.extractor.commons;

import java.io.File;

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
     * @param file input File
     * @return String representation of file extention
     */
    public String getFileExtention(File file){
        return file.getName().substring(file.getName().lastIndexOf(".") + 1);
    }
}
