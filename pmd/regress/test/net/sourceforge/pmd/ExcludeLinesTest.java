
 package test.net.sourceforge.pmd;
 
 import java.io.StringReader;
 
 import net.sourceforge.pmd.PMD;
 import net.sourceforge.pmd.Report;
 import net.sourceforge.pmd.Rule;
 import net.sourceforge.pmd.RuleContext;
 import net.sourceforge.pmd.RuleSet;
 import net.sourceforge.pmd.RuleSets;
 import net.sourceforge.pmd.SourceTypeToRuleLanguageMapper;
 import test.net.sourceforge.pmd.testframework.RuleTst;
 import test.net.sourceforge.pmd.testframework.TestDescriptor;
 
 public class ExcludeLinesTest extends RuleTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("unusedcode", "UnusedLocalVariable");
     }
 
     public void testAcceptance() {
         runTest(new TestDescriptor(TEST1, "NOPMD should work", 0, rule));
         runTest(new TestDescriptor(TEST2, "Should fail without exclude marker", 1, rule));
     }
 
     public void testAlternateMarker() throws Throwable {
         PMD p = new PMD();
         p.setExcludeMarker("FOOBAR");
         RuleContext ctx = new RuleContext();
         Report r = new Report();
         ctx.setReport(r);
         ctx.setSourceCodeFilename("n/a");
         RuleSet rules = new RuleSet();
         rules.addRule(rule);
         rules.setLanguage(SourceTypeToRuleLanguageMapper.getMappedLanguage(DEFAULT_SOURCE_TYPE));
         p.processFile(new StringReader(TEST3), new RuleSets(rules), ctx, DEFAULT_SOURCE_TYPE);
         assertTrue(r.isEmpty());
         assertEquals(r.getSuppressedRuleViolations().size(), 1);
     }
 
     private static final String TEST1 =
             "public class Foo {" + PMD.EOL +
             " void foo() {" + PMD.EOL +
             "  int x; //NOPMD " + PMD.EOL +
             " } " + PMD.EOL +
             "}";
 
     private static final String TEST2 =
             "public class Foo {" + PMD.EOL +
             " void foo() {" + PMD.EOL +
             "  int x;" + PMD.EOL +
             " } " + PMD.EOL +
             "}";
 
     private static final String TEST3 =
             "public class Foo {" + PMD.EOL +
             " void foo() {" + PMD.EOL +
             "  int x; // FOOBAR" + PMD.EOL +
             " } " + PMD.EOL +
             "}";
 
 }
