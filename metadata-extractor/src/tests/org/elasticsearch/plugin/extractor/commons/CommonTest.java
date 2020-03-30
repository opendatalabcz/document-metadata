package org.elasticsearch.plugin.extractor.commons;


import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class CommonTest {

    @org.junit.jupiter.api.Test
    void getInstance() {
        Common obj = Common.getInstance();
        assertNotNull(obj);
        assertEquals(obj,Common.getInstance());
    }

    @org.junit.jupiter.api.Test
    void getFileExtention() throws MalformedURLException {
        URL url_1 = new URL("file:///fsk/sadksa/pdf.pdf");
        URL url_2 = new URL("file:///tisk/sds/pdf./pdf.pdf");
        URL url_3 = new URL("file:///tiss/sdadsa/");
        URL url_4 = new URL("file:///ksda/dsasad/ts.");
        URL url_5 = new URL("file:///.pdf");
        URL url_6 = new URL("https://skuska/skuska.pdf");
        URL url_7 = new URL("file://C:\\.pdf\\moj.pdf");
        URL url_8 = new URL("file://C:\\.pdf\\moj");
        URL url_9 = new URL("file://C:/.pdf/moj");
        assertEquals("pdf",Common.getInstance().getFileExtention(url_1));
        assertEquals("pdf",Common.getInstance().getFileExtention(url_2));
        assertEquals(null,Common.getInstance().getFileExtention(url_3));
        assertEquals("",Common.getInstance().getFileExtention(url_4));
        assertEquals("pdf",Common.getInstance().getFileExtention(url_5));
        assertEquals("pdf",Common.getInstance().getFileExtention(url_6));
        assertEquals("pdf",Common.getInstance().getFileExtention(url_7));
        assertEquals(null,Common.getInstance().getFileExtention(url_8));
        assertEquals(null,Common.getInstance().getFileExtention(url_9));
    }
}