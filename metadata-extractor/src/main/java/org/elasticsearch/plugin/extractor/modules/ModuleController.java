package org.elasticsearch.plugin.extractor.modules;

import org.elasticsearch.plugin.extractor.commons.Common;
import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * Singleton class which is resposible for loading all modules into hashmap,
 * verifying existance of module and calling right module's extract function.
 */
public class ModuleController {
    private static ModuleController module_controller;
    private HashMap<String,ExtractionModule> modules;
    private Reflections reflections;

    private Set<Class<? extends ExtractionModule>> allClasses;
    private ModuleController() {
        modules = new HashMap<String, ExtractionModule>();
        reflections = new Reflections("org.elasticsearch.plugin.extractor.modules.implementation");
        allClasses = reflections.getSubTypesOf(ExtractionModule.class);
        loadModules();

    }

    /**
     * Function creates (if not created) and returns the instance of ModuleController.
     * @return ModuleController instance
     */
    public static ModuleController getInstance(){
        if(module_controller==null){
            module_controller = new ModuleController();
        }
        return module_controller;
    }

    /**
     * Function will find correct ExtractorModule and call it's extractMetadata function (works like connector).
     * @param url path to input file for metadata extraction
     * @return InfoHolder object containting extracted metadata and validation information.
     */
    public InfoHolder extractMetadata(URL url){
        return modules.get(Common.getInstance().getFileExtention(url)).extractMetadata(url);
    }

    /**
     * Function check if one of extractor classes supports provided extention in String.
     * @param extention file extention in String object
     * @return true if extentions is supported
     */
    public boolean containsModuleForExtention(String extention){
        return modules.containsKey(extention);
    }

    /**
     * Function will load all extractor classes (modules) in package org.elasticsearch.plugin.extractor.modules.implementation
     */
    private void loadModules(){
        try {
            for(Class cls : allClasses){
                ExtractionModule extraction_module = (ExtractionModule) cls.getConstructor().newInstance();
                for(String sup_extention:extraction_module.getSupportedExtentions()){
                    modules.put(sup_extention,extraction_module);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
