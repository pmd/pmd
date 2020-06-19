/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RulesetsFactoryUtils;
import net.sourceforge.pmd.internal.util.xml.SchemaConstants;
import net.sourceforge.pmd.internal.util.xml.XmlErrorMessages;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.testframework.internal.TestSchemaConstants;

import com.github.oowekyala.ooxml.messages.DefaultXmlErrorReporter;
import com.github.oowekyala.ooxml.messages.PositionedXmlDoc;
import com.github.oowekyala.ooxml.messages.XmlErrorReporter;
import com.github.oowekyala.ooxml.messages.XmlMessageHandler;
import com.github.oowekyala.ooxml.messages.XmlMessageUtils;

/**
 * Advanced methods for test cases
 */
public abstract class RuleTst {
    private final DocumentBuilder documentBuilder;

    public RuleTst() {
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
            Rule rule = RulesetsFactoryUtils.defaultFactory().createRuleSets(ruleSet).getRuleByName(ruleName);
            if (rule == null) {
                fail("Rule " + ruleName + " not found in ruleset " + ruleSet);
            } else {
                rule.setRuleSetName(ruleSet);
            }
            return rule;
        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();
            fail("Couldn't find ruleset " + ruleSet);
            return null;
        }
    }

    /**
     * Run the rule on the given code, and check the expected number of
     * violations.
     */
    public void runTest(TestDescriptor test) {
        Rule rule = test.getRule();

        int res;
        Report report;
        try {
            report = processUsingStringReader(test, rule);
            res = report.getViolations().size();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException('"' + test.getDescription() + "\" failed", e);
        }
        if (test.getNumberOfProblemsExpected() != res) {
            printReport(test, report);
        }
        assertEquals('"' + test.getDescription() + "\" resulted in wrong number of failures,",
                     test.getNumberOfProblemsExpected(), res);
        assertMessages(report, test);
        assertLineNumbers(report, test);
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
            assertEquals(
                    '"' + test.getDescription() + "\" produced wrong message on violation number " + (index + 1) + ".",
                    expectedMessages.get(index), actual);
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
            assertEquals('"' + test.getDescription() + "\" violation on wrong line number: violation number "
                    + (index + 1) + ".", expected.get(index), actual);
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

    private Report processUsingStringReader(TestDescriptor test, Rule rule) throws PMDException {
        Report report = new Report();
        runTestFromString(test, rule, report);
        return report;
    }

    /**
     * Run the rule on the given code and put the violations in the report.
     */
    public void runTestFromString(String code, Rule rule, Report report, LanguageVersion languageVersion) {
        runTestFromString(code, rule, report, languageVersion, true);
    }

    public void runTestFromString(String code, Rule rule, Report report, LanguageVersion languageVersion,
            boolean isUseAuxClasspath) {
        try {
            PMD p = new PMD();
            p.getConfiguration().setDefaultLanguageVersion(languageVersion);
            p.getConfiguration().setIgnoreIncrementalAnalysis(true);
            if (isUseAuxClasspath) {
                // configure the "auxclasspath" option for unit testing
                p.getConfiguration().prependClasspath(".");
            } else {
                // simple class loader, that doesn't delegate to parent.
                // this allows us in the tests to simulate PMD run without
                // auxclasspath, not even the classes from the test dependencies
                // will be found.
                p.getConfiguration().setClassLoader(new ClassLoader() {
                    @Override
                    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                        if (name.startsWith("java.") || name.startsWith("javax.")) {
                            return super.loadClass(name, resolve);
                        }
                        throw new ClassNotFoundException(name);
                    }
                });
            }
            RuleContext ctx = new RuleContext();
            ctx.setReport(report);
            ctx.setSourceCodeFile(new File("n/a"));
            ctx.setLanguageVersion(languageVersion);
            ctx.setIgnoreExceptions(false);
            RuleSet rules = RulesetsFactoryUtils.defaultFactory().createSingleRuleRuleSet(rule);
            p.getSourceCodeProcessor().processSourceCode(new StringReader(code), new RuleSets(rules), ctx);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runTestFromString(TestDescriptor test, Rule rule, Report report) {
        runTestFromString(test.getCode(), rule, report, test.getLanguageVersion(), test.isUseAuxClasspath());
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
    @SuppressWarnings("PMD.CloseResource")
    public TestDescriptor[] extractTestsFromXml(Rule rule, String testsFileName, String baseDirectory) {
        String testXmlFileName = baseDirectory + testsFileName + ".xml";
        InputStream inputStream = getClass().getResourceAsStream(testXmlFileName);
        if (inputStream == null) {
            throw new RuntimeException("Couldn't find " + testXmlFileName);
        }

        try (InputStream is = inputStream;
             Reader reader = new BufferedReader(new InputStreamReader(is))) {
            InputSource inputSource = new InputSource(testXmlFileName);
            inputSource.setCharacterStream(reader);
            PositionedXmlDoc positionedXmlDoc = XmlMessageUtils.getInstance().parse(documentBuilder, inputSource, XmlMessageHandler.SYSTEM_ERR);

            try (XmlErrorReporter err = getReporter(positionedXmlDoc)) {
                return parseTests(rule, positionedXmlDoc.getDocument(), err);
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + e, e);
        }

    }

    @NonNull
    private DefaultXmlErrorReporter getReporter(PositionedXmlDoc positionedXmlDoc) {
        return new DefaultXmlErrorReporter(XmlMessageHandler.SYSTEM_ERR, positionedXmlDoc.getPositioner()) {
            @Override
            protected String template(String message, Object... args) {
                return new MessageFormat(message).format(args);
            }
        };
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

    private TestDescriptor[] parseTests(Rule baseRule, Document doc, XmlErrorReporter err) {
        Element root = doc.getDocumentElement();
        NodeList testCodes = root.getElementsByTagName("test-code");

        TestDescriptor[] tests = new TestDescriptor[testCodes.getLength()];
        for (int i = 0; i < testCodes.getLength(); i++) {
            Rule rule = baseRule.deepCopy();
            Element testCode = (Element) testCodes.item(i);

            boolean isRegressionTest = TestSchemaConstants.REGRESSION_TEST.getAsBooleanAttr(testCode, true);
            boolean isUseAuxClasspath = TestSchemaConstants.USE_AUXCLASSPATH.getAsBooleanAttr(testCode, true);

            // set properties on the rule copy
            for (Element ruleProperty : TestSchemaConstants.RULE_PROPERTY.getChildrenIn(testCode)) {
                String name = SchemaConstants.NAME.getAttributeOrThrow(ruleProperty, err);
                PropertyDescriptor<?> descriptor = rule.getPropertyDescriptor(name);
                if (descriptor == null) {
                    throw err.error(ruleProperty, XmlErrorMessages.ERR__PROPERTY_DOES_NOT_EXIST, name, rule.getName());
                }

                setPropertyCapture(rule, descriptor, ruleProperty, err);
            }

            List<String> messages = new ArrayList<>();
            @Nullable Element expectedMessagesNodes = TestSchemaConstants.EXPECTED_MESSAGES.getOptChildIn(testCode, err);
            if (expectedMessagesNodes != null) {
                for (Element message : TestSchemaConstants.MESSAGE.getChildrenIn(expectedMessagesNodes)) {
                    messages.add(parseTextNode(message));
                }
            }

            List<Integer> expectedLineNumbers = new ArrayList<>();
            @Nullable Element expectedLineNumbersNodes = TestSchemaConstants.EXPECTED_LINE_NUMBERS.getOptChildIn(testCode, err);
            if (expectedLineNumbersNodes != null) {
                String numbers = expectedLineNumbersNodes.getTextContent();
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

            String description = getNodeValue(testCode, "description", true);
            int expectedProblems = Integer.parseInt(getNodeValue(testCode, "expected-problems", true));

            String languageVersionString = getNodeValue(testCode, "source-type", false);
            final TestDescriptor descriptor;
            if (languageVersionString == null) {
                descriptor = new TestDescriptor(code, description, expectedProblems, rule);
            } else {

                LanguageVersion languageVersion = parseSourceType(languageVersionString);
                if (languageVersion != null) {
                    descriptor = new TestDescriptor(code, description, expectedProblems, rule, languageVersion);
                } else {
                    throw new RuntimeException("Unknown LanguageVersion for test: " + languageVersionString);
                }
            }

            descriptor.setRegressionTest(isRegressionTest);
            descriptor.setUseAuxClasspath(isUseAuxClasspath);
            descriptor.setExpectedMessages(messages);
            descriptor.setExpectedLineNumbers(expectedLineNumbers);
            descriptor.setNumberInDocument(i + 1);
            tests[i] = descriptor;
        }
        return tests;
    }

    private static <T> void setPropertyCapture(PropertySource properties, PropertyDescriptor<T> descriptor, Element valueElement, XmlErrorReporter err) {
        valueElement.setNodeValue("value");
        T value = descriptor.xmlMapper().fromXml(valueElement, err);
        properties.setProperty(descriptor, value);
    }

    /** FIXME this is stupid, the language version may be of a different language than the Rule... */
    private static LanguageVersion parseSourceType(String terseNameAndVersion) {
        final String version;
        final String terseName;
        if (terseNameAndVersion.contains(" ")) {
            version = StringUtils.trimToNull(terseNameAndVersion.substring(terseNameAndVersion.lastIndexOf(' ') + 1));
            terseName = terseNameAndVersion.substring(0, terseNameAndVersion.lastIndexOf(' '));
        } else {
            version = null;
            terseName = terseNameAndVersion;
        }
        Language language = LanguageRegistry.findLanguageByTerseName(terseName);
        if (language != null) {
            if (version == null) {
                return language.getDefaultVersion();
            } else {
                return language.getVersion(version);
            }
        }
        return null;
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
        return buffer.toString().trim();
    }
}
