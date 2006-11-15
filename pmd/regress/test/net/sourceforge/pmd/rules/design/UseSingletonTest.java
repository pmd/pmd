
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.design;
 
 import net.sourceforge.pmd.IRuleViolation;
 import net.sourceforge.pmd.Report;
 import net.sourceforge.pmd.ReportListener;
 import net.sourceforge.pmd.Rule;
 import net.sourceforge.pmd.stat.Metric;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 import test.net.sourceforge.pmd.testframework.TestDescriptor;
 
 public class UseSingletonTest extends SimpleAggregatorTst implements ReportListener {
 
     private int callbacks;
     private Rule rule;
     private TestDescriptor[] tests;
 
     public void setUp() {
         rule = findRule("design", "UseSingleton");
         tests = extractTestsFromXml(rule);
     }
 
     public void testAll() {
         runTests(tests);
     }
 
     public void testResetState() throws Throwable {
         callbacks = 0;
         Report report = new Report();
         report.addListener(this);
         runTestFromString(tests[2].getCode(), rule, report);
         runTestFromString(tests[3].getCode(), rule, report);
         assertEquals(1, callbacks);
     }
 
     public void ruleViolationAdded(IRuleViolation ruleViolation) {
         callbacks++;
     }
 
     public void metricAdded(Metric metric) {
     }
 }
