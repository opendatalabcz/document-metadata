package org.elasticsearch.plugin.extractor.modules;

import org.elasticsearch.plugin.extractor.commons.Common;
import org.json.JSONObject;
import org.reflections.Reflections;

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
    private ModuleController() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        modules = new HashMap<String, ExtractionModule>();
        reflections = new Reflections("org.elasticsearch.plugin.extractor.modules.implementation");
        allClasses = reflections.getSubTypesOf(ExtractionModule.class);
        loadModules();

    }

    /**
     * Function creates (if not created) and returns the instance of ModuleController.
     * @return ModuleController instance
     * @throws NoSuchMethodException exception thrown if loading extraction classes fails
     * @throws InstantiationException exception thrown if loading extraction classes fails
     * @throws IllegalAccessException exception thrown if loading extraction classes fails
     * @throws InvocationTargetException exception thrown if loading extraction classes fails
     */
    public static ModuleController getInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if(module_controller==null){
            module_controller = new ModuleController();
        }
        return module_controller;
    }

    /**
     * Function will find correct ExtractorModule and call it's extractMetadata function (works like connector).
     * @param url path to input file for metadata extraction
     * @return JSONObject containting extracted metadata
     * @throws Exception from parsing and extracting metadata from file
     */
    public JSONObject extractMetadata(URL url) throws Exception{
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
    private void loadModules() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            for(Class cls : allClasses){
                ExtractionModule extraction_module = (ExtractionModule) cls.getConstructor().newInstance();
                for(String sup_extention:extraction_module.getSupportedExtentions()){
                    modules.put(sup_extention,extraction_module);
                }
            }
    }

}
