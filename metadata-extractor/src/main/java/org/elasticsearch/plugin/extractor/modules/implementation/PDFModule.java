package org.elasticsearch.plugin.extractor.modules.implementation;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.elasticsearch.plugin.extractor.modules.ExtractionModule;
import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.*;

public class PDFModule extends ExtractionModule {
    private final String [] supported_extentions = {"pdf"};
    public PDFModule(){
    }
    @Override
    public InfoHolder extractMetadata(File file){
        InfoHolder info_holder = new InfoHolder();
        try {

            PDDocument doc = PDDocument.load(file);
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
            info_holder.setValid(true);
            info_holder.setMetadata(final_meta);
        } catch (IOException e) {
            info_holder.setValid(false);
            info_holder.setStatus(RestStatus.BAD_REQUEST);
            info_holder.setValidation_message("IOEXCEPTION ON FILE: "+e.getMessage());
        }

        return info_holder;
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
