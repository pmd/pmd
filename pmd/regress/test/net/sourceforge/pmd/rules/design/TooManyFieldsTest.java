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
               new TestDescriptor(TEST1, "3 fields, max is 10", 0, rule),
               new TestDescriptor(TEST2, "11 fields, bad", 1, rule),
               new TestDescriptor(TEST3, "12 fields, but 6 in inner and 6 in outer", 0, rule),
               new TestDescriptor(TEST4, "outer class, inner interface, both OK", 0, rule),
               new TestDescriptor(TEST5, "interface with 10 fields", 0, rule),
               new TestDescriptor(TEST6, "2 inner classes, each with > 10 fields", 2, rule),
       });
    }
    

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " int a1; " + PMD.EOL +
    " int a2; " + PMD.EOL +
    " int a3; " + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " int a1; " + PMD.EOL +
    " int a2; " + PMD.EOL +
    " int a3; " + PMD.EOL +
    " int a4; " + PMD.EOL +
    " int a5; " + PMD.EOL +
    " int a6; " + PMD.EOL +
    " int a7; " + PMD.EOL +
    " int a8; " + PMD.EOL +
    " int a9; " + PMD.EOL +
    " int a10; " + PMD.EOL +
    " int a11; " + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " int a1; " + PMD.EOL +
    " int a2; " + PMD.EOL +
    " int a3; " + PMD.EOL +
    " int a4; " + PMD.EOL +
    " int a5; " + PMD.EOL +
    " int a6; " + PMD.EOL +
    " public class Bar {" + PMD.EOL +
    "  int a7; " + PMD.EOL +
    "  int a8; " + PMD.EOL +
    "  int a9; " + PMD.EOL +
    "  int a10; " + PMD.EOL +
    "  int a11; " + PMD.EOL +
    "  int a12; " + PMD.EOL +
    " }" +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " int a1; " + PMD.EOL +
    " int a2; " + PMD.EOL +
    " int a3; " + PMD.EOL +
    " int a4; " + PMD.EOL +
    " int a5; " + PMD.EOL +
    " interface Bar {" + PMD.EOL +
    "  int a6; " + PMD.EOL +
    "  int a7; " + PMD.EOL +
    "  int a8; " + PMD.EOL +
    "  int a9; " + PMD.EOL +
    "  int a10; " + PMD.EOL +
    " }" +
    "}";

    private static final String TEST5 =
    "public interface Foo {" + PMD.EOL +
    " int a1; " + PMD.EOL +
    " int a2; " + PMD.EOL +
    " int a3; " + PMD.EOL +
    " int a4; " + PMD.EOL +
    " int a5; " + PMD.EOL +
    " int a6; " + PMD.EOL +
    " int a7; " + PMD.EOL +
    " int a8; " + PMD.EOL +
    " int a9; " + PMD.EOL +
    " int a10; " + PMD.EOL +
    "}";
    
    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " public class Bar1 {" + PMD.EOL +
    "  int b1; " + PMD.EOL +
    "  int b2; " + PMD.EOL +
    "  int b3; " + PMD.EOL +
    "  int b4; " + PMD.EOL +
    "  int b5; " + PMD.EOL +
    "  int b6; " + PMD.EOL +
    "  int b7; " + PMD.EOL +
    "  int b8; " + PMD.EOL +
    "  int b9; " + PMD.EOL +
    "  int b10; " + PMD.EOL +
    "  int b11; " + PMD.EOL +
    "  int b12; " + PMD.EOL +
    " }" + PMD.EOL +
    " public class Bar2 {" + PMD.EOL +
    "  int b1; " + PMD.EOL +
    "  int b2; " + PMD.EOL +
    "  int b3; " + PMD.EOL +
    "  int b4; " + PMD.EOL +
    "  int b5; " + PMD.EOL +
    "  int b6; " + PMD.EOL +
    "  int b7; " + PMD.EOL +
    "  int b8; " + PMD.EOL +
    "  int b9; " + PMD.EOL +
    "  int b10; " + PMD.EOL +
    "  int b11; " + PMD.EOL +
    "  int b12; " + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
