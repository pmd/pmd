package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class OverrideBothEqualsAndHashcodeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ClassDeclaration//MethodDeclarator" +
        	"[" +
        	"(" +
        	"@Image = 'equals'" +
        	" and count(FormalParameters/*) = 1" +
        	" and not(//MethodDeclarator[count(FormalParameters/*) = 0][@Image = 'hashCode'])" +
        	") or (" +
        	"@Image='hashCode'" +
        	" and count(FormalParameters/*) = 0" +
        	" and not(//MethodDeclarator[count(FormalParameters//Type/Name[@Image = 'Object']) = 1][@Image = 'equals'])" +
        	")]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "hash code only", 1, rule),
           new TestDescriptor(TEST2, "equals only", 1, rule),
           new TestDescriptor(TEST3, "overrides both", 0, rule),
           new TestDescriptor(TEST4, "overrides neither", 0, rule),
           new TestDescriptor(TEST5, "equals sig uses String, not Object", 1, rule),
           new TestDescriptor(TEST6, "interface", 0, rule),
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
}
