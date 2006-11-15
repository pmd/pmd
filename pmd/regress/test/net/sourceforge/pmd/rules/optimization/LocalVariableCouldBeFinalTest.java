
 /*
  * Created on Jan 10, 2005 
  *
  * $Id$
  */
 package test.net.sourceforge.pmd.rules.optimization;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Tests for the rule LocalVariableCouldBeFinal
  *
  * @author mgriffa
  */
 public class LocalVariableCouldBeFinalTest extends SimpleAggregatorTst {
 
     private Rule rule;
 
     public void setUp() {
         rule = findRule("optimizations", "LocalVariableCouldBeFinal");
     }
 
     public void testAll() {
         runTests(rule);
     }
 }
