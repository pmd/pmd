/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.design.SwitchDensityRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

/**
 * @author dpeugh
 *
 * This tests the new SwitchDensity rule to see if it really
 * does work.
 */
public class SwitchDensityTest extends RuleTst {

    private static final String TEST1 =
    "// Switch Density = 5.0" + PMD.EOL +
    "public class SwitchDensity1 {" + PMD.EOL +
    "       public void foo(int i) {" + PMD.EOL +
    "               switch (i) {" + PMD.EOL +
    "                       case 0:" + PMD.EOL +
    "                       {" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                       }" + PMD.EOL +
    "               }                               " + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "// Switch Density = 1.0" + PMD.EOL +
    "public class SwitchDensity2 {" + PMD.EOL +
    "       public void foo(int i) {" + PMD.EOL +
    "               switch (i) {" + PMD.EOL +
    "                       case 0:" + PMD.EOL +
    "                       {" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                       }" + PMD.EOL +
    "               }                               " + PMD.EOL +
    "       }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "// Switch Density = 1.0" + PMD.EOL +
    "public class SwitchDensity3 {" + PMD.EOL +
    "       public void foo(int i) {" + PMD.EOL +
    "               switch (i) {" + PMD.EOL +
    "                       case 0:" + PMD.EOL +
    "                       case 1:" + PMD.EOL +
    "                       case 2:" + PMD.EOL +
    "                       case 3:" + PMD.EOL +
    "                       case 4:" + PMD.EOL +
    "                       {" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                               System.err.println(\"I am a fish.\");" + PMD.EOL +
    "                       }" + PMD.EOL +
    "               }                               " + PMD.EOL +
    "       }" + PMD.EOL +
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
