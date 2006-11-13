/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SimpleRuleSetNameMapper;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.SourceTypeToRuleLanguageMapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RuleTst extends TestCase {
    public static final SourceType DEFAULT_SOURCE_TYPE = SourceType.JAVA_14;

    public void runTestFromString(TestDescriptor test) throws Throwable {
        runTestFromString(test, DEFAULT_SOURCE_TYPE);
    }
    
    /**
     * @deprecated use runTestFromString(TestDescriptor test)
     */
    public void runTestFromString(String code, int numberOfProblemsExpected, Rule rule) throws Throwable {
        TestDescriptor test = new TestDescriptor(code, "", numberOfProblemsExpected, rule);
        runTestFromString(test, DEFAULT_SOURCE_TYPE);
    }

    public Rule findRule(String rs, String r) {
        try {
            Rule rule = new RuleSetFactory().createRuleSets(new SimpleRuleSetNameMapper(rs).getRuleSets()).getRuleByName(r);
            if (rule == null) {
                fail("Rule " + r + " not found in ruleset " + rs);
            }
            return rule;
        } catch (RuleSetNotFoundException e) {
            e.printStackTrace();        
            fail("Couldn't find ruleset " + rs);
            return null;
        }
    }


    /**
     * Run the rule on the given code, and check the expected number of violations.
     *
     * @param code
     * @param expectedResults
     * @param rule
     * @throws Throwable
     */
    public void runTestFromString(TestDescriptor test,
                                  SourceType sourceType) throws Throwable {
        int res = processUsingStringReader(test.getCode(), test.getRule(), sourceType).size();
        assertEquals("\"" + test.getDescription() + "\" test resulted in wrong number of failures,",
            test.getNumberOfProblemsExpected(), res);
    }

    private Report processUsingStringReader(String code, Rule rule,
                                            SourceType sourceType) throws Throwable {
        Report report = new Report();
        runTestFromString(code, rule, report, sourceType);
        return report;
    }

    /**
     * Run the rule on the given code, and put the violations in the given report.
     *
     * @param code
     * @param rule
     * @param report 
     * @throws Throwable
     */
    public void runTestFromString(String code, Rule rule, Report report, SourceType sourceType) throws Throwable {
        PMD p = new PMD();
        p.setJavaVersion(sourceType);
        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFilename("n/a");
        RuleSet rules = new RuleSet();
        rules.addRule(rule);
        rules.setLanguage(SourceTypeToRuleLanguageMapper.getMappedLanguage(sourceType));
        p.processFile(new StringReader(code), new RuleSets(rules), ctx, sourceType);
    }

    public TestDescriptor[] extractTestsFromXml(Rule rule) {
        String testXmlFileName = "xml/" + rule.getName() + ".xml";
        InputStream inputStream = getClass().getResourceAsStream(testXmlFileName);
        Document doc;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(inputStream);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            throw new RuntimeException("Couldn't find " + testXmlFileName + ", due to: " + pce.getMessage());
        } catch (FactoryConfigurationError fce) {
            fce.printStackTrace();
            throw new RuntimeException("Couldn't find " + testXmlFileName + ", due to: " + fce.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Couldn't find " + testXmlFileName + ", due to: " + ioe.getMessage());
        } catch (SAXException se) {
            se.printStackTrace();
            throw new RuntimeException("Couldn't find " + testXmlFileName + ", due to: " + se.getMessage());
        }

        Element root = doc.getDocumentElement();
        NodeList testCodes = root.getElementsByTagName("test-code");
        TestDescriptor[] tests = new TestDescriptor[testCodes.getLength()];
        for (int i = 0; i < testCodes.getLength(); i++) {
            Element testCode = (Element)testCodes.item(i);
            int expectedProblems = Integer.parseInt(getNodeValue(testCode, "expected-problems"));
            String description = getNodeValue(testCode, "description");
            String code = getNodeValue(testCode, "code");
            
            tests[i] = new TestDescriptor(code, description, expectedProblems, rule);
        }
        return tests;
    }

    private String getNodeValue(Element parentElm, String nodeName) {
        NodeList nodes = parentElm.getElementsByTagName(nodeName);
        if (nodes == null || nodes.getLength() == 0) {
            throw new RuntimeException("Required tag is missing from the test-xml: " + nodeName);
        }
        Node node = nodes.item(0);
        String value = parseTextNode(node);
        return value.trim();
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
        return buffer.toString();
    }


    public void runTestFromString(String code, Rule rule, Report report) throws Throwable {
        runTestFromString(code, rule, report, DEFAULT_SOURCE_TYPE);
    }

    public void runTestFromString15(String code, Rule rule, Report report) throws Throwable {
        runTestFromString(code, rule, report, SourceType.JAVA_15);
    }

    public void runTestFromString13(String code, Rule rule, Report report) throws Throwable {
        runTestFromString(code, rule, report, SourceType.JAVA_13);
    }
}
