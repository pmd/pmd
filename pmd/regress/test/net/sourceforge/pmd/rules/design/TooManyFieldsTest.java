/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class TooManyFieldsTest extends SimpleAggregatorTst  {

    private Rule rule;

    public void setUp() {
        rule = findRule("rulesets/newrules.xml", "TooManyFields");
    }
    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST0, "TEST0", 0, rule),
               new TestDescriptor(TEST1, "TEST1", 1, rule),
               new TestDescriptor(TEST2, "TEST2", 0, rule),
               new TestDescriptor(TEST3, "TEST3", 0, rule),
               new TestDescriptor(TEST4, "TEST4", 0, rule),
               new TestDescriptor(TEST5, "TEST5", 2, rule),
       });
    }
    

    private static final String TEST0 = "public class Foo {" +
    "int a1; " + PMD.EOL +
    "int a2; " + PMD.EOL +
    "int a3; " + PMD.EOL +
    "}";

    private static final String TEST1 = "public class Foo {" +
    "int a1; " + PMD.EOL +
    "int a2; " + PMD.EOL +
    "int a3; " + PMD.EOL +
    "int a4; " + PMD.EOL +
    "int a5; " + PMD.EOL +
    "int a6; " + PMD.EOL +
    "int a7; " + PMD.EOL +
    "int a8; " + PMD.EOL +
    "int a9; " + PMD.EOL +
    "int a10; " + PMD.EOL +
    "int a11; " + PMD.EOL +
    "}";

    private static final String TEST2 = "public class Foo {" +
    "int a1; " + PMD.EOL +
    "int a2; " + PMD.EOL +
    "int a3; " + PMD.EOL +
    "int a4; " + PMD.EOL +
    "int a5; " + PMD.EOL +
    "public class Bar {" + PMD.EOL +
    "int a6; " + PMD.EOL +
    "int a7; " + PMD.EOL +
    "int a8; " + PMD.EOL +
    "int a9; " + PMD.EOL +
    "int a10; " + PMD.EOL +
    "}" +
    "}";

    private static final String TEST3 = "public class Foo {" +
    "int a1; " + PMD.EOL +
    "int a2; " + PMD.EOL +
    "int a3; " + PMD.EOL +
    "int a4; " + PMD.EOL +
    "int a5; " + PMD.EOL +
    "interface Bar {" + PMD.EOL +
    "int a6; " + PMD.EOL +
    "int a7; " + PMD.EOL +
    "int a8; " + PMD.EOL +
    "int a9; " + PMD.EOL +
    "int a10; " + PMD.EOL +
    "}" +
    "}";

    private static final String TEST4 = "public interface Foo {" +
    "int a1; " + PMD.EOL +
    "int a2; " + PMD.EOL +
    "int a3; " + PMD.EOL +
    "int a4; " + PMD.EOL +
    "int a5; " + PMD.EOL +
    "int a6; " + PMD.EOL +
    "int a7; " + PMD.EOL +
    "int a8; " + PMD.EOL +
    "int a9; " + PMD.EOL +
    "int a10; " + PMD.EOL +
    "}";
    
    private static final String TEST5 = "public class Foo {" +
    "public class Bar1 {" + PMD.EOL +
    "int b1; " + PMD.EOL +
    "int b2; " + PMD.EOL +
    "int b3; " + PMD.EOL +
    "int b4; " + PMD.EOL +
    "int b5; " + PMD.EOL +
    "int b6; " + PMD.EOL +
    "int b7; " + PMD.EOL +
    "int b8; " + PMD.EOL +
    "int b9; " + PMD.EOL +
    "int b10; " + PMD.EOL +
    "int b11; " + PMD.EOL +
    "int b12; " + PMD.EOL +
    "}" + PMD.EOL +
    "public class Bar2 {" + PMD.EOL +
    "int b1; " + PMD.EOL +
    "int b2; " + PMD.EOL +
    "int b3; " + PMD.EOL +
    "int b4; " + PMD.EOL +
    "int b5; " + PMD.EOL +
    "int b6; " + PMD.EOL +
    "int b7; " + PMD.EOL +
    "int b8; " + PMD.EOL +
    "int b9; " + PMD.EOL +
    "int b10; " + PMD.EOL +
    "int b11; " + PMD.EOL +
    "int b12; " + PMD.EOL +
    "}" + PMD.EOL +
    "}";

}
