/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.optimizations;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class OptimizationsRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-optimizations";

    @Override
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
}
