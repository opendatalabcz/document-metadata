package org.elasticsearch.plugin.extractor.modules;

import org.elasticsearch.plugin.extractor.objects.InfoHolder;

import java.io.File;

public abstract class ExtractionModule {
    /**
     * Abstract function which should extract metadata from the specified file,
     * create InfoHolder object with information about extraction and extracted metadata.
     * @param file input file for processing
     * @return InfoHolder object containting the validation output and metadata itself
     */
    public abstract InfoHolder extractMetadata(File file);

    /**
     * Abstract function responsible for providing supported extentions for module.
     * @return array of Strings supported by module
     */
    public abstract String[] getSupportedExtentions();
}
