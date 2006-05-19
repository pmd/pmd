/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class PreserveStackTraceTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "PreserveStackTrace");
    }
    
    public void test() throws Throwable {
        rule.setMessage("{0}");
        runTests(new TestDescriptor[]{
                new TestDescriptor(TEST1_FAIL, "1, Exception thrown without preserving stack", 1, rule),
                new TestDescriptor(TEST2_OK, "2, Exception thrown, stack preserved", 0, rule),
                new TestDescriptor(TEST3_OK, "3, Exception thrown, stack preserved", 0, rule),
                new TestDescriptor(TEST4_OK, "4, No exception thrown, OK", 0, rule),
                new TestDescriptor(TEST5_OK, "5, No exception thrown, OK", 0, rule),
                new TestDescriptor(TEST6_OK, "6, No exception thrown, OK", 0, rule),
                new TestDescriptor(TEST7_OK, "7, No exception thrown, OK", 0, rule),
                new TestDescriptor(TEST8_OK, "8, No exception thrown, OK", 0, rule),
                new TestDescriptor(TEST9_OK, "9, Excetion is cast, OK", 0, rule),
            });
    }

    private static final String TEST1_FAIL =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "            throw new Exception(e.getMessage());" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST2_OK =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "            throw new Exception(e);" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST3_OK =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "            throw new Exception(e, e.getMessage());" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST4_OK =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "            throw e.fillInStackTrace();" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST5_OK =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST6_OK =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "            e.printStackTrace();" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST7_OK =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "            throw new Exception(Bar.foo(e),e);" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST8_FAIL =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "            throw new Exception(Bar.foo(e));" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST8_OK =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "            throw (Error)e;" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST9_OK =
        "public class Foo {" + PMD.EOL +
        "    public void foo(String a) {" + PMD.EOL +
        "        try {" + PMD.EOL +
        "            int i = Integer.parseInt(a);" + PMD.EOL +
        "        } catch(Exception e){" + PMD.EOL +
        "            throw (Error)e.fillInStackTrace();" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
}
