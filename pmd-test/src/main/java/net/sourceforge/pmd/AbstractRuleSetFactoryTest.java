/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FilenameUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.ResourceLoader;

/**
 * Base test class to verify the language's rulesets. This class should be
 * subclassed for each language.
 */
public abstract class AbstractRuleSetFactoryTest {
    private static SAXParserFactory saxParserFactory;
    private static ValidateDefaultHandler validateDefaultHandler;
    private static SAXParser saxParser;

    protected Set<String> validXPathClassNames = new HashSet<>();

    public AbstractRuleSetFactoryTest() {
        validXPathClassNames.add(XPathRule.class.getName());
    }

    /**
     * Setups the XML parser with validation.
     *
     * @throws Exception
     *             any error
     */
    @BeforeClass
    public static void init() throws Exception {
        saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setValidating(true);
        saxParserFactory.setNamespaceAware(true);

        // Hope we're using Xerces, or this may not work!
        // Note: Features are listed here
        // http://xerces.apache.org/xerces2-j/features.html
        saxParserFactory.setFeature("http://xml.org/sax/features/validation", true);
        saxParserFactory.setFeature("http://apache.org/xml/features/validation/schema", true);
        saxParserFactory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

        validateDefaultHandler = new ValidateDefaultHandler();

        saxParser = saxParserFactory.newSAXParser();
    }

