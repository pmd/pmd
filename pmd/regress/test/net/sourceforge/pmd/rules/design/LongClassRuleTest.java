package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.design.LongClassRule;
import test.net.sourceforge.pmd.rules.RuleTst;

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
    "public class LongMethod1 {" + CPD.EOL +
    "    public static void main(String args[]) {" + CPD.EOL +
    "	System.err.println(\"This is short.\");" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    private static final String TEST1 =
    "public class LongClass1" + CPD.EOL +
    "{" + CPD.EOL +
    "    public void method0() {" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "	System.err.println(\"This is a long class.\");" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";
}

