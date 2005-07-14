/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AccessorClassGenerationTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "AccessorClassGeneration");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "inner class has private constructor", 1, rule),
           new TestDescriptor(TEST2, "inner class has public constructor", 0, rule),
           new TestDescriptor(TEST3, "outer class has public constructor", 1, rule),
           new TestDescriptor(TEST4, "final inner class", 0, rule),

       });
    }

    private static final String TEST1 =
    "public class Foo1 {" + PMD.EOL +
    " public class InnerClass {" + PMD.EOL +
    "   private InnerClass(){" + PMD.EOL +
    "   }" + PMD.EOL +
    " }" + PMD.EOL +
    " void method(){" + PMD.EOL +
    "   new InnerClass();//Causes generation of accessor" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo2 {" + PMD.EOL +
    " public class InnerClass {" + PMD.EOL +
    "   public InnerClass(){" + PMD.EOL +
    "   }" + PMD.EOL +
    " }" + PMD.EOL +
    " void method(){" + PMD.EOL +
    "   new InnerClass(); //OK, due to public constructor" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo3 {" + PMD.EOL +
    "    public class InnerClass {" + PMD.EOL +
    "      void method(){" + PMD.EOL +
    "        new Foo3();//Causes generation of accessor" + PMD.EOL +
    "      }" + PMD.EOL +
    "    }" + PMD.EOL +
    "    private Foo3() {" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void method() {" + PMD.EOL +
    "   final class Inner {}; " + PMD.EOL +
    "   Inner i = new Inner();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