    /**
     * Checks all rulesets of all languages on the classpath and verifies that
     * all required attributes for all rules are specified.
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testAllPMDBuiltInRulesMeetConventions() throws Exception {
        int invalidSinceAttributes = 0;
        int invalidExternalInfoURL = 0;
        int invalidClassName = 0;
        int invalidRegexSuppress = 0;
        int invalidXPathSuppress = 0;
        String messages = "";
        List<String> ruleSetFileNames = getRuleSetFileNames();
        for (String fileName : ruleSetFileNames) {
            RuleSet ruleSet = loadRuleSetByFileName(fileName);
            for (Rule rule : ruleSet.getRules()) {

                // Skip references
                if (rule instanceof RuleReference) {
                    continue;
                }

                Language language = rule.getLanguage();
                String group = fileName.substring(fileName.lastIndexOf('/') + 1);
                group = group.substring(0, group.indexOf(".xml"));
                if (group.indexOf('-') >= 0) {
                    group = group.substring(0, group.indexOf('-'));
                }

                // Is since missing ?
                if (rule.getSince() == null) {
                    invalidSinceAttributes++;
                    messages += "Rule " + fileName + "/" + rule.getName() + " is missing 'since' attribute" + PMD.EOL;
                }
                // Is URL valid ?
                if (rule.getExternalInfoUrl() == null || "".equalsIgnoreCase(rule.getExternalInfoUrl())) {
                    invalidExternalInfoURL++;
                    messages += "Rule " + fileName + "/" + rule.getName() + " is missing 'externalInfoURL' attribute"
                            + PMD.EOL;
                } else {
                    String expectedExternalInfoURL = "https?://pmd.(sourceforge.net|github.io)/.+/pmd_rules_"
                            + language.getTerseName() + "_"
                            + FilenameUtils.getBaseName(fileName)
                            + ".html#"
                            + rule.getName().toLowerCase(Locale.ROOT);
                    if (rule.getExternalInfoUrl() == null
                            || !rule.getExternalInfoUrl().matches(expectedExternalInfoURL)) {
                        invalidExternalInfoURL++;
                        messages += "Rule " + fileName + "/" + rule.getName()
                                + " seems to have an invalid 'externalInfoURL' value (" + rule.getExternalInfoUrl()
                                + "), it should be:" + expectedExternalInfoURL + PMD.EOL;
                    }
                }
                // Proper class name/packaging?
                String expectedClassName = "net.sourceforge.pmd.lang." + language.getTerseName() + ".rule." + group
                        + "." + rule.getName() + "Rule";
                if (!rule.getRuleClass().equals(expectedClassName)
                        && !validXPathClassNames.contains(rule.getRuleClass())) {
                    invalidClassName++;
                    messages += "Rule " + fileName + "/" + rule.getName() + " seems to have an invalid 'class' value ("
                            + rule.getRuleClass() + "), it should be:" + expectedClassName + PMD.EOL;
                }
                // Should not have violation suppress regex property
                if (rule.getProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR) != null) {
                    invalidRegexSuppress++;
                    messages += "Rule " + fileName + "/" + rule.getName() + " should not have '"
                            + Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR.name()
                            + "', this is intended for end user customization only." + PMD.EOL;
                }
                // Should not have violation suppress xpath property
                if (rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR) != null) {
                    invalidXPathSuppress++;
                    messages += "Rule " + fileName + "/" + rule.getName() + " should not have '"
                            + Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR.name()
                            + "', this is intended for end user customization only." + PMD.EOL;
                }
            }
        }
        // We do this at the end to ensure we test ALL the rules before failing
        // the test
        if (invalidSinceAttributes > 0 || invalidExternalInfoURL > 0 || invalidClassName > 0 || invalidRegexSuppress > 0
                || invalidXPathSuppress > 0) {
            fail("All built-in PMD rules need 'since' attribute (" + invalidSinceAttributes
                    + " are missing), a proper ExternalURLInfo (" + invalidExternalInfoURL
                    + " are invalid), a class name meeting conventions (" + invalidClassName + " are invalid), no '"
                    + Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR.name() + "' property (" + invalidRegexSuppress
                    + " are invalid), and no '" + Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR.name() + "' property ("
                    + invalidXPathSuppress + " are invalid)" + PMD.EOL + messages);
        }
    }

    /**
     * Verifies that all rulesets are valid XML according to the xsd schema.
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testXmlSchema() throws Exception {
        boolean allValid = true;
        List<String> ruleSetFileNames = getRuleSetFileNames();
        for (String fileName : ruleSetFileNames) {
            boolean valid = validateAgainstSchema(fileName);
            allValid = allValid && valid;
        }
        assertTrue("All XML must parse without producing validation messages.", allValid);
    }

    /**
     * Verifies that all rulesets are valid XML according to the DTD.
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testDtd() throws Exception {
        boolean allValid = true;
        List<String> ruleSetFileNames = getRuleSetFileNames();
        for (String fileName : ruleSetFileNames) {
            boolean valid = validateAgainstDtd(fileName);
            allValid = allValid && valid;
        }
        assertTrue("All XML must parse without producing validation messages.", allValid);
    }

    /**
     * Reads and writes the rulesets to make sure, that no data is lost if the
     * rulests are processed.
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testReadWriteRoundTrip() throws Exception {

        List<String> ruleSetFileNames = getRuleSetFileNames();
        for (String fileName : ruleSetFileNames) {
            testRuleSet(fileName);
        }

    }

    // Gets all test PMD Ruleset XML files
    private List<String> getRuleSetFileNames() throws IOException, RuleSetNotFoundException {
        List<String> result = new ArrayList<>();

        for (Language language : LanguageRegistry.getLanguages()) {
            result.addAll(getRuleSetFileNames(language.getTerseName()));
        }

        return result;
    }

    private List<String> getRuleSetFileNames(String language) throws IOException, RuleSetNotFoundException {
        List<String> ruleSetFileNames = new ArrayList<>();
        try {
            Properties properties = new Properties();
            try (InputStream is = new ResourceLoader().loadClassPathResourceAsStreamOrThrow("rulesets/" + language + "/rulesets.properties")) {
                properties.load(is);
            }
            String fileNames = properties.getProperty("rulesets.filenames");
            StringTokenizer st = new StringTokenizer(fileNames, ",");
            while (st.hasMoreTokens()) {
                ruleSetFileNames.add(st.nextToken());
            }
        } catch (RuleSetNotFoundException e) {
            // this might happen if a language is only support by CPD, but not
            // by PMD
            System.err.println("No ruleset found for language " + language);
        }
        return ruleSetFileNames;
    }

    private RuleSet loadRuleSetByFileName(String ruleSetFileName) throws RuleSetNotFoundException {
        RuleSetFactory rsf = RulesetsFactoryUtils.defaultFactory();
        return rsf.createRuleSet(ruleSetFileName);
    }

    private boolean validateAgainstSchema(String fileName)
            throws IOException, RuleSetNotFoundException, ParserConfigurationException, SAXException {
        try (InputStream inputStream = loadResourceAsStream(fileName)) {
            boolean valid = validateAgainstSchema(inputStream);
            if (!valid) {
                System.err.println("Validation against XML Schema failed for: " + fileName);
            }
            return valid;
        }
    }

    private boolean validateAgainstSchema(InputStream inputStream)
            throws IOException, RuleSetNotFoundException, ParserConfigurationException, SAXException {

        saxParser.parse(inputStream, validateDefaultHandler.resetValid());
        inputStream.close();
        return validateDefaultHandler.isValid();
    }

    private boolean validateAgainstDtd(String fileName)
            throws IOException, RuleSetNotFoundException, ParserConfigurationException, SAXException {
        try (InputStream inputStream = loadResourceAsStream(fileName)) {
            boolean valid = validateAgainstDtd(inputStream);
            if (!valid) {
                System.err.println("Validation against DTD failed for: " + fileName);
            }
            return valid;
        }
    }

    private boolean validateAgainstDtd(InputStream inputStream)
            throws IOException, RuleSetNotFoundException, ParserConfigurationException, SAXException {

        // Read file into memory
        String file = readFullyToString(inputStream);
        inputStream.close();

        String rulesetNamespace = RuleSetWriter.RULESET_2_0_0_NS_URI;

        // Remove XML Schema stuff, replace with DTD
        file = file.replaceAll("<\\?xml [ a-zA-Z0-9=\".-]*\\?>", "");
        file = file.replaceAll("xmlns=\"" + rulesetNamespace + "\"", "");
        file = file.replaceAll("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
        file = file.replaceAll("xsi:schemaLocation=\"" + rulesetNamespace
                + " https://pmd.sourceforge.io/ruleset_\\d_0_0.xsd\"", "");

        if (rulesetNamespace.equals(RuleSetWriter.RULESET_2_0_0_NS_URI)) {
            file = "<?xml version=\"1.0\"?>" + PMD.EOL + "<!DOCTYPE ruleset SYSTEM "
                    + "\"https://pmd.sourceforge.io/ruleset_2_0_0.dtd\">" + PMD.EOL + file;
        } else {
            file = "<?xml version=\"1.0\"?>" + PMD.EOL + "<!DOCTYPE ruleset>" + PMD.EOL + file;
        }

        try (InputStream modifiedStream = new ByteArrayInputStream(file.getBytes())) {
            saxParser.parse(modifiedStream, validateDefaultHandler.resetValid());
        }
        return validateDefaultHandler.isValid();
    }

    private String readFullyToString(InputStream inputStream) throws IOException {
        StringBuilder buf = new StringBuilder(64 * 1024);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                buf.append(PMD.EOL);
            }
            return buf.toString();
        }
    }

    private static InputStream loadResourceAsStream(String resource) throws RuleSetNotFoundException {
        return new ResourceLoader().loadClassPathResourceAsStreamOrThrow(resource);
    }

    private void testRuleSet(String fileName)
            throws IOException, RuleSetNotFoundException, ParserConfigurationException, SAXException {

        // Load original XML
        // String xml1 =
        // readFullyToString(ResourceLoader.loadResourceAsStream(fileName));
        // System.out.println("xml1: " + xml1);

        // Load the original RuleSet
        RuleSet ruleSet1 = loadRuleSetByFileName(fileName);

        // Write to XML, first time
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        RuleSetWriter writer1 = new RuleSetWriter(outputStream1);
        writer1.write(ruleSet1);
        writer1.close();
        String xml2 = new String(outputStream1.toByteArray());
        // System.out.println("xml2: " + xml2);

        // Read RuleSet from XML, first time
        RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
        RuleSet ruleSet2 = ruleSetFactory.createRuleSet(createRuleSetReferenceId(xml2));

        // Do write/read a 2nd time, just to be sure

        // Write to XML, second time
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        RuleSetWriter writer2 = new RuleSetWriter(outputStream2);
        writer2.write(ruleSet2);
        writer2.close();
        String xml3 = new String(outputStream2.toByteArray());
        // System.out.println("xml3: " + xml3);

        // Read RuleSet from XML, second time
        RuleSet ruleSet3 = ruleSetFactory.createRuleSet(createRuleSetReferenceId(xml3));

        // The 2 written XMLs should all be valid w.r.t Schema/DTD
        assertTrue("1st roundtrip RuleSet XML is not valid against Schema (filename: " + fileName + ")",
                validateAgainstSchema(new ByteArrayInputStream(xml2.getBytes())));
        assertTrue("2nd roundtrip RuleSet XML is not valid against Schema (filename: " + fileName + ")",
                validateAgainstSchema(new ByteArrayInputStream(xml3.getBytes())));
        assertTrue("1st roundtrip RuleSet XML is not valid against DTD (filename: " + fileName + ")",
                validateAgainstDtd(new ByteArrayInputStream(xml2.getBytes())));
        assertTrue("2nd roundtrip RuleSet XML is not valid against DTD (filename: " + fileName + ")",
                validateAgainstDtd(new ByteArrayInputStream(xml3.getBytes())));

        // All 3 versions of the RuleSet should be the same
        assertEqualsRuleSet("Original RuleSet and 1st roundtrip Ruleset not the same (filename: " + fileName + ")",
                ruleSet1, ruleSet2);
        assertEqualsRuleSet("1st roundtrip Ruleset and 2nd roundtrip RuleSet not the same (filename: " + fileName + ")",
                ruleSet2, ruleSet3);

        // It's hard to compare the XML DOMs. At least the roundtrip ones should
        // textually be the same.
        assertEquals("1st roundtrip RuleSet XML and 2nd roundtrip RuleSet XML (filename: " + fileName + ")", xml2,
                xml3);
    }

    private void assertEqualsRuleSet(String message, RuleSet ruleSet1, RuleSet ruleSet2) {
        assertEquals(message + ", RuleSet name", ruleSet1.getName(), ruleSet2.getName());
        assertEquals(message + ", RuleSet description", ruleSet1.getDescription(), ruleSet2.getDescription());
        assertEquals(message + ", RuleSet exclude patterns", ruleSet1.getExcludePatterns(),
                ruleSet2.getExcludePatterns());
        assertEquals(message + ", RuleSet include patterns", ruleSet1.getIncludePatterns(),
                ruleSet2.getIncludePatterns());
        assertEquals(message + ", RuleSet rule count", ruleSet1.getRules().size(), ruleSet2.getRules().size());

        for (int i = 0; i < ruleSet1.getRules().size(); i++) {
            Rule rule1 = ((List<Rule>) ruleSet1.getRules()).get(i);
            Rule rule2 = ((List<Rule>) ruleSet2.getRules()).get(i);

            assertFalse(message + ", Different RuleReference",
                        rule1 instanceof RuleReference != rule2 instanceof RuleReference);

            if (rule1 instanceof RuleReference) {
                RuleReference ruleReference1 = (RuleReference) rule1;
                RuleReference ruleReference2 = (RuleReference) rule2;
                assertEquals(message + ", RuleReference overridden language", ruleReference1.getOverriddenLanguage(),
                        ruleReference2.getOverriddenLanguage());
                assertEquals(message + ", RuleReference overridden minimum language version",
                        ruleReference1.getOverriddenMinimumLanguageVersion(),
                        ruleReference2.getOverriddenMinimumLanguageVersion());
                assertEquals(message + ", RuleReference overridden maximum language version",
                        ruleReference1.getOverriddenMaximumLanguageVersion(),
                        ruleReference2.getOverriddenMaximumLanguageVersion());
                assertEquals(message + ", RuleReference overridden deprecated", ruleReference1.isOverriddenDeprecated(),
                        ruleReference2.isOverriddenDeprecated());
                assertEquals(message + ", RuleReference overridden name", ruleReference1.getOverriddenName(),
                        ruleReference2.getOverriddenName());
                assertEquals(message + ", RuleReference overridden description",
                        ruleReference1.getOverriddenDescription(), ruleReference2.getOverriddenDescription());
                assertEquals(message + ", RuleReference overridden message", ruleReference1.getOverriddenMessage(),
                        ruleReference2.getOverriddenMessage());
                assertEquals(message + ", RuleReference overridden external info url",
                        ruleReference1.getOverriddenExternalInfoUrl(), ruleReference2.getOverriddenExternalInfoUrl());
                assertEquals(message + ", RuleReference overridden priority", ruleReference1.getOverriddenPriority(),
                        ruleReference2.getOverriddenPriority());
                assertEquals(message + ", RuleReference overridden examples", ruleReference1.getOverriddenExamples(),
                        ruleReference2.getOverriddenExamples());
            }

            assertEquals(message + ", Rule name", rule1.getName(), rule2.getName());
            assertEquals(message + ", Rule class", rule1.getRuleClass(), rule2.getRuleClass());
            assertEquals(message + ", Rule description " + rule1.getName(), rule1.getDescription(),
                    rule2.getDescription());
            assertEquals(message + ", Rule message", rule1.getMessage(), rule2.getMessage());
            assertEquals(message + ", Rule external info url", rule1.getExternalInfoUrl(), rule2.getExternalInfoUrl());
            assertEquals(message + ", Rule priority", rule1.getPriority(), rule2.getPriority());
            assertEquals(message + ", Rule examples", rule1.getExamples(), rule2.getExamples());

            List<PropertyDescriptor<?>> propertyDescriptors1 = rule1.getPropertyDescriptors();
            List<PropertyDescriptor<?>> propertyDescriptors2 = rule2.getPropertyDescriptors();
            assertEquals(message + ", Rule property descriptor ", propertyDescriptors1, propertyDescriptors2);
            for (int j = 0; j < propertyDescriptors1.size(); j++) {
                Object value1 = rule1.getProperty(propertyDescriptors1.get(j));
                Object value2 = rule2.getProperty(propertyDescriptors2.get(j));
                // special case for Pattern, there is no equals method
                if (propertyDescriptors1.get(j).type() == Pattern.class) {
                    value1 = ((Pattern) value1).pattern();
                    value2 = ((Pattern) value2).pattern();
                }
                assertEquals(message + ", Rule property value " + j, value1, value2);
            }
            assertEquals(message + ", Rule property descriptor count", propertyDescriptors1.size(),
                    propertyDescriptors2.size());
        }
    }

    /**
     * Create a {@link RuleSetReferenceId} by the given XML string.
     *
     * @param ruleSetXml
     *            the ruleset file content as string
     * @return the {@link RuleSetReferenceId}
     */
    protected static RuleSetReferenceId createRuleSetReferenceId(final String ruleSetXml) {
        return new RuleSetReferenceId(null) {
            @Override
            public InputStream getInputStream(ResourceLoader resourceLoader) throws RuleSetNotFoundException {
                try {
                    return new ByteArrayInputStream(ruleSetXml.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }
        };
    }

    /**
     * Validator for the SAX parser
     */
    private static class ValidateDefaultHandler extends DefaultHandler {
        private boolean valid = true;
        private final Map<String, String> schemaMapping;

        ValidateDefaultHandler() {
            schemaMapping = new HashMap<>();
            schemaMapping.put("https://pmd.sourceforge.io/ruleset_2_0_0.xsd", "ruleset_2_0_0.xsd");
            schemaMapping.put("https://pmd.sourceforge.io/ruleset_2_0_0.dtd", "ruleset_2_0_0.dtd");
        }

        public ValidateDefaultHandler resetValid() {
            valid = true;
            return this;
        }

        public boolean isValid() {
            return valid;
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            log("Error", e);
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            log("FatalError", e);
        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            log("Warning", e);
        }

        private void log(String prefix, SAXParseException e) {
            String message = prefix + " at (" + e.getLineNumber() + ", " + e.getColumnNumber() + "): " + e.getMessage();
            System.err.println(message);
            valid = false;
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
            String resource = schemaMapping.get(systemId);

            if (resource != null) {
                try {
                    InputStream inputStream = loadResourceAsStream(resource);
                    return new InputSource(inputStream);
                } catch (RuleSetNotFoundException e) {
                    System.err.println(e.getMessage());
                    throw new IOException(e.getMessage());
                }
            }
            throw new IllegalArgumentException(
                    "No clue how to handle: publicId=" + publicId + ", systemId=" + systemId);
        }
    }

}
