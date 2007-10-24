package test.net.sourceforge.pmd.rules.optimizations;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class OptimizationsRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("optimizations", "AddEmptyString");
        addRule("optimizations", "AvoidArrayLoops");
        addRule("optimizations", "AvoidInstantiatingObjectsInLoops");
        addRule("optimizations", "LocalVariableCouldBeFinal");
        addRule("optimizations", "MethodArgumentCouldBeFinal");
        addRule("optimizations", "SimplifyStartsWith");
        addRule("optimizations", "UnnecessaryWrapperObjectCreation");
        addRule("optimizations", "UseArrayListInsteadOfVector");
        addRule("optimizations", "UseArraysAsList");
        addRule("optimizations", "UseStringBufferForStringAppends");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(OptimizationsRulesTest.class);
    }
}
