
 /*
  * Created on Jan 10, 2005 
  *
  * $Id$
  */
 package test.net.sourceforge.pmd.rules.optimizations;
 
 import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 /**
  * Tests for the rule LocalVariableCouldBeFinal
  *
  * @author mgriffa
  */
 public class LocalVariableCouldBeFinalTest extends SimpleAggregatorTst {
 
     private Rule rule;

     @Before
     public void setUp() {
         rule = findRule("optimizations", "LocalVariableCouldBeFinal");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(LocalVariableCouldBeFinalTest.class);
     }
 }
