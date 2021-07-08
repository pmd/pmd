/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class CpdXsltTest {
    /* Sample ant build.xml file. Run with "ant cpdxsl".

<project>
  <target name="cpdxslt">
    <xslt in="src/test/resources/net/sourceforge/pmd/cpd/SampleCpdReport.xml" style="etc/xslt/cpdhtml.xslt" out="cpd.html" />
  </target>
</project>
     */

    @Test
    public void cpdhtml() throws Exception {
        XSLTErrorListener errorListener = new XSLTErrorListener();

        // note: using the default JDK factory, otherwise we would use Saxon from PMD's classpath
        // which supports more xslt features.
        TransformerFactory factory = TransformerFactory
                .newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
        factory.setErrorListener(errorListener);
        StreamSource xslt = new StreamSource(new File("etc/xslt/cpdhtml.xslt"));
        Templates template = factory.newTemplates(xslt);
        StreamSource cpdReport = new StreamSource(CpdXsltTest.class.getResourceAsStream("SampleCpdReport.xml"));
        StreamResult result = new StreamResult(new StringWriter());
        Transformer transformer = template.newTransformer();
        transformer.setErrorListener(errorListener);
        transformer.transform(cpdReport, result);

        String expected = IOUtils.toString(CpdXsltTest.class.getResourceAsStream("ExpectedCpdHtmlReport.html"), StandardCharsets.UTF_8);
        Assert.assertEquals(expected, result.getWriter().toString());
        Assert.assertTrue("XSLT errors occured: " + errorListener, errorListener.hasNoErrors());
    }

    private static class XSLTErrorListener implements ErrorListener {
        final List<TransformerException> errors = new ArrayList<>();

        @Override
        public void warning(TransformerException exception) throws TransformerException {
            errors.add(exception);
        }

        @Override
        public void fatalError(TransformerException exception) throws TransformerException {
            errors.add(exception);
        }

        @Override
        public void error(TransformerException exception) throws TransformerException {
            errors.add(exception);
        }

        public boolean hasNoErrors() {
            return errors.isEmpty();
        }

        @Override
        public String toString() {
            return errors.toString();
        }
    }
}
