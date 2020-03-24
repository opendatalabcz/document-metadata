package org.elasticsearch.plugin.extractor.modules;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.elasticsearch.plugin.extractor.objects.InfoHolder;
import org.elasticsearch.rest.RestStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.CharBuffer;

public class PDFModule extends ExtractionModule{

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
                InputStream xmlInputStream = metadata.createInputStream();
                Reader reader = new InputStreamReader(xmlInputStream);
                xml_meta = XML.toJSONObject(reader);
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

    private JSONObject readMetadata(PDMetadata meta) throws IOException {
        InputStream xmlInputStream = meta.createInputStream();
        Reader reader = new InputStreamReader(xmlInputStream);
        return XML.toJSONObject(reader);
    }
}
