
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.javabeans;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class MissingSerialVersionUIDTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() throws Exception {
         rule = findRule("javabeans", "MissingSerialVersionUID");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
