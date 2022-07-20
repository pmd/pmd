/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetLoadException;
import net.sourceforge.pmd.RuleSetLoader;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.IOUtil;

/**
 * Advanced methods for test cases
 */
public abstract class RuleTst {
    private final DocumentBuilder documentBuilder;

    /** Use a single classloader for all tests. */
    private final ClassLoader classpathClassLoader;

    public RuleTst() {
        classpathClassLoader = makeClassPathClassLoader();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(RuleTst.class.getResource("/rule-tests_1_0_0.xsd"));
            dbf.setSchema(schema);
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });
            documentBuilder = builder;
        } catch (SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private ClassLoader makeClassPathClassLoader() {
        final ClassLoader classpathClassLoader;
        PMDConfiguration config = new PMDConfiguration();
        config.prependAuxClasspath(".");
        classpathClassLoader = config.getClassLoader();
        return classpathClassLoader;
    }


    protected void setUp() {
        // This method is intended to be overridden by subclasses.
    }

    protected List<Rule> getRules() {
        return Collections.emptyList();
    }

    /**
     * Find a rule in a certain ruleset by name
     */
    public Rule findRule(String ruleSet, String ruleName) {
        try {
            RuleSet parsedRset = new RuleSetLoader().warnDeprecated(false).loadFromResource(ruleSet);
            Rule rule = parsedRset.getRuleByName(ruleName);
            if (rule == null) {
                Assertions.fail("Rule " + ruleName + " not found in ruleset " + ruleSet);
            } else {
                rule.setRuleSetName(ruleSet);
            }
            return rule;
        } catch (RuleSetLoadException e) {
            e.printStackTrace();
            Assertions.fail("Couldn't find ruleset " + ruleSet);
            return null;
        }
    }

    /**
     * Run the rule on the given code, and check the expected number of
     * violations.
     */
    @SuppressWarnings("unchecked")
    public void runTest(TestDescriptor test) {
        Rule rule = test.getRule();

        if (test.getReinitializeRule()) {
            rule = reinitializeRule(rule);
        }

        Map<PropertyDescriptor<?>, Object> oldProperties = rule.getPropertiesByPropertyDescriptor();
        try {
            int res;
            Report report;
            try {
                // Set test specific properties onto the Rule
                if (test.getProperties() != null) {
                    for (Map.Entry<Object, Object> entry : test.getProperties().entrySet()) {
                        String propertyName = (String) entry.getKey();
                        PropertyDescriptor propertyDescriptor = rule.getPropertyDescriptor(propertyName);
                        if (propertyDescriptor == null) {
                            throw new IllegalArgumentException(
                                    "No such property '" + propertyName + "' on Rule " + rule.getName());
                        }

                        Object value = propertyDescriptor.valueFrom((String) entry.getValue());
                        rule.setProperty(propertyDescriptor, value);
                    }
                }

                report = processUsingStringReader(test, rule);
                res = report.getViolations().size();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException('"' + test.getDescription() + "\" failed", e);
            }
            if (test.getNumberOfProblemsExpected() != res) {
                printReport(test, report);
            }
            Assertions.assertEquals(test.getNumberOfProblemsExpected(), res,
                    '"' + test.getDescription() + "\" resulted in wrong number of failures,");
            assertMessages(report, test);
            assertLineNumbers(report, test);
        } finally {
            // Restore old properties
            for (Map.Entry<PropertyDescriptor<?>, Object> entry : oldProperties.entrySet()) {
                rule.setProperty((PropertyDescriptor) entry.getKey(), entry.getValue());
            }
        }
    }


    /**
     * Code to be executed if the rule is reinitialised.
     *
     * @param rule The rule to reinitialise
     *
     * @return The rule once it has been reinitialised
     */
    protected Rule reinitializeRule(Rule rule) {
        return findRule(rule.getRuleSetName(), rule.getName());
    }


    private void assertMessages(Report report, TestDescriptor test) {
        if (report == null || test.getExpectedMessages().isEmpty()) {
            return;
        }

        List<String> expectedMessages = test.getExpectedMessages();
        if (report.getViolations().size() != expectedMessages.size()) {
            throw new RuntimeException("Test setup error: number of expected messages doesn't match "
                    + "number of violations for test case '" + test.getDescription() + "'");
        }

        int index = 0;
        for (RuleViolation violation : report.getViolations()) {
            String actual = violation.getDescription();
            if (!expectedMessages.get(index).equals(actual)) {
                printReport(test, report);
            }
            Assertions.assertEquals(expectedMessages.get(index), actual,
                    '"' + test.getDescription() + "\" produced wrong message on violation number " + (index + 1) + ".");
            index++;
        }
    }

    private void assertLineNumbers(Report report, TestDescriptor test) {
        if (report == null || test.getExpectedLineNumbers().isEmpty()) {
            return;
        }

        List<Integer> expected = test.getExpectedLineNumbers();
        if (report.getViolations().size() != expected.size()) {
            throw new RuntimeException("Test setup error: number of expected line numbers " + expected.size()
                    + " doesn't match number of violations " + report.getViolations().size() + " for test case '"
                    + test.getDescription() + "'");
        }

        int index = 0;
        for (RuleViolation violation : report.getViolations()) {
            Integer actual = violation.getBeginLine();
            if (expected.get(index) != actual.intValue()) {
                printReport(test, report);
            }
            Assertions.assertEquals(expected.get(index), actual,
                    '"' + test.getDescription() + "\" violation on wrong line number: violation number "
                    + (index + 1) + ".");
            index++;
        }
    }

    private void printReport(TestDescriptor test, Report report) {
        System.out.println("--------------------------------------------------------------");
        System.out.println("Test Failure: " + test.getDescription());
        System.out.println(" -> Expected " + test.getNumberOfProblemsExpected() + " problem(s), " + report.getViolations().size()
                + " problem(s) found.");
        System.out.println(" -> Expected messages: " + test.getExpectedMessages());
        System.out.println(" -> Expected line numbers: " + test.getExpectedLineNumbers());
        System.out.println("Test Method Name: " + test.getTestMethodName());
        System.out.println("    @org.junit.Test public void " + test.getTestMethodName() + "() {}");
        System.out.println();
        TextRenderer renderer = new TextRenderer();
        renderer.setWriter(new StringWriter());
        try {
            renderer.start();
            renderer.renderFileReport(report);
            renderer.end();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(renderer.getWriter().toString());
        System.out.println("--------------------------------------------------------------");
    }

    private Report processUsingStringReader(TestDescriptor test, Rule rule) {
        return runTestFromString(test.getCode(), rule, test.getLanguageVersion(), test.isUseAuxClasspath());
    }

    public Report runTestFromString(String code, Rule rule, LanguageVersion languageVersion, boolean isUseAuxClasspath) {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setIgnoreIncrementalAnalysis(true);
        configuration.setDefaultLanguageVersion(languageVersion);
        configuration.setThreads(1);
        configuration.setIgnoreIncrementalAnalysis(true);


        if (isUseAuxClasspath) {
            // configure the "auxclasspath" option for unit testing
            // we share a single classloader so that pmd-java doesn't create
            // a new TypeSystem for every test. This problem will go
            // away when languages have a lifecycle.
            configuration.setClassLoader(classpathClassLoader);
        } else {
            // simple class loader, that doesn't delegate to parent.
            // this allows us in the tests to simulate PMD run without
            // auxclasspath, not even the classes from the test dependencies
            // will be found.
            configuration.setClassLoader(new ClassLoader() {
                @Override
                protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    if (name.startsWith("java.") || name.startsWith("javax.")) {
                        return super.loadClass(name, resolve);
                    }
                    throw new ClassNotFoundException(name);
                }
            });
        }

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.files().addFile(TextFile.forCharSeq(code, "testFile", languageVersion));
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            pmd.addListener(GlobalAnalysisListener.exceptionThrower());
            return pmd.performAnalysisAndCollectReport();
        }
    }

    /**
     * getResourceAsStream tries to find the XML file in weird locations if the
     * ruleName includes the package, so we strip it here.
     */
    protected String getCleanRuleName(Rule rule) {
        String fullClassName = rule.getClass().getName();
        if (fullClassName.equals(rule.getName())) {
            // We got the full class name, so we'll use the stripped name
            // instead
            String packageName = rule.getClass().getPackage().getName();
            return fullClassName.substring(packageName.length() + 1);
        } else {
            return rule.getName(); // Test is using findRule, smart!
        }
    }

    /**
     * Extract a set of tests from an XML file. The file should be
     * ./xml/RuleName.xml relative to the test class. The format is defined in
     * test-data.xsd.
     */
    public TestDescriptor[] extractTestsFromXml(Rule rule) {
        String testsFileName = getCleanRuleName(rule);

        return extractTestsFromXml(rule, testsFileName);
    }

    public TestDescriptor[] extractTestsFromXml(Rule rule, String testsFileName) {
        return extractTestsFromXml(rule, testsFileName, "xml/");
    }

    /**
     * Extract a set of tests from an XML file with the given name. The file
     * should be ./xml/[testsFileName].xml relative to the test class. The
     * format is defined in test-data.xsd.
     */
    public TestDescriptor[] extractTestsFromXml(Rule rule, String testsFileName, String baseDirectory) {
        String testXmlFileName = baseDirectory + testsFileName + ".xml";

        Document doc;
        List<Integer> lineNumbersForTests;
        try (InputStream inputStream = getClass().getResourceAsStream(testXmlFileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Couldn't find " + testXmlFileName);
            }
            String testXml = IOUtil.readToString(inputStream, StandardCharsets.UTF_8);
            lineNumbersForTests = determineLineNumbers(testXml);
            try (StringReader r = new StringReader(testXml)) {
                doc = documentBuilder.parse(new InputSource(r));
            }
        } catch (FactoryConfigurationError | IOException | SAXException e) {
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + e, e);
        }

        return parseTests(rule, doc, testXmlFileName, lineNumbersForTests);
    }

    private List<Integer> determineLineNumbers(String testXml) {
        List<Integer> tests = new ArrayList<>();
        int lineNumber = 1;
        int index = 0;
        while (index < testXml.length()) {
            char c = testXml.charAt(index);
            if (c == '\n') {
                lineNumber++;
            } else if (c == '<') {
                if (testXml.startsWith("<test-code", index)) {
                    tests.add(lineNumber);
                }
            }
            index++;
        }
        return tests;
    }

    /**
     * Run a set of tests defined in an XML test-data file for a rule. The file
     * should be ./xml/RuleName.xml relative to the test-class. The format is
     * defined in test-data.xsd.
     */
    public void runTests(Rule rule) {
        runTests(extractTestsFromXml(rule));
    }

    /**
     * Run a set of tests defined in a XML test-data file. The file should be
     * ./xml/[testsFileName].xml relative to the test-class. The format is
     * defined in test-data.xsd.
     */
    public void runTests(Rule rule, String testsFileName) {
        runTests(extractTestsFromXml(rule, testsFileName));
    }

    /**
     * Run a set of tests of a certain sourceType.
     */
    public void runTests(TestDescriptor[] tests) {
        for (TestDescriptor test : tests) {
            runTest(test);
        }
    }

    private TestDescriptor[] parseTests(Rule rule, Document doc, String testXmlFileName, List<Integer> lineNumbersForTests) {
        Element root = doc.getDocumentElement();
        NodeList testCodes = root.getElementsByTagName("test-code");

        String absoluteUriToTestXmlFile = new File(".").getAbsoluteFile().toURI() + "/src/test/resources/"
                + this.getClass().getPackage().getName().replaceAll("\\.", "/")
                + "/" + testXmlFileName;

        TestDescriptor[] tests = new TestDescriptor[testCodes.getLength()];
        for (int i = 0; i < testCodes.getLength(); i++) {
            Element testCode = (Element) testCodes.item(i);

            boolean reinitializeRule = true;
            Node reinitializeRuleAttribute = testCode.getAttributes().getNamedItem("reinitializeRule");
            if (reinitializeRuleAttribute != null) {
                String reinitializeRuleValue = reinitializeRuleAttribute.getNodeValue();
                if ("false".equalsIgnoreCase(reinitializeRuleValue) || "0".equalsIgnoreCase(reinitializeRuleValue)) {
                    reinitializeRule = false;
                }
            }

            boolean isRegressionTest = true;
            Node regressionTestAttribute = testCode.getAttributes().getNamedItem("regressionTest");
            if (regressionTestAttribute != null) {
                String reinitializeRuleValue = regressionTestAttribute.getNodeValue();
                if ("false".equalsIgnoreCase(reinitializeRuleValue)) {
                    isRegressionTest = false;
                }
            }

            boolean isUseAuxClasspath = true;
            Node useAuxClasspathAttribute = testCode.getAttributes().getNamedItem("useAuxClasspath");
            if (useAuxClasspathAttribute != null) {
                String useAuxClasspathValue = useAuxClasspathAttribute.getNodeValue();
                if ("false".equalsIgnoreCase(useAuxClasspathValue)) {
                    isUseAuxClasspath = false;
                }
            }

            NodeList ruleProperties = testCode.getElementsByTagName("rule-property");
            Properties properties = new Properties();
            for (int j = 0; j < ruleProperties.getLength(); j++) {
                Node ruleProperty = ruleProperties.item(j);
                String propertyName = ruleProperty.getAttributes().getNamedItem("name").getNodeValue();
                properties.setProperty(propertyName, parseTextNode(ruleProperty));
            }

            NodeList expectedMessagesNodes = testCode.getElementsByTagName("expected-messages");
            List<String> messages = new ArrayList<>();
            if (expectedMessagesNodes != null && expectedMessagesNodes.getLength() > 0) {
                Element item = (Element) expectedMessagesNodes.item(0);
                NodeList messagesNodes = item.getElementsByTagName("message");
                for (int j = 0; j < messagesNodes.getLength(); j++) {
                    messages.add(parseTextNode(messagesNodes.item(j)));
                }
            }

            NodeList expectedLineNumbersNodes = testCode.getElementsByTagName("expected-linenumbers");
            List<Integer> expectedLineNumbers = new ArrayList<>();
            if (expectedLineNumbersNodes != null && expectedLineNumbersNodes.getLength() > 0) {
                Element item = (Element) expectedLineNumbersNodes.item(0);
                String numbers = item.getTextContent();
                for (String n : numbers.split(" *, *")) {
                    expectedLineNumbers.add(Integer.valueOf(n));
                }
            }

            String code = getNodeValue(testCode, "code", false);
            if (code == null) {
                // Should have a coderef
                NodeList coderefs = testCode.getElementsByTagName("code-ref");
                if (coderefs.getLength() == 0) {
                    throw new RuntimeException(
                            "Required tag is missing from the test-xml. Supply either a code or a code-ref tag");
                }
                Node coderef = coderefs.item(0);
                String referenceId = coderef.getAttributes().getNamedItem("id").getNodeValue();
                NodeList codeFragments = root.getElementsByTagName("code-fragment");
                for (int j = 0; j < codeFragments.getLength(); j++) {
                    String fragmentId = codeFragments.item(j).getAttributes().getNamedItem("id").getNodeValue();
                    if (referenceId.equals(fragmentId)) {
                        code = parseTextNode(codeFragments.item(j));
                    }
                }

                if (code == null) {
                    throw new RuntimeException("No matching code fragment found for coderef");
                }
            }
            code = Chars.wrap(code).trimBlankLines().toString();

            String description = getNodeValue(testCode, "description", true);
            int expectedProblems = Integer.parseInt(getNodeValue(testCode, "expected-problems", true).trim());

            String languageVersionString = getNodeValue(testCode, "source-type", false);
            if (languageVersionString == null) {
                tests[i] = new TestDescriptor(code, description, expectedProblems, rule);
            } else {
                languageVersionString = languageVersionString.trim();
                LanguageVersion languageVersion = parseSourceType(languageVersionString);
                if (languageVersion != null) {
                    tests[i] = new TestDescriptor(code, description, expectedProblems, rule, languageVersion);
                } else {
                    throw new RuntimeException("Unknown LanguageVersion for test: " + languageVersionString);
                }
            }
            tests[i].setReinitializeRule(reinitializeRule);
            tests[i].setRegressionTest(isRegressionTest);
            tests[i].setUseAuxClasspath(isUseAuxClasspath);
            tests[i].setExpectedMessages(messages);
            tests[i].setExpectedLineNumbers(expectedLineNumbers);
            tests[i].setProperties(properties);
            tests[i].setNumberInDocument(i + 1);
            tests[i].setTestSourceUri(absoluteUriToTestXmlFile, lineNumbersForTests.get(i));
        }
        return tests;
    }

    /** FIXME this is stupid, the language version may be of a different language than the Rule... */
    private LanguageVersion parseSourceType(String terseNameAndVersion) {
        final String version;
        final String terseName;
        if (terseNameAndVersion.contains(" ")) {
            version = StringUtils.trimToNull(terseNameAndVersion.substring(terseNameAndVersion.lastIndexOf(' ') + 1));
            terseName = terseNameAndVersion.substring(0, terseNameAndVersion.lastIndexOf(' '));
        } else {
            version = null;
            terseName = terseNameAndVersion;
        }
        return LanguageRegistry.PMD.getLanguageVersionById(terseName, version);
    }

    private String getNodeValue(Element parentElm, String nodeName, boolean required) {
        NodeList nodes = parentElm.getElementsByTagName(nodeName);
        if (nodes == null || nodes.getLength() == 0) {
            if (required) {
                throw new RuntimeException("Required tag is missing from the test-xml: " + nodeName);
            } else {
                return null;
            }
        }
        Node node = nodes.item(0);
        return parseTextNode(node);
    }

    private static String parseTextNode(Node exampleNode) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < exampleNode.getChildNodes().getLength(); i++) {
            Node node = exampleNode.getChildNodes().item(i);
            if (node.getNodeType() == Node.CDATA_SECTION_NODE || node.getNodeType() == Node.TEXT_NODE) {
                buffer.append(node.getNodeValue());
            }
        }
        return buffer.toString();
    }

    @TestFactory
    Collection<DynamicTest> ruleTests() {
        setUp();
        final List<Rule> rules = new ArrayList<>(getRules());
        rules.sort(Comparator.comparing(Rule::getName));

        final List<TestDescriptor> tests = new LinkedList<>();
        for (final Rule r : rules) {
            final TestDescriptor[] ruleTests = extractTestsFromXml(r);
            Collections.addAll(tests, ruleTests);
        }

        return tests.stream().map(this::toDynamicTest).collect(Collectors.toList());
    }

    private DynamicTest toDynamicTest(TestDescriptor testDescriptor) {
        if (isIgnored(testDescriptor)) {
            return DynamicTest.dynamicTest("[IGNORED] " + testDescriptor.getTestMethodName(),
                    testDescriptor.getTestSourceUri(),
                    () -> {});
        }
        return DynamicTest.dynamicTest(testDescriptor.getTestMethodName(),
                testDescriptor.getTestSourceUri(),
                () -> runTest(testDescriptor));
    }

    private static boolean isIgnored(TestDescriptor testDescriptor) {
        return TestDescriptor.inRegressionTestMode() && !testDescriptor.isRegressionTest();
    }
}
