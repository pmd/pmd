/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.design.SwitchDensityRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class SwitchDensityTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new SwitchDensityRule();
        rule.addProperty("minimum", "4");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "Five stmts in one switch case, should be flagged", 1, rule),
            new TestDescriptor(TEST2, "One stmt in one switch case, ok", 0, rule),
            new TestDescriptor(TEST3, "Five stmts, 5 cases, OK", 0, rule),
        });
    }

    private static final String TEST1 =
            "// Switch Density = 5.0" + PMD.EOL +
            "public class SwitchDensity1 {" + PMD.EOL +
            " public void foo(int i) {" + PMD.EOL +
            "  switch (i) {" + PMD.EOL +
            "  case 0:" + PMD.EOL +
            "  {" + PMD.EOL + pad(5) +
            "  }" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "// Switch Density = 1.0" + PMD.EOL +
            "public class SwitchDensity2 {" + PMD.EOL +
            "       public void foo(int i) {" + PMD.EOL +
            "               switch (i) {" + PMD.EOL +
            "                       case 0:" + PMD.EOL +
            "                       {" + PMD.EOL + pad(1) +
            "                       }" + PMD.EOL +
            "               }" + PMD.EOL +
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
            "                       {" + PMD.EOL + pad(5) +
            "                       }" + PMD.EOL +
            "               } " + PMD.EOL +
            "       }" + PMD.EOL +
            "}";

    private static String pad(int times) {
        String x = "";
        for (int i = 0; i < times; i++) {
            x += "System.err.println(\"I am a fish.\");" + PMD.EOL;
        }
        return x;
    }

}
