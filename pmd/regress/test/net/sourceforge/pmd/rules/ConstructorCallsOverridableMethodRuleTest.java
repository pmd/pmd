package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ConstructorCallsOverridableMethodRule;

public class ConstructorCallsOverridableMethodRuleTest extends RuleTst {

    public void testCallsPublic() throws Throwable {
        runTestFromFile("ConstructorCallsOverridableMethodRule1.java", 1, new ConstructorCallsOverridableMethodRule());
    }

    public void testCallsProtected() throws Throwable {
        runTestFromFile("ConstructorCallsOverridableMethodRule2.java", 1, new ConstructorCallsOverridableMethodRule());
    }

    public void testCallsPackage() throws Throwable {
        runTestFromFile("ConstructorCallsOverridableMethodRule3.java", 1, new ConstructorCallsOverridableMethodRule());
    }

    public void testCallsPrivateMethodOK() throws Throwable {
        runTestFromFile("ConstructorCallsOverridableMethodRule4.java", 0, new ConstructorCallsOverridableMethodRule());
    }

    public void testCallsPrivateConstructor() throws Throwable {
        runTestFromFile("ConstructorCallsOverridableMethodRule5.java", 1, new ConstructorCallsOverridableMethodRule());
    }

}
