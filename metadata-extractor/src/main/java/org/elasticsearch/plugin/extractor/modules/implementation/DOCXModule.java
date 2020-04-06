package org.elasticsearch.plugin.extractor.modules.implementation;

import org.apache.poi.xwpf.usermodel.XWPFComment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.elasticsearch.plugin.extractor.modules.ExtractionModule;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.HashSet;

public class DOCXModule extends ExtractionModule {
    private final String [] supported_extentions = {"docx"};
    @Override
    public JSONObject extractMetadata(URL url) throws Exception {
        XWPFDocument doc = new XWPFDocument(new BufferedInputStream(url.openStream()));
        JSONObject final_meta = new JSONObject();
        JSONArray authors = new JSONArray();
        final_meta.put("revisions_enabled",doc.isTrackRevisions());
        HashSet<String> comment_authors = new HashSet<String>();
        for (XWPFComment comment : doc.getComments()){
            comment_authors.add(comment.getAuthor());
        }
        authors.put(comment_authors);
        final_meta.put("comments_authors", authors);
        if(doc.getProperties()!=null) {
            if (doc.getProperties().getCustomProperties() != null && doc.getProperties().getCustomProperties().getUnderlyingProperties() != null) {
                for (CTProperty prop : doc.getProperties().getCustomProperties().getUnderlyingProperties().getPropertyList()) {
                    final_meta.put(prop.getName(), prop.toString());
                }
            }
            if (doc.getProperties().getExtendedProperties() != null) {
                final_meta.put("application_name", doc.getProperties().getExtendedProperties().getApplication());
                final_meta.put("application_version", doc.getProperties().getExtendedProperties().getAppVersion());
                final_meta.put("chars_with_spaces", doc.getProperties().getExtendedProperties().getCharactersWithSpaces());
                final_meta.put("company", doc.getProperties().getExtendedProperties().getCompany());
                final_meta.put("hidden_slides", doc.getProperties().getExtendedProperties().getHiddenSlides());
                final_meta.put("hyperlink_base", doc.getProperties().getExtendedProperties().getHyperlinkBase());
                final_meta.put("lines", doc.getProperties().getExtendedProperties().getLines());
                final_meta.put("manager", doc.getProperties().getExtendedProperties().getManager());
                final_meta.put("mmclips", doc.getProperties().getExtendedProperties().getMMClips());
                final_meta.put("notes", doc.getProperties().getExtendedProperties().getNotes());
                final_meta.put("pages", doc.getProperties().getExtendedProperties().getPages());
                final_meta.put("paragraphs", doc.getProperties().getExtendedProperties().getParagraphs());
                final_meta.put("presentation_format", doc.getProperties().getExtendedProperties().getPresentationFormat());
                final_meta.put("slides", doc.getProperties().getExtendedProperties().getSlides());
                final_meta.put("template", doc.getProperties().getExtendedProperties().getTemplate());
                final_meta.put("words", doc.getProperties().getExtendedProperties().getWords());
                final_meta.put("total_time", doc.getProperties().getExtendedProperties().getTotalTime());

                if (doc.getProperties().getExtendedProperties().getUnderlyingProperties() != null) {
                    if(doc.getProperties().getExtendedProperties().getUnderlyingProperties().getDigSig()!=null){
                        final_meta.put("digital_signature", doc.getProperties().getExtendedProperties().getUnderlyingProperties().getDigSig().toString());
                    }
                    final_meta.put("security", doc.getProperties().getExtendedProperties().getUnderlyingProperties().getDocSecurity());
                    final_meta.put("hyperlinks_changed", doc.getProperties().getExtendedProperties().getUnderlyingProperties().getHyperlinksChanged());
                    final_meta.put("links_up_to_date", doc.getProperties().getExtendedProperties().getUnderlyingProperties().getLinksUpToDate());
                    final_meta.put("scaled", doc.getProperties().getExtendedProperties().getUnderlyingProperties().getScaleCrop());
                    final_meta.put("shared", doc.getProperties().getExtendedProperties().getUnderlyingProperties().getSharedDoc());
                }
            }
            if (doc.getProperties().getCoreProperties() != null) {
                final_meta.put("category",doc.getProperties().getCoreProperties().getCategory());
                final_meta.put("content_status",doc.getProperties().getCoreProperties().getContentStatus());
                final_meta.put("content_type",doc.getProperties().getCoreProperties().getContentType());
                final_meta.put("creation_date",doc.getProperties().getCoreProperties().getCreated());
                final_meta.put("creator",doc.getProperties().getCoreProperties().getCreator());
                final_meta.put("description",doc.getProperties().getCoreProperties().getDescription());
                final_meta.put("identifier",doc.getProperties().getCoreProperties().getIdentifier());
                final_meta.put("keywords",doc.getProperties().getCoreProperties().getKeywords());
                final_meta.put("last_modified_by",doc.getProperties().getCoreProperties().getLastModifiedByUser());
                final_meta.put("last_printed_date",doc.getProperties().getCoreProperties().getLastPrinted());
                final_meta.put("last_modified_date",doc.getProperties().getCoreProperties().getModified());
                final_meta.put("revision",doc.getProperties().getCoreProperties().getRevision());
                final_meta.put("subject",doc.getProperties().getCoreProperties().getSubject());
                final_meta.put("title",doc.getProperties().getCoreProperties().getTitle());
                if(doc.getProperties().getCoreProperties().getUnderlyingProperties()!=null){
                    if(doc.getProperties().getCoreProperties().getUnderlyingProperties().getVersionProperty().isPresent()){
                        final_meta.put("category",doc.getProperties().getCoreProperties().getUnderlyingProperties().getVersionProperty().get());
                    }
                    if(doc.getProperties().getCoreProperties().getUnderlyingProperties().getLanguageProperty().isPresent()){
                        final_meta.put("category",doc.getProperties().getCoreProperties().getUnderlyingProperties().getLanguageProperty().get());
                    }
                }
            }
        }
        return final_meta;
    }

    @Override
    public String[] getSupportedExtentions() {
        return supported_extentions;
    }
}
