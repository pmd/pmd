/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the performance category
 */
public class PerformanceRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/performance.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AddEmptyString");
        addRule(RULESET, "AppendCharacterWithChar");
        addRule(RULESET, "AvoidArrayLoops");
        addRule(RULESET, "AvoidFileInputStream");
        addRule(RULESET, "AvoidFileOutputStream");
        addRule(RULESET, "AvoidFileReader");
        addRule(RULESET, "AvoidFileWriter");
        addRule(RULESET, "AvoidInstantiatingObjectsInLoops");
        addRule(RULESET, "AvoidUsingShortType");
        addRule(RULESET, "BigIntegerInstantiation");
        addRule(RULESET, "BooleanInstantiation");
        addRule(RULESET, "ByteInstantiation");
        addRule(RULESET, "ConsecutiveAppendsShouldReuse");
        addRule(RULESET, "ConsecutiveLiteralAppends");
        addRule(RULESET, "InefficientEmptyStringCheck");
        addRule(RULESET, "InefficientStringBuffering");
        addRule(RULESET, "InsufficientStringBufferDeclaration");
        addRule(RULESET, "IntegerInstantiation");
        addRule(RULESET, "LongInstantiation");
        addRule(RULESET, "OptimizableToArrayCall");
        addRule(RULESET, "RedundantFieldInitializer");
        addRule(RULESET, "ShortInstantiation");
        addRule(RULESET, "SimplifyStartsWith");
        addRule(RULESET, "StringInstantiation");
        addRule(RULESET, "StringToString");
        addRule(RULESET, "TooFewBranchesForASwitchStatement");
        addRule(RULESET, "UnnecessaryWrapperObjectCreation");
        addRule(RULESET, "UseArrayListInsteadOfVector");
        addRule(RULESET, "UseArraysAsList");
        addRule(RULESET, "UseIndexOfChar");
        addRule(RULESET, "UselessStringValueOf");
        addRule(RULESET, "UseStringBufferForStringAppends");
        addRule(RULESET, "UseStringBufferLength");
    }

}
