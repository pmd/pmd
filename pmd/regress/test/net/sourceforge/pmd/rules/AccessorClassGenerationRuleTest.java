package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AccessorClassGenerationRule;

public class AccessorClassGenerationRuleTest extends RuleTst {

    public void testInnerClassHasPrivateConstructor() throws Throwable {
        runTestFromFile("AccessorClassGeneration1.java", 1, new AccessorClassGenerationRule());
    }

    public void testInnerClassHasPublicConstructor() throws Throwable {
        runTestFromFile("AccessorClassGeneration2.java", 0, new AccessorClassGenerationRule());
    }

    public void testOuterClassHasPrivateConstructor() throws Throwable {
        runTestFromFile("AccessorClassGeneration3.java", 1, new AccessorClassGenerationRule());
    }
}
