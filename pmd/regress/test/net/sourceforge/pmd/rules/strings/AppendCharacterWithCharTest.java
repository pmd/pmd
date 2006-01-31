/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class AppendCharacterWithCharTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("strings", "AppendCharacterWithChar");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
               new TestDescriptor(TEST1, "appending single character string, should fail", 1, rule),
               new TestDescriptor(TEST2, "appending single char, should be ok", 0, rule),
               new TestDescriptor(TEST3, "this is probably wrong, but shouldn't fail", 0, rule),
               new TestDescriptor(TEST4, "concatenates a three character int", 0, rule),
               new TestDescriptor(TEST5, "concatenates a string explicitly set to 1 character, not explicitly checking right now", 0, rule),
               new TestDescriptor(TEST6, "for statement", 1, rule),
               new TestDescriptor(TEST7, "concatenates an escaped character", 1, rule),
               new TestDescriptor(TEST8, "concatenates a single upper case", 1, rule),
               new TestDescriptor(TEST9, "concatenates a single number", 1, rule),
               new TestDescriptor(TEST10, "concatenates a single character &", 1, rule),
               new TestDescriptor(TEST11, "concatenates two characters", 0, rule),
       });
    }
    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  sb.append(\"a\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  sb.append('a');" + PMD.EOL +
        " }" + PMD.EOL +
        "}";


    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  sb.append(\"a\" + \"foo\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
    

    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  sb.append(123);" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  String str = \"a\";" + PMD.EOL +
        "  sb.append(str);" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  for(int ix = 0; ix < 100; ix++) "+
        "      sb.append(\"a\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST7 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  sb.append(\"\\t\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST8 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  sb.append(\"A\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST9 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  sb.append(\"3\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST10 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  sb.append(\"&\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST11 =
        "public class Foo {" + PMD.EOL +
        " public void bar(StringBuffer sb) {" + PMD.EOL +
        "  sb.append(\"/t\");" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

}
