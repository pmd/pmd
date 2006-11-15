
 /**
  * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
  */
 package test.net.sourceforge.pmd.rules;
 
 import net.sourceforge.pmd.Rule;
 import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
 
 public class ConstructorCallsOverridableMethodTest extends SimpleAggregatorTst {
     private Rule rule;
 
     public void setUp() {
         rule = findRule("design", "ConstructorCallsOverridableMethod");
     }
 
     public void testAll() {
         runTests(rule);
         //FIXME new TestDescriptor(BUG_985989, "bug report 985989, ", 1, rule),
     }
 
     /*private static final String BUG_985989 =
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
             "}";*/
 }
 
 
 
