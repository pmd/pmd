/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.testframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.renderers.TextRenderer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Advanced methods for test cases
 */
public abstract class RuleTst {
    /**
     * Find a rule in a certain ruleset by name
     */
    public Rule findRule(String ruleSet, String ruleName) {
        try {
            Rule rule = new RuleSetFactory().createRuleSets(ruleSet).getRuleByName(ruleName);
            if (rule == null) {
                fail("Rule " + ruleName + " not found in ruleset " + ruleSet);
            }
            rule.setRuleSetName(ruleSet);
            return rule;
        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();
            fail("Couldn't find ruleset " + ruleSet);
            return null;
        }
    }


    /**
     * Run the rule on the given code, and check the expected number of violations.
     */
    @SuppressWarnings("unchecked")
    public void runTest(TestDescriptor test) {
        Rule rule = test.getRule();

        if (test.getReinitializeRule()) {
            rule = findRule(rule.getRuleSetName(), rule.getName());
        }

        Map<PropertyDescriptor<?>, Object> oldProperties = rule.getPropertiesByPropertyDescriptor();
        try {
            int res;
            Report report;
            try {
        	// Set test specific properties onto the Rule
                if (test.getProperties() != null) {
                    for (Map.Entry<Object, Object> entry : test.getProperties().entrySet()) {
                	String propertyName = (String)entry.getKey();
                	PropertyDescriptor propertyDescriptor = rule.getPropertyDescriptor(propertyName);
                	if (propertyDescriptor == null) {
                            throw new IllegalArgumentException("No such property '" + propertyName + "' on Rule " + rule.getName());
                	}

                	Object value = propertyDescriptor.valueFrom((String)entry.getValue());
                	rule.setProperty(propertyDescriptor, value);
                    }
                }

                report = processUsingStringReader(test, rule);
                res = report.size();
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException('"' + test.getDescription() + "\" failed", t);
            }
            if (test.getNumberOfProblemsExpected() != res) {
                printReport(test, report);
            }
            assertEquals('"' + test.getDescription() + "\" resulted in wrong number of failures,",
                    test.getNumberOfProblemsExpected(), res);
            assertMessages(report, test);
            assertLineNumbers(report, test);
        } finally {
            //Restore old properties
            // TODO Tried to use generics here, but there's a compiler bug doing so in a finally block.
            // Neither 1.5.0_16-b02 or 1.6.0_07-b06 works, but 1.7.0-ea-b34 seems to work.   
            for (Map.Entry entry: oldProperties.entrySet()) {
        	rule.setProperty((PropertyDescriptor)entry.getKey(), entry.getValue());
            }
        }
    }

