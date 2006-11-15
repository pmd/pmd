
 package test.net.sourceforge.pmd.rules.logging.jakartacommons;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class UseCorrectExceptionLoggingTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("rulesets/logging-jakarta-commons.xml", "UseCorrectExceptionLogging");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
