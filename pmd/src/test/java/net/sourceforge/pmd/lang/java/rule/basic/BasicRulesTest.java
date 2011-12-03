/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.basic;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class BasicRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-basic";

    @Before
    public void setUp() {
        addRule(RULESET, "AvoidBranchingStatementAsLastInLoop");
        addRule(RULESET, "AvoidDecimalLiteralsInBigDecimalConstructor");
    	addRule(RULESET, "AvoidMultipleUnaryOperators");
        addRule(RULESET, "AvoidThreadGroup");
        addRule(RULESET, "AvoidUsingHardCodedIP");
//        addRule(RULESET, "AvoidUsingHardCodedURL");
        addRule(RULESET, "AvoidUsingOctalValues");
        addRule(RULESET, "BigIntegerInstantiation");
        addRule(RULESET, "BooleanInstantiation");
        addRule(RULESET, "BrokenNullCheck");
        addRule(RULESET, "CheckResultSet");
        addRule(RULESET, "CheckSkipResult");
        addRule(RULESET, "ClassCastExceptionWithToArray");
        addRule(RULESET, "CollapsibleIfStatements");
        addRule(RULESET, "DoubleCheckedLocking");
        addRule(RULESET, "ExtendsObject");
        addRule(RULESET, "ForLoopShouldBeWhileLoop");
        addRule(RULESET, "JumbledIncrementer");
        addRule(RULESET, "MisplacedNullCheck");
        addRule(RULESET, "OverrideBothEqualsAndHashcode");
        addRule(RULESET, "ReturnFromFinallyBlock");
        addRule(RULESET, "DontCallThreadRun");
        addRule(RULESET, "DontUseFloatTypeForLoopIndices");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(BasicRulesTest.class);
    }
}
