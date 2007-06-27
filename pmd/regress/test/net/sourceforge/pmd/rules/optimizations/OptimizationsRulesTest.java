package test.net.sourceforge.pmd.rules.optimizations;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class OptimizationsRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("optimizations", "AddEmptyString"));
        rules.add(findRule("optimizations", "AvoidArrayLoops"));
        rules.add(findRule("optimizations", "AvoidInstantiatingObjectsInLoops"));
        rules.add(findRule("optimizations", "LocalVariableCouldBeFinal"));
        rules.add(findRule("optimizations", "MethodArgumentCouldBeFinal"));
        rules.add(findRule("optimizations", "SimplifyStartsWith"));
        rules.add(findRule("optimizations", "UnnecessaryWrapperObjectCreation"));
        rules.add(findRule("optimizations", "UseArrayListInsteadOfVector"));
        rules.add(findRule("optimizations", "UseArraysAsList"));
        rules.add(findRule("optimizations", "UseStringBufferForStringAppends"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(OptimizationsRulesTest.class);
    }
}
