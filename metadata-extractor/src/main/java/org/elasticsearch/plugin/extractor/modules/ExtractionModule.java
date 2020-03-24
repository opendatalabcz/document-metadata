package org.elasticsearch.plugin.extractor.modules;

import org.elasticsearch.plugin.extractor.objects.InfoHolder;

import java.io.File;

public abstract class ExtractionModule {
    public abstract InfoHolder extractMetadata(File file);
}
