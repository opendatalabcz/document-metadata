package org.elasticsearch.plugin.extractor.modules;

import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.json.JSONObject;

import java.net.URL;


public abstract class ExtractionModule {
    /**
     * Abstract function which should extract metadata from the specified file,
     * create InfoHolder object with information about extraction and extracted metadata.
     * @param path path to input file for processing
     * @return JSONObject containting the metadata itself
     * @throws Exception from parsing and extracting metadata from file
     */
    public abstract JSONObject extractMetadata(URL path) throws Exception;

    /**
     * Abstract function responsible for providing supported extentions for module.
     * @return array of Strings supported by module
     */
    public abstract String[] getSupportedExtentions();
}
