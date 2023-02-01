/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Base test class to verify the language's rulesets. This class should be
 * subclassed for each language.
 */
public abstract class AbstractRuleSetFactoryTest {

    private static ValidateDefaultHandler validateDefaultHandler;
    private static SAXParser saxParser;

    protected Set<String> validXPathClassNames = new HashSet<>();
    private final Set<String> languagesToSkip = new HashSet<>();

    public AbstractRuleSetFactoryTest() {
        this(new String[0]);
    }

    /**
     * Constructor used when a module that depends on another module wants to filter out the dependee's rulesets.
     *
     * @param languagesToSkip {@link Language}s terse names that appear in the classpath via a dependency, but should be
     * skipped because they aren't the primary language which the concrete instance of this class is testing.
     */
    public AbstractRuleSetFactoryTest(String... languagesToSkip) {
        this.languagesToSkip.add("dummy");
        this.languagesToSkip.addAll(Arrays.asList(languagesToSkip));
        validXPathClassNames.add(XPathRule.class.getName());
    }

    /**
     * Setups the XML parser with validation.
     *
     * @throws Exception
     *             any error
     */
    @BeforeAll
    static void init() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
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
    void testAllPMDBuiltInRulesMeetConventions() throws Exception {
        int invalidSinceAttributes = 0;
        int invalidExternalInfoURL = 0;
        int invalidClassName = 0;
        int invalidRegexSuppress = 0;
        int invalidXPathSuppress = 0;
        StringBuilder messages = new StringBuilder();
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
                    messages.append("Rule ")
                            .append(fileName)
                            .append("/")
                            .append(rule.getName())
                            .append(" is missing 'since' attribute")
                            .append(PMD.EOL);
                }
                // Is URL valid ?
                if (rule.getExternalInfoUrl() == null || "".equalsIgnoreCase(rule.getExternalInfoUrl())) {
                    invalidExternalInfoURL++;
                    messages.append("Rule ")
                            .append(fileName)
                            .append("/")
                            .append(rule.getName())
                            .append(" is missing 'externalInfoURL' attribute")
                            .append(PMD.EOL);
                } else {
                    String expectedExternalInfoURL = "https?://pmd.(sourceforge.net|github.io)/.+/pmd_rules_"
                            + language.getTerseName() + "_"
                            + IOUtil.getFilenameBase(fileName)
                            + ".html#"
                            + rule.getName().toLowerCase(Locale.ROOT);
                    if (rule.getExternalInfoUrl() == null
                            || !rule.getExternalInfoUrl().matches(expectedExternalInfoURL)) {
                        invalidExternalInfoURL++;
                        messages.append("Rule ")
                                .append(fileName)
                                .append("/")
                                .append(rule.getName())
                                .append(" seems to have an invalid 'externalInfoURL' value (")
                                .append(rule.getExternalInfoUrl())
                                .append("), it should be:")
                                .append(expectedExternalInfoURL)
                                .append(PMD.EOL);
                    }
                }
                // Proper class name/packaging?
                String expectedClassName = "net.sourceforge.pmd.lang." + language.getTerseName() + ".rule." + group
                        + "." + rule.getName() + "Rule";
                if (!rule.getRuleClass().equals(expectedClassName)
                        && !validXPathClassNames.contains(rule.getRuleClass())) {
                    invalidClassName++;
                    messages.append("Rule ")
                            .append(fileName)
                            .append("/")
                            .append(rule.getName())
                            .append(" seems to have an invalid 'class' value (")
                            .append(rule.getRuleClass())
                            .append("), it should be:")
                            .append(expectedClassName)
                            .append(PMD.EOL);
                }
                // Should not have violation suppress regex property
                if (rule.getProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR) != null) {
                    invalidRegexSuppress++;
                    messages.append("Rule ")
                            .append(fileName)
                            .append("/")
                            .append(rule.getName())
                            .append(" should not have '")
                            .append(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR.name())
                            .append("', this is intended for end user customization only.")
                            .append(PMD.EOL);
                }
                // Should not have violation suppress xpath property
                if (rule.getProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR) != null) {
                    invalidXPathSuppress++;
                    messages.append("Rule ").append(fileName).append("/").append(rule.getName()).append(" should not have '").append(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR.name()).append("', this is intended for end user customization only.").append(PMD.EOL);
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
    void testXmlSchema() throws Exception {
        boolean allValid = true;
        List<String> ruleSetFileNames = getRuleSetFileNames();
        for (String fileName : ruleSetFileNames) {
            boolean valid = validateAgainstSchema(fileName);
            allValid = allValid && valid;
        }
        assertTrue(allValid, "All XML must parse without producing validation messages.");
    }

    /**
     * Verifies that all rulesets are valid XML according to the DTD.
     *
     * @throws Exception
     *             any error
     */
    @Test
    void testDtd() throws Exception {
        boolean allValid = true;
        List<String> ruleSetFileNames = getRuleSetFileNames();
        for (String fileName : ruleSetFileNames) {
            boolean valid = validateAgainstDtd(fileName);
            allValid = allValid && valid;
        }
        assertTrue(allValid, "All XML must parse without producing validation messages.");
    }

    /**
     * Reads and writes the rulesets to make sure, that no data is lost if the
     * rulests are processed.
     *
     * @throws Exception
     *             any error
     */
    @Test
    void testReadWriteRoundTrip() throws Exception {

        List<String> ruleSetFileNames = getRuleSetFileNames();
        for (String fileName : ruleSetFileNames) {
            testRuleSet(fileName);
        }

    }

    // Gets all test PMD Ruleset XML files
    private List<String> getRuleSetFileNames() throws IOException {
        List<String> result = new ArrayList<>();

        for (Language language : LanguageRegistry.PMD.getLanguages()) {
            if (this.languagesToSkip.contains(language.getTerseName())) {
                continue;
            }
            result.addAll(getRuleSetFileNames(language.getTerseName()));
        }

        return result;
    }

    private List<String> getRuleSetFileNames(String language) throws IOException {
        List<String> ruleSetFileNames = new ArrayList<>();
        ruleSetFileNames.addAll(getRuleSetFileNames(language, "rulesets/" + language + "/rulesets.properties"));
        ruleSetFileNames.addAll(getRuleSetFileNames(language, "category/" + language + "/categories.properties"));
        return ruleSetFileNames;
    }

    private List<String> getRuleSetFileNames(String language, String propertiesPath) throws IOException {
        List<String> ruleSetFileNames = new ArrayList<>();
        Properties properties = new Properties();
        @SuppressWarnings("PMD.CloseResource")
        InputStream input = loadResourceAsStream(propertiesPath);
        if (input == null) {
            // this might happen if a language is only support by CPD, but not
            // by PMD
            System.err.println("No ruleset found for language " + language);
            return Collections.emptyList();
        }
        try (InputStream is = input) {
            properties.load(is);
        }
        String fileNames = properties.getProperty("rulesets.filenames");
        StringTokenizer st = new StringTokenizer(fileNames, ",");
        while (st.hasMoreTokens()) {
            ruleSetFileNames.add(st.nextToken());
        }
        return ruleSetFileNames;
    }

    private RuleSet loadRuleSetByFileName(String ruleSetFileName) {
        return new RuleSetLoader().loadFromResource(ruleSetFileName);
    }

    private boolean validateAgainstSchema(String fileName) throws IOException, SAXException {
        try (InputStream inputStream = loadResourceAsStream(fileName)) {
            boolean valid = validateAgainstSchema(inputStream);
            if (!valid) {
                System.err.println("Validation against XML Schema failed for: " + fileName);
            }
            return valid;
        }
    }

    private boolean validateAgainstSchema(InputStream inputStream) throws IOException, SAXException {

        saxParser.parse(inputStream, validateDefaultHandler.resetValid());
        inputStream.close();
        return validateDefaultHandler.isValid();
    }

    private boolean validateAgainstDtd(String fileName) throws IOException, SAXException {
        try (InputStream inputStream = loadResourceAsStream(fileName)) {
            boolean valid = validateAgainstDtd(inputStream);
            if (!valid) {
                System.err.println("Validation against DTD failed for: " + fileName);
            }
            return valid;
        }
    }

    private boolean validateAgainstDtd(InputStream inputStream) throws IOException, SAXException {

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

        if (RuleSetWriter.RULESET_2_0_0_NS_URI.equals(rulesetNamespace)) {
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

    private InputStream loadResourceAsStream(String resource) {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

    private void testRuleSet(String fileName) throws IOException, SAXException {

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
        RuleSetLoader loader = new RuleSetLoader();
        RuleSet ruleSet2 = loader.loadFromString("", xml2);

        // Do write/read a 2nd time, just to be sure

        // Write to XML, second time
        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        RuleSetWriter writer2 = new RuleSetWriter(outputStream2);
        writer2.write(ruleSet2);
        writer2.close();
        String xml3 = new String(outputStream2.toByteArray());
        // System.out.println("xml3: " + xml3);

        // Read RuleSet from XML, second time
        RuleSet ruleSet3 = loader.loadFromString("", xml3);

        // The 2 written XMLs should all be valid w.r.t Schema/DTD
        assertTrue(validateAgainstSchema(new ByteArrayInputStream(xml2.getBytes())),
                "1st roundtrip RuleSet XML is not valid against Schema (filename: " + fileName + ")");
        assertTrue(validateAgainstSchema(new ByteArrayInputStream(xml3.getBytes())),
                "2nd roundtrip RuleSet XML is not valid against Schema (filename: " + fileName + ")");
        assertTrue(validateAgainstDtd(new ByteArrayInputStream(xml2.getBytes())),
                "1st roundtrip RuleSet XML is not valid against DTD (filename: " + fileName + ")");
        assertTrue(validateAgainstDtd(new ByteArrayInputStream(xml3.getBytes())),
                "2nd roundtrip RuleSet XML is not valid against DTD (filename: " + fileName + ")");

        // All 3 versions of the RuleSet should be the same
        assertEqualsRuleSet("Original RuleSet and 1st roundtrip Ruleset not the same (filename: " + fileName + ")",
                ruleSet1, ruleSet2);
        assertEqualsRuleSet("1st roundtrip Ruleset and 2nd roundtrip RuleSet not the same (filename: " + fileName + ")",
                ruleSet2, ruleSet3);

        // It's hard to compare the XML DOMs. At least the roundtrip ones should
        // textually be the same.
        assertEquals(xml2, xml3,
                "1st roundtrip RuleSet XML and 2nd roundtrip RuleSet XML (filename: " + fileName + ")");
    }

    private void assertEqualsRuleSet(String message, RuleSet ruleSet1, RuleSet ruleSet2) {
        assertEquals(ruleSet1.getName(), ruleSet2.getName(), message + ", RuleSet name");
        assertEquals(ruleSet1.getDescription(), ruleSet2.getDescription(), message + ", RuleSet description");
        assertEquals(ruleSet1.getFileExclusions(), ruleSet2.getFileExclusions(),
                message + ", RuleSet exclude patterns");
        assertEquals(ruleSet1.getFileInclusions(), ruleSet2.getFileInclusions(),
                message + ", RuleSet include patterns");
        assertEquals(ruleSet1.getRules().size(), ruleSet2.getRules().size(), message + ", RuleSet rule count");

        for (int i = 0; i < ruleSet1.getRules().size(); i++) {
            Rule rule1 = ((List<Rule>) ruleSet1.getRules()).get(i);
            Rule rule2 = ((List<Rule>) ruleSet2.getRules()).get(i);

            assertFalse(rule1 instanceof RuleReference != rule2 instanceof RuleReference,
                    message + ", Different RuleReference");

            if (rule1 instanceof RuleReference) {
                RuleReference ruleReference1 = (RuleReference) rule1;
                RuleReference ruleReference2 = (RuleReference) rule2;
                assertEquals(ruleReference1.getOverriddenMinimumLanguageVersion(),
                        ruleReference2.getOverriddenMinimumLanguageVersion(),
                        message + ", RuleReference overridden minimum language version");
                assertEquals(ruleReference1.getOverriddenMaximumLanguageVersion(),
                        ruleReference2.getOverriddenMaximumLanguageVersion(),
                        message + ", RuleReference overridden maximum language version");
                assertEquals(ruleReference1.isOverriddenDeprecated(), ruleReference2.isOverriddenDeprecated(),
                        message + ", RuleReference overridden deprecated");
                assertEquals(ruleReference1.getOverriddenName(), ruleReference2.getOverriddenName(),
                        message + ", RuleReference overridden name");
                assertEquals(ruleReference1.getOverriddenDescription(), ruleReference2.getOverriddenDescription(),
                        message + ", RuleReference overridden description");
                assertEquals(ruleReference1.getOverriddenMessage(), ruleReference2.getOverriddenMessage(),
                        message + ", RuleReference overridden message");
                assertEquals(ruleReference1.getOverriddenExternalInfoUrl(), ruleReference2.getOverriddenExternalInfoUrl(),
                        message + ", RuleReference overridden external info url");
                assertEquals(ruleReference1.getOverriddenPriority(), ruleReference2.getOverriddenPriority(),
                        message + ", RuleReference overridden priority");
                assertEquals(ruleReference1.getOverriddenExamples(), ruleReference2.getOverriddenExamples(),
                        message + ", RuleReference overridden examples");
            }

            assertEquals(rule1.getName(), rule2.getName(), message + ", Rule name");
            assertEquals(rule1.getRuleClass(), rule2.getRuleClass(), message + ", Rule class");
            assertEquals(rule1.getDescription(), rule2.getDescription(),
                    message + ", Rule description " + rule1.getName());
            assertEquals(rule1.getMessage(), rule2.getMessage(), message + ", Rule message");
            assertEquals(rule1.getExternalInfoUrl(), rule2.getExternalInfoUrl(), message + ", Rule external info url");
            assertEquals(rule1.getPriority(), rule2.getPriority(), message + ", Rule priority");
            assertEquals(rule1.getExamples(), rule2.getExamples(), message + ", Rule examples");

            List<PropertyDescriptor<?>> propertyDescriptors1 = rule1.getPropertyDescriptors();
            List<PropertyDescriptor<?>> propertyDescriptors2 = rule2.getPropertyDescriptors();
            assertEquals(propertyDescriptors1, propertyDescriptors2, message + ", Rule property descriptor ");
            for (int j = 0; j < propertyDescriptors1.size(); j++) {
                Object value1 = rule1.getProperty(propertyDescriptors1.get(j));
                Object value2 = rule2.getProperty(propertyDescriptors2.get(j));
                // special case for Pattern, there is no equals method
                if (propertyDescriptors1.get(j).type() == Pattern.class) {
                    value1 = ((Pattern) value1).pattern();
                    value2 = ((Pattern) value2).pattern();
                }
                assertEquals(value1, value2, message + ", Rule property value " + j);
            }
            assertEquals(propertyDescriptors1.size(), propertyDescriptors2.size(),
                    message + ", Rule property descriptor count");
        }
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
        public void error(SAXParseException e) {
            log("Error", e);
        }

        @Override
        public void fatalError(SAXParseException e) {
            log("FatalError", e);
        }

        @Override
        public void warning(SAXParseException e) {
            log("Warning", e);
        }

        private void log(String prefix, SAXParseException e) {
            String message = prefix + " at (" + e.getLineNumber() + ", " + e.getColumnNumber() + "): " + e.getMessage();
            System.err.println(message);
            valid = false;
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException {
            String resource = schemaMapping.get(systemId);

            if (resource != null) {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
                if (inputStream == null) {
                    throw new FileNotFoundException(resource);
                }
                return new InputSource(inputStream);
            }
            throw new IllegalArgumentException(
                    "No clue how to handle: publicId=" + publicId + ", systemId=" + systemId);
        }
    }

}
