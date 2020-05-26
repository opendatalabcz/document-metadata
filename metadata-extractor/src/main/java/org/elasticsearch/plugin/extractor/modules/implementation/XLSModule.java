package org.elasticsearch.plugin.extractor.modules.implementation;

import org.apache.poi.hpsf.CustomProperty;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.elasticsearch.plugin.extractor.modules.ExtractionModule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

public class XLSModule extends ExtractionModule {
    private final String [] supported_extentions = {"xls"};
    @Override
    public JSONObject extractMetadata(URL url) throws Exception {
        HSSFWorkbook doc = new HSSFWorkbook(new BufferedInputStream(url.openStream()));
        JSONObject final_meta = new JSONObject();
        HashSet<String> notes_authors = new HashSet<String>();
        Iterator<Sheet> sheet_it = doc.sheetIterator();
        while(sheet_it.hasNext()){
            Sheet sheet = sheet_it.next();
            for(Comment comment : sheet.getCellComments().values()){
                notes_authors.add(comment.getAuthor());
            }
        }
        final_meta.put("notes_authors", notes_authors);
        if(doc.getDocumentSummaryInformation().getCustomProperties()!=null) {
            for (CustomProperty prop : doc.getDocumentSummaryInformation().getCustomProperties().properties()) {
                final_meta.put(prop.getName(), prop.getValue());
            }
        }
        //DocumentSummaryInformation
        final_meta.put("application_version",doc.getDocumentSummaryInformation().getApplicationVersion());
        final_meta.put("category",doc.getDocumentSummaryInformation().getCategory());
        final_meta.put("chars_with_spaces",doc.getDocumentSummaryInformation().getCharCountWithSpaces());
        final_meta.put("company",doc.getDocumentSummaryInformation().getCompany());
        final_meta.put("content_status",doc.getDocumentSummaryInformation().getContentStatus());
        final_meta.put("content_type",doc.getDocumentSummaryInformation().getContentType());
        final_meta.put("document_version",doc.getDocumentSummaryInformation().getDocumentVersion());
        final_meta.put("hidden_count",doc.getDocumentSummaryInformation().getHiddenCount());
        final_meta.put("hiperlinks_changed",doc.getDocumentSummaryInformation().getHyperlinksChanged());
        final_meta.put("language",doc.getDocumentSummaryInformation().getLanguage());
        final_meta.put("lines",doc.getDocumentSummaryInformation().getLineCount());
        final_meta.put("links_dirty",doc.getDocumentSummaryInformation().getLinksDirty());
        final_meta.put("manager",doc.getDocumentSummaryInformation().getManager());
        final_meta.put("mmclips",doc.getDocumentSummaryInformation().getMMClipCount());
        final_meta.put("notes",doc.getDocumentSummaryInformation().getNoteCount());
        final_meta.put("pars",doc.getDocumentSummaryInformation().getParCount());
        final_meta.put("presentation_format",doc.getDocumentSummaryInformation().getPresentationFormat());
        final_meta.put("scaled",doc.getDocumentSummaryInformation().getScale());
        final_meta.put("slides",doc.getDocumentSummaryInformation().getSlideCount());
        if(doc.getDocumentSummaryInformation().getVBADigitalSignature()!=null) {
            final_meta.put("digital_signature", new String(doc.getDocumentSummaryInformation().getVBADigitalSignature()));
        }

        //SummaryInformation
        final_meta.put("application_name",doc.getSummaryInformation().getApplicationName());
        final_meta.put("author",doc.getSummaryInformation().getAuthor());
        final_meta.put("create_date",doc.getSummaryInformation().getCreateDateTime());
        final_meta.put("edit_duration",doc.getSummaryInformation().getEditTime());
        final_meta.put("keywords",doc.getSummaryInformation().getKeywords());
        final_meta.put("last_author",doc.getSummaryInformation().getLastAuthor());
        final_meta.put("last_printed_date",doc.getSummaryInformation().getLastPrinted());
        final_meta.put("last_saved_date",doc.getSummaryInformation().getLastSaveDateTime());
        final_meta.put("pages",doc.getSummaryInformation().getPageCount());
        final_meta.put("revision_number",doc.getSummaryInformation().getRevNumber());
        final_meta.put("security",doc.getSummaryInformation().getSecurity());
        final_meta.put("subject",doc.getSummaryInformation().getSubject());
        final_meta.put("template",doc.getSummaryInformation().getTemplate());
        final_meta.put("title",doc.getSummaryInformation().getTitle());
        final_meta.put("words",doc.getSummaryInformation().getWordCount());
        return final_meta;
    }

    @Override
    public String[] getSupportedExtentions() {
        return supported_extentions;
    }
}
