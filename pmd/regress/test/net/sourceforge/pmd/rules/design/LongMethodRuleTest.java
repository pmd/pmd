/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.design.LongMethodRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class LongMethodRuleTest extends RuleTst {

    private LongMethodRule getIUT() {
        LongMethodRule IUT = new LongMethodRule();
        IUT.addProperty("minimum", "10");
        return IUT;
    }

    public void testShortMethod() throws Throwable {
        runTestFromString(TEST1, 0, getIUT());
    }
    public void testReallyLongMethod() throws Throwable {
        runTestFromString(TEST2, 1, getIUT());
    }
    public void testReallyLongMethodWithLongerRange() throws Throwable {
        LongMethodRule IUT = getIUT();
        IUT.addProperty("minimum", "20");
        runTestFromString(TEST2, 0, IUT);
    }
    public void testNotQuiteLongMethod() throws Throwable {
        runTestFromString(TEST3, 0, getIUT());
    }
    public void testLongMethod() throws Throwable {
        runTestFromString(TEST4, 1, getIUT());
    }

    private static final String TEST1 =
    "public class LongMethod1 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is short.\");" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class LongMethod2 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "    } // 11 lines - violation" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class LongMethod2 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "    } // 9 lines - Not a violation" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class LongMethod2 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "    } // > 10 lines - Not a violation" + PMD.EOL +
    "}";
}

