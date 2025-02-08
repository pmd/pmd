/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;


class CpdXsltTest {
    /* Sample ant build.xml file. Run with "ant cpdxsl".

<project>
  <target name="cpdxslt">
    <xslt in="src/test/resources/net/sourceforge/pmd/cpd/SampleCpdReport.xml" style="etc/xslt/cpdhtml.xslt" out="cpd.html" />
  </target>
</project>
     */

    // note: we use src/test/resources instead of CpdXsltTest.class.getResourceAsStream()
    // so that we use the platform specific line endings in all cases
    private Path expectedBasePath = Paths.get("src/test/resources", CpdXsltTest.class.getPackage().getName().replace('.', '/'));


    @Test
    void cpdhtml() throws Exception {
        String result = runXslt("cpdhtml.xslt");
        assertPlatformLineEndingFirstLine(result);
        String expected = IOUtil.readFileToString(expectedBasePath.resolve("ExpectedCpdHtmlReport.html").toFile(), StandardCharsets.UTF_8);
        assertEquals(expected, result);
    }

    @Test
    void cpdhtmlv2() throws Exception {
        String result = runXslt("cpdhtml-v2.xslt");
        assertPlatformLineEndingFirstLine(result);
        String expected = IOUtil.readFileToString(expectedBasePath.resolve("ExpectedCpdHtmlReport-v2.html").toFile(), StandardCharsets.UTF_8);
        assertEquals(expected, result);
    }

    private static void assertPlatformLineEndingFirstLine(String text) {
        String eol = System.lineSeparator();
        int firstEol = text.indexOf(eol);
        assertTrue(firstEol > -1, "Could find line separator at all");
        String actualEol = text.substring(firstEol, firstEol + eol.length());

        Function<String, String> hexDump = s -> s.codePoints()
                .mapToObj(Integer::toHexString)
                .map(h -> "0x" + h)
                .collect(Collectors.joining(" ", "'", "'"));
        assertEquals(eol, actualEol, "Wrong line separator found: expected=" + hexDump.apply(eol) + " actual=" + hexDump.apply(actualEol));
    }

    private String runXslt(String stylesheet) throws Exception {
        XSLTErrorListener errorListener = new XSLTErrorListener();

        // note: using the default JDK factory, otherwise we would use Saxon from PMD's classpath
        // which supports more xslt features.
        TransformerFactory factory = TransformerFactory
                .newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
        factory.setErrorListener(errorListener);
        StreamSource xslt = new StreamSource(new File("etc/xslt/" + stylesheet));
        Templates template = factory.newTemplates(xslt);
        StreamSource cpdReport = new StreamSource(CpdXsltTest.class.getResourceAsStream("SampleCpdReport.xml"));
        StreamResult result = new StreamResult(new StringWriter());
        Transformer transformer = template.newTransformer();
        transformer.setErrorListener(errorListener);
        transformer.transform(cpdReport, result);

        assertTrue(errorListener.hasNoErrors(), "XSLT errors occurred: " + errorListener);
        return result.getWriter().toString();
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
