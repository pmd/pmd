/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.design.LongClassRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class LongClassRuleTest extends RuleTst {

    public LongClassRule getIUT() {
        LongClassRule IUT = new LongClassRule();
        IUT.addProperty("minimum", "10");
        return IUT;
    }

    public void testShortClass() throws Throwable {
        runTestFromString(TEST0, 0, getIUT());
    }

    public void testLongClass() throws Throwable {
        runTestFromString(TEST1, 1, getIUT());
    }

    public void testLongClassWithLongerTest() throws Throwable {
        LongClassRule IUT = getIUT();
        IUT.addProperty("minimum", "2000");
        runTestFromString(TEST1, 0, IUT);
    }

    private static final String TEST0 =
    "public class LongMethod1 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is short.\");" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST1 =
    "public class LongClass1" + PMD.EOL +
    "{" + PMD.EOL +
    "    public void method0() {" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";
}

