/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
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

/**
 * Advanced methods for test cases
 */
public class RuleTst extends TestCase {
    public static final SourceType DEFAULT_SOURCE_TYPE = SourceType.JAVA_14;

    /**
     * Find a rule in a certain ruleset by name
     */
    public Rule findRule(String ruleSet, String ruleName) {
        try {
            Rule rule = new RuleSetFactory().createRuleSets(new SimpleRuleSetNameMapper(ruleSet).getRuleSets()).getRuleByName(ruleName);
            rule.setRuleSetName(ruleSet);
            if (rule == null) {
                fail("Rule " + ruleName + " not found in ruleset " + ruleSet);
            }
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
    public void runTest(TestDescriptor test) {
        Rule rule = test.getRule();
        
        if (test.getReinitializeRule()) {
            rule = findRule(rule.getRuleSetName(), rule.getName());
        }
        
        Properties ruleProperties = rule.getProperties();
        Properties oldProperties = (Properties)ruleProperties.clone();
        try {
            if (test.getProperties() != null) {
                oldProperties = (Properties)ruleProperties.clone();
                ruleProperties.putAll(test.getProperties());
            }
            
            int res = processUsingStringReader(test.getCode(), rule, test.getSourceType()).size();
            assertEquals("\"" + test.getDescription() + "\" test resulted in wrong number of failures,",
                test.getNumberOfProblemsExpected(), res);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("Test \"" + test.getDescription()  + "\" failed");
        } finally {
            //Restore old properties
            ruleProperties.clear();
            ruleProperties.putAll(oldProperties);
        }
    }

    private Report processUsingStringReader(String code, Rule rule,
                                            SourceType sourceType) throws PMDException {
        Report report = new Report();
        runTestFromString(code, rule, report, sourceType);
        return report;
    }

    /**
     * Run the rule on the given code and put the violations in the report.
     */
    public void runTestFromString(String code, Rule rule, Report report, SourceType sourceType) throws PMDException {
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
    
    /**
     * getResourceAsStream tries to find the XML file in weird locations if the
     * ruleName includes the package, so we strip it here.
     */
    private String getCleanRuleName(Rule rule) {
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

    /**
     * Extract a set of tests from an XML file with the given name. The file should be
     * ./xml/[testsFileName].xml relative to the test class. The format is defined in
     * test-data.xsd.
     */
    public TestDescriptor[] extractTestsFromXml(Rule rule, String testsFileName) {
        String testXmlFileName = "xml/" + testsFileName + ".xml";
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

            boolean reinitializeRule = false;
            Node reinitializeRuleAttribute = testCode.getAttributes().getNamedItem("reinitializeRule");
            if (reinitializeRuleAttribute != null) {
                String reinitializeRuleValue = reinitializeRuleAttribute.getNodeValue();
                if ("true".equalsIgnoreCase(reinitializeRuleValue) || 
                        "1".equalsIgnoreCase(reinitializeRuleValue)) {
                    reinitializeRule = true;
                }
            }
            
            NodeList ruleProperties = testCode.getElementsByTagName("rule-property");
            Properties properties = new Properties();
            for (int j = 0; j < ruleProperties.getLength(); j++) {
                Node ruleProperty = ruleProperties.item(j);
                String propertyName = ruleProperty.getAttributes().getNamedItem("name").getNodeValue();
                properties.setProperty(propertyName, parseTextNode(ruleProperty));
            }
            int expectedProblems = Integer.parseInt(getNodeValue(testCode, "expected-problems", true));
            String description = getNodeValue(testCode, "description", true);
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
            
            String sourceTypeString = getNodeValue(testCode, "source-type", false);
            if (sourceTypeString == null) {
                tests[i] = new TestDescriptor(code, description, expectedProblems, rule);
            } else {
                SourceType sourceType = SourceType.getSourceTypeForId(sourceTypeString);
                if (sourceType != null) {
                    tests[i] = new TestDescriptor(code, description, expectedProblems, rule, sourceType);
                } else {
                    throw new RuntimeException("Unknown sourceType for test: " + sourceTypeString);
                }
            }
            tests[i].setReinitializeRule(reinitializeRule);
            tests[i].setProperties(properties);
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
    
    /**
     * Run the test using the DEFAULT_SOURCE_TYPE and put the violations in the report.
     * Convenience method.
     */
    public void runTestFromString(String code, Rule rule, Report report) throws PMDException {
        runTestFromString(code, rule, report, DEFAULT_SOURCE_TYPE);
    }
}
