package org.elasticsearch.plugin.extractor.modules.implementation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.elasticsearch.plugin.extractor.modules.ExtractionModule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.*;
import java.net.URL;

public class PDFModule extends ExtractionModule {
    private final String [] supported_extentions = {"pdf"};
    public PDFModule(){
    }
    @Override
    public JSONObject extractMetadata(URL url) throws Exception{
            PDDocument doc = PDDocument.load(new BufferedInputStream(url.openStream()));
            PDDocumentInformation info = doc.getDocumentInformation();
            PDDocumentCatalog catalog = doc.getDocumentCatalog();
            PDMetadata metadata = catalog.getMetadata();
            JSONObject final_meta = new JSONObject();
            JSONObject xml_meta = new JSONObject();
            if(metadata!=null) {
                xml_meta = readMetadata(metadata);
            }
            JSONObject dict_meta = new JSONObject();
            JSONArray pages_meta_array = new JSONArray();
            for(PDPage pdPage : doc.getPages()){
                PDMetadata page_metadata = pdPage.getMetadata();
                if(page_metadata!=null) {
                    pages_meta_array.put(readMetadata(page_metadata));
                }
            }
            final_meta.put("pages_metadata",pages_meta_array);
            final_meta.put("document_metadata_xml",xml_meta);
            for(String key:info.getMetadataKeys()){
                dict_meta.put(key,info.getCustomMetadataValue(key));
            }
            final_meta.put("document_metadata_dict",dict_meta);
            doc.close();
            return final_meta;
    }

    @Override
    public String[] getSupportedExtentions() {
        return supported_extentions;
    }

    /**
     * Function reads xmp metadata from metadata object and coverts them into JSONObject.
     * @param meta metadata object containting xmp metadata
     * @return JSONObject from xmp metadata
     * @throws IOException
     */
    private JSONObject readMetadata(PDMetadata meta) throws IOException {
        InputStream xmlInputStream = meta.createInputStream();
        Reader reader = new InputStreamReader(xmlInputStream);
        return XML.toJSONObject(reader);
    }
}
