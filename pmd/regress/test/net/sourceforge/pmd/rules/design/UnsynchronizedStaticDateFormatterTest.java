/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;


public class UnsynchronizedStaticDateFormatterTest extends SimpleAggregatorTst  {

    private Rule rule;

    public void setUp() {
        rule = findRule("design", "UnsynchronizedStaticDateFormatter");
    }

    public void testAll() throws Exception{
        runTests(new TestDescriptor[] { 
                new TestDescriptor(TEST1, "Format called from non-synchronized block", 1, rule),
                new TestDescriptor(TEST2, "2, No call to format", 0, rule),
                new TestDescriptor(TEST3, "3, Inside synchronized, OK", 0, rule),
                new TestDescriptor(TEST4, "4, Inside synchronized, OK", 0, rule),
                new TestDescriptor(TEST5, "5, Use DateFormat, ok", 0, rule),
                new TestDescriptor(TEST6, "6, Use DateFormat, fail", 1, rule),
        });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        "    private static final SimpleDateFormat sdf = new SimpleDateFormat();" + PMD.EOL +
        "    void bar() {" + PMD.EOL +
        "        sdf.format();" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        "    private final SimpleDateFormat sdf = new SimpleDateFormat();" + PMD.EOL +
        "    void bar() {" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST3 =
        "public class Foo {" + PMD.EOL +
        "    private static final SimpleDateFormat sdf = new SimpleDateFormat();" + PMD.EOL +
        "    synchronized void bar() {" + PMD.EOL +
        "        sdf.format();" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";


    private static final String TEST4 =
        "public class Foo {" + PMD.EOL +
        "    private static final SimpleDateFormat sdf = new SimpleDateFormat();" + PMD.EOL +
        "    public void bar() {" + PMD.EOL +
        "        synchronized (sdf) {" + PMD.EOL +
        "            sdf.format();" + PMD.EOL +
        "        }" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
    

    private static final String TEST5 =
        "public class Foo {" + PMD.EOL +
        "    private static final DateFormat sdf = new DateFormat();" + PMD.EOL +
        "    synchronized void bar() {" + PMD.EOL +
        "        sdf.format();" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

    private static final String TEST6 =
        "public class Foo {" + PMD.EOL +
        "    private static final DateFormat sdf = new DateFormat();" + PMD.EOL +
        "    void bar() {" + PMD.EOL +
        "        sdf.format();" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";

}