/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class OverrideBothEqualsAndHashcodeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("rulesets/basic.xml", "OverrideBothEqualsAndHashcodeRule");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "hash code only", 1, rule),
           new TestDescriptor(TEST2, "equals only", 1, rule),
           new TestDescriptor(TEST3, "overrides both", 0, rule),
           new TestDescriptor(TEST4, "overrides neither", 0, rule),
           new TestDescriptor(TEST5, "equals sig uses String, not Object", 1, rule),
           new TestDescriptor(TEST6, "interface", 0, rule),
           new TestDescriptor(TEST7, "java.lang.Object", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class OverrideBothEqualsAndHashcode1 {" + PMD.EOL +
    " public int hashCode() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class OverrideBothEqualsAndHashcode2 {" + PMD.EOL +
    " public boolean equals(Object other) {}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class OverrideBothEqualsAndHashcode3 {" + PMD.EOL +
    " public boolean equals(Object other) {}" + PMD.EOL +
    " public int hashCode() {}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class OverrideBothEqualsAndHashcode4 {" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class OverrideBothEqualsAndHashcode5 {" + PMD.EOL +
    " public boolean equals(String o) {" + PMD.EOL +
    "  return true;" + PMD.EOL +
    " }" + PMD.EOL +
    " public int hashCode() {" + PMD.EOL +
    "  return 0;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public interface OverrideBothEqualsAndHashcode6 {" + PMD.EOL +
    " public boolean equals(Object o);" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public class Foo {" + PMD.EOL +
    " public boolean equals(java.lang.Object o) {" + PMD.EOL +
    "  return true;" + PMD.EOL +
    " }" + PMD.EOL +
    " public int hashCode() {" + PMD.EOL +
    "  return 0;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
