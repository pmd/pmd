/**
 * Created on Sep 4, 2002
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.design.SwitchDensityRule;
import test.net.sourceforge.pmd.rules.RuleTst;

/**
 * @author dpeugh
 *
 * This tests the new SwitchDensity rule to see if it really
 * does work.
 */
public class SwitchDensityTest extends RuleTst {

    private static final String TEST1 =
    "// Switch Density = 5.0" + CPD.EOL +
    "public class SwitchDensity1 {" + CPD.EOL +
    "       public void foo(int i) {" + CPD.EOL +
    "               switch (i) {" + CPD.EOL +
    "                       case 0:" + CPD.EOL +
    "                       {" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                       }" + CPD.EOL +
    "               }                               " + CPD.EOL +
    "       }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "// Switch Density = 1.0" + CPD.EOL +
    "public class SwitchDensity2 {" + CPD.EOL +
    "       public void foo(int i) {" + CPD.EOL +
    "               switch (i) {" + CPD.EOL +
    "                       case 0:" + CPD.EOL +
    "                       {" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                       }" + CPD.EOL +
    "               }                               " + CPD.EOL +
    "       }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "// Switch Density = 1.0" + CPD.EOL +
    "public class SwitchDensity3 {" + CPD.EOL +
    "       public void foo(int i) {" + CPD.EOL +
    "               switch (i) {" + CPD.EOL +
    "                       case 0:" + CPD.EOL +
    "                       case 1:" + CPD.EOL +
    "                       case 2:" + CPD.EOL +
    "                       case 3:" + CPD.EOL +
    "                       case 4:" + CPD.EOL +
    "                       {" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                               System.err.println(\"I am a fish.\");" + CPD.EOL +
    "                       }" + CPD.EOL +
    "               }                               " + CPD.EOL +
    "       }" + CPD.EOL +
    "}";

    public SwitchDensityTest() {
        super();
    }

    public SwitchDensityRule getIUT() {
        SwitchDensityRule RC = new SwitchDensityRule();
        RC.addProperty("minimum", "4");
        return RC;
    }

    public void testSD1() throws Throwable {
        runTestFromString(TEST1, 1, getIUT());
    }

    public void testSD2() throws Throwable {
        runTestFromString(TEST2, 0, getIUT());
    }

    public void testSD3() throws Throwable {
        runTestFromString(TEST3, 0, getIUT());
    }
}
