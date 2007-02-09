
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules.design;
 
 import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;
 
 public class ConstructorCallsOverridableMethodTest extends SimpleAggregatorTst {
     private Rule rule;
 
     @Before
     public void setUp() {
         rule = findRule("design", "ConstructorCallsOverridableMethod");
     }
 
     @Test
     public void testAll() {
         runTests(rule);
     }
     
     @Ignore
     @Test
     public void test985989() {
         runTest(new TestDescriptor(BUG_985989, "bug report 985989, ", 1, rule));
     }
 
     private static final String BUG_985989 =
             "public class Test {" + PMD.EOL +
             "public static class SeniorClass {" + PMD.EOL +
             "  public SeniorClass(){" + PMD.EOL +
             "    toString(); //may throw NullPointerException if overridden" + PMD.EOL +
             "  }" + PMD.EOL +
             "  public String toString(){" + PMD.EOL +
             "    return \"IAmSeniorClass\";" + PMD.EOL +
             "  }" + PMD.EOL +
             "}" + PMD.EOL +
             "public static class JuniorClass extends SeniorClass {" + PMD.EOL +
             "  private String name;" + PMD.EOL +
             "  public JuniorClass(){" + PMD.EOL +
             "    super(); //Automatic call leads to NullPointerException" + PMD.EOL +
             "    name = \"JuniorClass\";" + PMD.EOL +
             "  }" + PMD.EOL +
             "  public String toString(){" + PMD.EOL +
             "    return name.toUpperCase();" + PMD.EOL +
             "  }" + PMD.EOL +
             "}" + PMD.EOL +
             "public static void main (String[] args) {" + PMD.EOL +
             "  System.out.println(\": \"+new SeniorClass());" + PMD.EOL +
             "  System.out.println(\": \"+new JuniorClass());" + PMD.EOL +
             "}" + PMD.EOL +
             "}";

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(ConstructorCallsOverridableMethodTest.class);
     }
 }
 
 
 
