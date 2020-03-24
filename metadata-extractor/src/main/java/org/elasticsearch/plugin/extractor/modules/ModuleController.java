package org.elasticsearch.plugin.extractor.modules;

import org.elasticsearch.plugin.extractor.objects.InfoHolder;

import java.io.File;
import java.util.HashMap;

public class ModuleController {
    private static ModuleController module_controller;
    private HashMap<String,ExtractionModule> modules;
    private ModuleController(){
        modules = new HashMap<String, ExtractionModule>();
        modules.put("pdf",new PDFModule());
    }
    public static ModuleController getInstance(){
        if(module_controller==null){
            module_controller = new ModuleController();
        }
        return module_controller;
    }
    public InfoHolder extractMetadata(File file, String extention){
        return modules.get(extention).extractMetadata(file);
    }

    public boolean containsModuleForExtention(String extention){
        return modules.containsKey(extention);
    }

}
