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
        addRule("basic", "CheckSkipResult");
        addRule("basic", "ClassCastExceptionWithToArray");
        addRule("basic", "CollapsibleIfStatements");
        addRule("basic", "DoubleCheckedLocking");
	addRule("basic", "ExtendsObject");
        addRule("basic", "ForLoopShouldBeWhileLoop");
        addRule("basic", "JumbledIncrementer");
        addRule("basic", "MisplacedNullCheck");
        addRule("basic", "OverrideBothEqualsAndHashcode");
        addRule("basic", "ReturnFromFinallyBlock");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(BasicRulesTest.class);
    }
}
