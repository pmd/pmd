/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class RuleSetSchemaTest {

    private CollectingErrorHandler errorHandler;

    @Before
    public void setUp() {
        Locale.setDefault(Locale.ROOT);
        errorHandler = new CollectingErrorHandler();
    }

    @Test
    public void verifyVersion2() throws Exception {
        String ruleset = generateRuleSet("2.0.0");
        Document doc = parseWithVersion2(ruleset);
        assertNotNull(doc);

        assertTrue(errorHandler.isValid());

        assertEquals("Custom ruleset", ((Attr) doc.getElementsByTagName("ruleset").item(0).getAttributes().getNamedItem("name")).getValue());
    }

    @Test
    public void validateOnly() throws Exception {
        Validator validator = PMDRuleSetEntityResolver.getSchemaVersion2().newValidator();
        validator.setErrorHandler(errorHandler);
        validator.validate(new StreamSource(new ByteArrayInputStream(generateRuleSet("2.0.0").getBytes(StandardCharsets.UTF_8))));
        assertTrue(errorHandler.isValid());
        errorHandler.reset();
    }

    private Document parseWithVersion2(String ruleset) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/validation/schema", true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        builder.setErrorHandler(errorHandler);
        builder.setEntityResolver(new PMDRuleSetEntityResolver());

        Document doc = builder.parse(new ByteArrayInputStream(ruleset.getBytes(StandardCharsets.UTF_8)));
        return doc;
    }

    private String generateRuleSet(String version) {
        String versionUnderscore = version.replaceAll("\\.", "_");
        String ruleset = "<?xml version=\"1.0\"?>" + PMD.EOL
                + "<ruleset " + PMD.EOL
                + "    xmlns=\"http://pmd.sourceforge.net/ruleset/" + version + "\"" + PMD.EOL
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + PMD.EOL
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset/" + version + " https://pmd.sourceforge.io/ruleset_" + versionUnderscore + ".xsd\""
                + "    name=\"Custom ruleset\" >" + PMD.EOL
                + "  <description>" + PMD.EOL
                + "  This ruleset checks my code for bad stuff" + PMD.EOL
                + "  </description>" + PMD.EOL
                + "  <rule name=\"DummyBasicMockRule\" language=\"dummy\" since=\"1.0\" message=\"Test Rule 1\"" + PMD.EOL
                + "        class=\"net.sourceforge.pmd.lang.rule.MockRule\"" + PMD.EOL
                + "        externalInfoUrl=\"${pmd.website.baseurl}/rules/dummy/basic.xml#DummyBasicMockRule\"" + PMD.EOL
                + "  >" + PMD.EOL
                + "        <description>" + PMD.EOL
                + "           Just for test" + PMD.EOL
                + "     </description>" + PMD.EOL
                + "        <priority>3</priority>" + PMD.EOL
                + "        <example>" + PMD.EOL
                + " <![CDATA[" + PMD.EOL
                + " ]]>" + PMD.EOL
                + "     </example>" + PMD.EOL
                + "    </rule>" + PMD.EOL
                + "  <rule ref=\"rulesets/dummy/basic.xml#DummyBasicMockRule\"/>" + PMD.EOL
                + "</ruleset>" + PMD.EOL;
        return ruleset;
    }

    public static class PMDRuleSetEntityResolver implements EntityResolver {
        private static URL schema2 = RuleSetFactory.class.getResource("/ruleset_2_0_0.xsd");
        private static SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if ("https://pmd.sourceforge.io/ruleset_2_0_0.xsd".equals(systemId)) {
                return new InputSource(schema2.toExternalForm());
            }
            throw new IllegalArgumentException("Unable to resolve entity (publicId=" + publicId + ", systemId=" + systemId + ")");
        }

        public static Schema getSchemaVersion2() throws SAXException {
            return schemaFactory.newSchema(schema2);
        }
    }

    public static class CollectingErrorHandler implements ErrorHandler {
        private List<SAXParseException> warnings = new ArrayList<>();
        private List<SAXParseException> errors = new ArrayList<>();
        private List<SAXParseException> fatalErrors = new ArrayList<>();

        public boolean isValid() {
            return warnings.isEmpty() && errors.isEmpty() && fatalErrors.isEmpty();
        }

        public List<SAXParseException> getWarnings() {
            return warnings;
        }

        public List<SAXParseException> getErrors() {
            return errors;
        }

        public List<SAXParseException> getFatalErrors() {
            return fatalErrors;
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            warnings.add(exception);
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            errors.add(exception);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            fatalErrors.add(exception);
        }

        @Override
        public String toString() {
            return "Warnings: " + warnings + "; Errors: " + errors + "; Fatal Errors: " + fatalErrors;
        }

        public void reset() {
            warnings.clear();
            errors.clear();
            fatalErrors.clear();
        }
    }
}
