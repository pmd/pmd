package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.rules.XPathRule;

public class OverrideBothEqualsAndHashcodeRuleTest extends RuleTst {
    private static final String TEST1 =
    "public class OverrideBothEqualsAndHashcode1 {" + CPD.EOL +
    " public int hashCode() {}" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class OverrideBothEqualsAndHashcode2 {" + CPD.EOL +
    " public boolean equals(Object other) {}" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class OverrideBothEqualsAndHashcode3 {" + CPD.EOL +
    " public boolean equals(Object other) {}" + CPD.EOL +
    " public int hashCode() {}" + CPD.EOL +
    "}";

    private static final String TEST4 =
    "public class OverrideBothEqualsAndHashcode4 {" + CPD.EOL +
    "}";

    private static final String TEST5 =
    "public class OverrideBothEqualsAndHashcode5 {" + CPD.EOL +
    " public boolean equals(String o) {" + CPD.EOL +
    "  return true;" + CPD.EOL +
    " }" + CPD.EOL +
    " public int hashCode() {" + CPD.EOL +
    "  return 0;" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST6 =
    "public interface OverrideBothEqualsAndHashcode6 {" + CPD.EOL +
    " public boolean equals(Object o);" + CPD.EOL +
    "}";

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

    public void testHashCodeOnly() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }
    public void testEqualsOnly() throws Throwable {
        runTestFromString(TEST2, 1, rule);
    }
    public void testCorrectImpl() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
    public void testNeither() throws Throwable {
        runTestFromString(TEST4, 0, rule);
    }
    public void testEqualsSignatureUsesStringNotObject() throws Throwable {
        runTestFromString(TEST5, 1, rule);
    }
    public void testInterface() throws Throwable {
        runTestFromString(TEST6, 0, rule);
    }
}
