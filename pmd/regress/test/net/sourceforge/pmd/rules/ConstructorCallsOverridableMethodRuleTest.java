package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ConstructorCallsOverridableMethodRule;

public class ConstructorCallsOverridableMethodRuleTest extends RuleTst {

    public void testCallsPublic() throws Throwable {
        runTest("ConstructorCallsOverridableMethodRule1.java", 1, new ConstructorCallsOverridableMethodRule());
    }

    public void testCallsProtected() throws Throwable {
        runTest("ConstructorCallsOverridableMethodRule2.java", 1, new ConstructorCallsOverridableMethodRule());
    }

    public void testCallsPackage() throws Throwable {
        runTest("ConstructorCallsOverridableMethodRule3.java", 1, new ConstructorCallsOverridableMethodRule());
    }

    public void testCallsPrivateMethodOK() throws Throwable {
        runTest("ConstructorCallsOverridableMethodRule4.java", 0, new ConstructorCallsOverridableMethodRule());
    }

    public void testCallsPrivateConstructor() throws Throwable {
        runTest("ConstructorCallsOverridableMethodRule5.java", 1, new ConstructorCallsOverridableMethodRule());
    }

}