    private void assertMessages(Report report, TestDescriptor test) {
        if (report == null || test.getExpectedMessages().isEmpty()) {
            return;
        }

        List<String> expectedMessages = test.getExpectedMessages();
        if (report.size() != expectedMessages.size()) {
            throw new RuntimeException("Test setup error: number of expected messages doesn't match "
                    + "number of violations for test case '" + test.getDescription() + "'");
        }

        Iterator<RuleViolation> it = report.iterator();
        int index = 0;
        while (it.hasNext()) {
            RuleViolation violation = it.next();
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
        if (report.getViolationTree().size() != expected.size()) {
            throw new RuntimeException("Test setup error: number of execpted line numbers doesn't match "
                    + "number of violations for test case '" + test.getDescription() + "'");
        }

        Iterator<RuleViolation> it = report.getViolationTree().iterator();
        int index = 0;
        while (it.hasNext()) {
            RuleViolation violation = it.next();
            Integer actual = violation.getBeginLine();
            if (expected.get(index) != actual.intValue()) {
                printReport(test, report);
            }
            assertEquals(
                    '"' + test.getDescription() + "\" violation on wrong line number: violation number " + (index + 1) + ".",
                    expected.get(index), actual);
            index++;
        }
    }

    private void printReport(TestDescriptor test, Report report) {
        System.out.println("--------------------------------------------------------------");
        System.out.println("Test Failure: " + test.getDescription());
        System.out.println(" -> Expected " + test.getNumberOfProblemsExpected() + " problem(s), "
                + report.size() + " problem(s) found.");
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

    public void runTestFromString(String code, Rule rule, Report report, LanguageVersion languageVersion, boolean isUseAuxClasspath) {
        try {
            PMD p = new PMD();
            p.getConfiguration().setDefaultLanguageVersion(languageVersion);
            if (isUseAuxClasspath) {
                p.getConfiguration().prependClasspath("."); // configure the "auxclasspath" option for unit testing
            }
            RuleContext ctx = new RuleContext();
            ctx.setReport(report);
            ctx.setSourceCodeFilename("n/a");
            ctx.setLanguageVersion(languageVersion);
            ctx.setIgnoreExceptions(false);
            RuleSet rules = new RuleSet();
            rules.addRule(rule);
            rules.start(ctx);
            p.getSourceCodeProcessor().processSourceCode(new StringReader(code), new RuleSets(rules), ctx);
            rules.end(ctx);
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
            //We got the full class name, so we'll use the stripped name instead
            String packageName = rule.getClass().getPackage().getName();
            return fullClassName.substring(packageName.length()+1);
        } else {
            return rule.getName();  //Test is using findRule, smart!
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
     * Extract a set of tests from an XML file with the given name. The file should be
     * ./xml/[testsFileName].xml relative to the test class. The format is defined in
     * test-data.xsd.
     */
    public TestDescriptor[] extractTestsFromXml(Rule rule, String testsFileName, String baseDirectory) {
        String testXmlFileName = baseDirectory + testsFileName + ".xml";
        InputStream inputStream = getClass().getResourceAsStream(testXmlFileName);
        if (inputStream == null) {
            throw new RuntimeException("Couldn't find " + testXmlFileName);
        }

        Document doc;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(inputStream);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + pce.getMessage());
        } catch (FactoryConfigurationError fce) {
            fce.printStackTrace();
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + fce.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + ioe.getMessage());
        } catch (SAXException se) {
            se.printStackTrace();
            throw new RuntimeException("Couldn't parse " + testXmlFileName + ", due to: " + se.getMessage());
        }

        return parseTests(rule, doc);
    }

    private TestDescriptor[] parseTests(Rule rule, Document doc) {
        Element root = doc.getDocumentElement();
        NodeList testCodes = root.getElementsByTagName("test-code");

        TestDescriptor[] tests = new TestDescriptor[testCodes.getLength()];
        for (int i = 0; i < testCodes.getLength(); i++) {
            Element testCode = (Element)testCodes.item(i);

            boolean reinitializeRule = true;
            Node reinitializeRuleAttribute = testCode.getAttributes().getNamedItem("reinitializeRule");
            if (reinitializeRuleAttribute != null) {
                String reinitializeRuleValue = reinitializeRuleAttribute.getNodeValue();
                if ("false".equalsIgnoreCase(reinitializeRuleValue) ||
                        "0".equalsIgnoreCase(reinitializeRuleValue)) {
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
            List<String> messages = new ArrayList<String>();
            if (expectedMessagesNodes != null && expectedMessagesNodes.getLength() > 0) {
                Element item = (Element)expectedMessagesNodes.item(0);
                NodeList messagesNodes = item.getElementsByTagName("message");
                for (int j = 0; j < messagesNodes.getLength(); j++) {
                    messages.add(parseTextNode(messagesNodes.item(j)));
                }
            }

            NodeList expectedLineNumbersNodes = testCode.getElementsByTagName("expected-linenumbers");
            List<Integer> expectedLineNumbers = new ArrayList<Integer>();
            if (expectedLineNumbersNodes != null && expectedLineNumbersNodes.getLength() > 0) {
                Element item = (Element)expectedLineNumbersNodes.item(0);
                String numbers = item.getTextContent();
                for (String n : numbers.split(" *, *")) {
                    expectedLineNumbers.add(Integer.valueOf(n));
                }
            }

            String code = getNodeValue(testCode, "code", false);
            if (code == null) {
                //Should have a coderef
                NodeList coderefs = testCode.getElementsByTagName("code-ref");
                if (coderefs.getLength()==0) {
                    throw new RuntimeException("Required tag is missing from the test-xml. Supply either a code or a code-ref tag");
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

                if (code==null) {
                    throw new RuntimeException("No matching code fragment found for coderef");
                }
            }

            String description = getNodeValue(testCode, "description", true);
            int expectedProblems = Integer.parseInt(getNodeValue(testCode, "expected-problems", true));

            String languageVersionString = getNodeValue(testCode, "source-type", false);
            if (languageVersionString == null) {
                tests[i] = new TestDescriptor(code, description, expectedProblems, rule);
            } else {
            	LanguageVersion languageVersion = LanguageRegistry.findLanguageVersionByTerseName(languageVersionString);
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
            tests[i].setNumberInDocument(i);
        }
        return tests;
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
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < exampleNode.getChildNodes().getLength(); i++) {
            Node node = exampleNode.getChildNodes().item(i);
            if (node.getNodeType() == Node.CDATA_SECTION_NODE
                    || node.getNodeType() == Node.TEXT_NODE) {
                buffer.append(node.getNodeValue());
            }
        }
        return buffer.toString().trim();
    }
}
