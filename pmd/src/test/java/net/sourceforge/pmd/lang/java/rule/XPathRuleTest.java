package net.sourceforge.pmd.lang.java.rule;
 
import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
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
         rule.setLanguage(Language.JAVA);
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
         RuleViolation rv = (RuleViolation) report.iterator().next();
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
         RuleViolation rv = (RuleViolation) report.iterator().next();
         assertEquals(3, rv.getBeginLine());
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
