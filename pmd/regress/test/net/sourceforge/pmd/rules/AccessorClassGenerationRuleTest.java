package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AccessorClassGenerationRule;

public class AccessorClassGenerationRuleTest extends RuleTst {

    public void testInnerClassHasPrivateConstructor() throws Throwable {
        runTest("AccessorClassGeneration1.java", 1, new AccessorClassGenerationRule());
    }

    public void testInnerClassHasPublicConstructor() throws Throwable {
        runTest("AccessorClassGeneration2.java", 0, new AccessorClassGenerationRule());
    }

    public void testOuterClassHasPrivateConstructor() throws Throwable {
        runTest("AccessorClassGeneration3.java", 1, new AccessorClassGenerationRule());
    }
}
