package net.sourceforge.pmd.lang.java.rule.optimizations;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class OptimizationsRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-optimizations";

    @Before
    public void setUp() {
        addRule(RULESET, "AddEmptyString");
        addRule(RULESET, "AvoidArrayLoops");
        addRule(RULESET, "AvoidInstantiatingObjectsInLoops");
        addRule(RULESET, "LocalVariableCouldBeFinal");
        addRule(RULESET, "MethodArgumentCouldBeFinal");
        addRule(RULESET, "RedundantFieldInitializer");
        addRule(RULESET, "SimplifyStartsWith");
        addRule(RULESET, "UnnecessaryWrapperObjectCreation");
        addRule(RULESET, "UseArrayListInsteadOfVector");
        addRule(RULESET, "UseArraysAsList");
        addRule(RULESET, "UseStringBufferForStringAppends");
        addRule(RULESET, "PrematureDeclaration");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(OptimizationsRulesTest.class);
    }
}
