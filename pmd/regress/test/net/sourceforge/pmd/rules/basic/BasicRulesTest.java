/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.basic;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BasicRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("basic", "AvoidDecimalLiteralsInBigDecimalConstructor");
    	addRule("basic", "AvoidMultipleUnaryOperators");
        addRule("basic", "AvoidThreadGroup");
        addRule("basic", "AvoidUsingHardCodedIP");
//        addRule("basic", "AvoidUsingHardCodedURL");
        addRule("basic", "AvoidUsingOctalValues");
        addRule("basic", "BigIntegerInstantiation");
        addRule("basic", "BooleanInstantiation");
        addRule("basic", "BrokenNullCheck");
        addRule("basic", "CheckResultSet");
        addRule("basic", "ClassCastExceptionWithToArray");
        addRule("basic", "CollapsibleIfStatements");
        addRule("basic", "DoubleCheckedLocking");
        addRule("basic", "EmptyCatchBlock");
        addRule("basic", "EmptyFinallyBlock");
        addRule("basic", "EmptyIfStmt");
        addRule("basic", "EmptyInitializer");
        addRule("basic", "EmptyStatementNotInLoop");
        addRule("basic", "EmptyStaticInitializer");
        addRule("basic", "EmptySwitchStatements");
        addRule("basic", "EmptySynchronizedBlock");
        addRule("basic", "EmptyTryBlock");
        addRule("basic", "EmptyWhileStmt");
        addRule("basic", "ForLoopShouldBeWhileLoop");
        addRule("basic", "JumbledIncrementer");
        addRule("basic", "MisplacedNullCheck");
        addRule("basic", "OverrideBothEqualsAndHashcode");
        addRule("basic", "ReturnFromFinallyBlock");
        addRule("basic", "UnconditionalIfStatement");
        addRule("basic", "UnnecessaryFinalModifier");
        addRule("basic", "UnnecessaryReturn");
        addRule("basic", "UnnecessaryConversionTemporary");
        addRule("basic", "UselessOperationOnImmutable");
        addRule("basic", "UselessOverridingMethod");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(BasicRulesTest.class);
    }
}
