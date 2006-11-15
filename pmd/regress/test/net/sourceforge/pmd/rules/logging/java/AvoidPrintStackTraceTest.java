
 package test.net.sourceforge.pmd.rules.logging.java;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class AvoidPrintStackTraceTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("logging-java", "AvoidPrintStackTrace");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
