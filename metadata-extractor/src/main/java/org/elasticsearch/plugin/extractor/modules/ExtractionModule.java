package org.elasticsearch.plugin.extractor.modules;

import org.elasticsearch.plugin.extractor.objects.InfoHolder;

import java.net.URL;


public abstract class ExtractionModule {
    /**
     * Abstract function which should extract metadata from the specified file,
     * create InfoHolder object with information about extraction and extracted metadata.
     * @param path path to input file for processing
     * @return InfoHolder object containting the validation output and metadata itself
     */
    public abstract InfoHolder extractMetadata(URL path);

    /**
     * Abstract function responsible for providing supported extentions for module.
     * @return array of Strings supported by module
     */
    public abstract String[] getSupportedExtentions();
}
