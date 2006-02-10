/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class InefficientEmptyStringCheckTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("strings", "InefficientEmptyStringCheck");
    }

    public void testAll() {

        runTests(new TestDescriptor[]{new TestDescriptor(TEST1, "test is ok, ok", 0, rule),
                                      new TestDescriptor(TEST2, "String.trim.length is called, should have failed", 1, rule),
                                      new TestDescriptor(TEST3, "String.trim.length not is called, ok", 0, rule),
                                      new TestDescriptor(TEST4, "String.trim.length is called, should have failed", 1, rule),
                                      new TestDescriptor(TEST5, "String.trim.length is called, assigned to int, ok", 0, rule),
                                      new TestDescriptor(TEST6, "String.trim.length is called, assigned to boolean, should have failed", 1, rule),
                                      new TestDescriptor(TEST7, "Using trim.length to find the length and compare to 1, OK", 0, rule),
                                      new TestDescriptor(TEST8, "Passes trim().length() and 0 to another method", 0, rule),
                                      new TestDescriptor(TEST9, "Compares the length against a mathematical function", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            "    void bar() {" + PMD.EOL +
            "        String foo = \"foo\";" + PMD.EOL +
            "        if(foo.length() == 0) {" + PMD.EOL +
            "        // this is bad" + PMD.EOL +
            "        }" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            "    void bar() {" + PMD.EOL +
            "        String foo = \"foo\";" + PMD.EOL +
            "        if(foo.trim().length() == 0) {" + PMD.EOL +
            "        // this is bad" + PMD.EOL +
            "        }" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            "    void bar() {" + PMD.EOL +
            "        String foo = \"foo\";" + PMD.EOL +
            "        if(foo.trim().equals(\"\")) {" + PMD.EOL +
            "        }" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";


    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            "    void bar() {" + PMD.EOL +
            "        String foo = \"foo\";" + PMD.EOL +
            "        while(foo.trim().length()==0) {" + PMD.EOL +
            "        }" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            "    void bar() {" + PMD.EOL +
            "        String foo = \"foo\";" + PMD.EOL +
            "        int i = foo.trim().length();" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            "    void bar() {" + PMD.EOL +
            "        String foo = \"foo\";" + PMD.EOL +
            "        boolean b = foo.trim().length()==0;" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            "    void bar() {" + PMD.EOL +
            "        String foo = \"foo\";" + PMD.EOL +
            "        boolean b = foo.trim().length()==1;" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public class Foo {" + PMD.EOL +
            "    void bar() {" + PMD.EOL +
            "        String foo = \"foo\";" + PMD.EOL +
            "        boolean b = foo(foo.trim().length(),0);" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";

    private static final String TEST9 =
            "public class Foo {" + PMD.EOL +
            "    void bar() {" + PMD.EOL +
            "        String foo = \"foo\";" + PMD.EOL +
            "        boolean b = foo(foo.trim().length()==(2-1));" + PMD.EOL +
            "    }" + PMD.EOL +
            "}";


}
