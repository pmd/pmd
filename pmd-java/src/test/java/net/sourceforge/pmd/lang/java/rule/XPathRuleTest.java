/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule;
 
import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.lang.rule.xpath.JaxenXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.SaxonXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.testframework.RuleTst;

import org.junit.Before;
import org.junit.Test;

 /**
  * @author daniels
  */
 public class XPathRuleTest extends RuleTst {
 
     XPathRule rule;
 
     @Before
     public void setUp() {
         rule = new XPathRule();
         rule.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
         rule.setMessage("XPath Rule Failed");
     }
 
     @Test
     public void testPluginname() throws Throwable {
         rule.setXPath("//VariableDeclaratorId[string-length(@Image) < 3]");
         rule.setMessage("{0}");
         PMD p = new PMD();
         RuleContext ctx = new RuleContext();
         Report report = new Report();
         ctx.setReport(report);
         ctx.setSourceCodeFilename("n/a");
         RuleSet rules = new RuleSet();
         rules.addRule(rule);
         p.getSourceCodeProcessor().processSourceCode(new StringReader(TEST1), new RuleSets(rules), ctx);
         RuleViolation rv = report.iterator().next();
         assertEquals("a", rv.getDescription());
     }
 
     @Test
     public void testVariables() throws Throwable {
         rule.setXPath("//VariableDeclaratorId[@Image=$var]");
         rule.setMessage("Avoid vars");
         StringProperty varDescriptor = new StringProperty("var", "Test var", null, 1.0f);
         rule.definePropertyDescriptor(varDescriptor);
         rule.setProperty(varDescriptor, "fiddle");
         PMD p = new PMD();
         RuleContext ctx = new RuleContext();
         Report report = new Report();
         ctx.setReport(report);
         ctx.setSourceCodeFilename("n/a");
         RuleSet rules = new RuleSet();
         rules.addRule(rule);
         p.getSourceCodeProcessor().processSourceCode(new StringReader(TEST2), new RuleSets(rules), ctx);
         RuleViolation rv = report.iterator().next();
         assertEquals(3, rv.getBeginLine());
     }

     /**
      * Test for problem reported in bug #1219 PrimarySuffix/@Image does not work in some cases in xpath 2.0
      * @throws Exception any error
      */
     @Test
     public void testImageOfPrimarySuffix() throws Exception {
         String SUFFIX = "import java.io.File;\n" + 
                 "\n" + 
                 "public class TestSuffix {\n" + 
                 "    public static void main(String args[]) {\n" + 
                 "        new File(\"subdirectory\").list();\n" + 
                 "    }\n" + 
                 "}";
         LanguageVersion language = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion();
         ParserOptions parserOptions = language.getLanguageVersionHandler().getDefaultParserOptions();
         Parser parser = language.getLanguageVersionHandler().getParser(parserOptions);
         ASTCompilationUnit cu = (ASTCompilationUnit)parser.parse("test", new StringReader(SUFFIX));
         RuleContext ruleContext = new RuleContext();
         ruleContext.setLanguageVersion(language);

         String xpath = "//PrimarySuffix[@Image='list']";

         // XPATH version 1.0
         XPathRuleQuery xpathRuleQuery = new JaxenXPathRuleQuery();
         xpathRuleQuery.setXPath(xpath);
         xpathRuleQuery.setProperties(new HashMap<PropertyDescriptor<?>, Object>());
         xpathRuleQuery.setVersion(XPathRuleQuery.XPATH_1_0);
         List<Node> nodes = xpathRuleQuery.evaluate(cu, ruleContext);
         assertEquals(1, nodes.size());

         // XPATH version 1.0 Compatibility
         xpathRuleQuery = new SaxonXPathRuleQuery();
         xpathRuleQuery.setXPath(xpath);
         xpathRuleQuery.setProperties(new HashMap<PropertyDescriptor<?>, Object>());
         xpathRuleQuery.setVersion(XPathRuleQuery.XPATH_1_0_COMPATIBILITY);
         nodes = xpathRuleQuery.evaluate(cu, ruleContext);
         assertEquals(1, nodes.size());

         // XPATH version 2.0
         xpathRuleQuery = new SaxonXPathRuleQuery();
         xpathRuleQuery.setXPath(xpath);
         xpathRuleQuery.setProperties(new HashMap<PropertyDescriptor<?>, Object>());
         xpathRuleQuery.setVersion(XPathRuleQuery.XPATH_2_0);
         nodes = xpathRuleQuery.evaluate(cu, ruleContext);
         assertEquals(1, nodes.size());
     }

     /**
      * Following sibling check: See https://sourceforge.net/p/pmd/bugs/1209/
      * @throws Exception any error
      */
     @Test
     public void testFollowingSibling() throws Exception {
         String SOURCE = "public class dummy {\n" +
                 "  public String toString() {\n" +
                 "    String test = \"bad example\";\n" +
                 "    test = \"a\";\n" +
                 "    return test;\n" +
                 "  }\n" +
                 "}";
         LanguageVersion language = LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion();
         ParserOptions parserOptions = language.getLanguageVersionHandler().getDefaultParserOptions();
         Parser parser = language.getLanguageVersionHandler().getParser(parserOptions);
         ASTCompilationUnit cu = (ASTCompilationUnit)parser.parse("test", new StringReader(SOURCE));
         RuleContext ruleContext = new RuleContext();
         ruleContext.setLanguageVersion(language);

         String xpath = "//Block/BlockStatement/following-sibling::BlockStatement";

         // XPATH version 1.0
         XPathRuleQuery xpathRuleQuery = new JaxenXPathRuleQuery();
         xpathRuleQuery.setXPath(xpath);
         xpathRuleQuery.setProperties(new HashMap<PropertyDescriptor<?>, Object>());
         xpathRuleQuery.setVersion(XPathRuleQuery.XPATH_1_0);
         List<Node> nodes = xpathRuleQuery.evaluate(cu, ruleContext);
         assertEquals(2, nodes.size());
         assertEquals(4, nodes.get(0).getBeginLine());
         assertEquals(5, nodes.get(1).getBeginLine());

         // XPATH version 2.0
         xpathRuleQuery = new SaxonXPathRuleQuery();
         xpathRuleQuery.setXPath(xpath);
         xpathRuleQuery.setProperties(new HashMap<PropertyDescriptor<?>, Object>());
         xpathRuleQuery.setVersion(XPathRuleQuery.XPATH_2_0);
         nodes = xpathRuleQuery.evaluate(cu, ruleContext);
         assertEquals(2, nodes.size());
         assertEquals(4, nodes.get(0).getBeginLine());
         assertEquals(5, nodes.get(1).getBeginLine());
     }

     private static final String TEST1 =
             "public class Foo {" + PMD.EOL +
             " int a;" + PMD.EOL +
             "}";
 
     private static final String TEST2 =
             "public class Foo {" + PMD.EOL +
             " int faddle;" + PMD.EOL +
             " int fiddle;" + PMD.EOL +
             "}";

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(XPathRuleTest.class);
     }
 }
