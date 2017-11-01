/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the code style category
 */
public class CodeStyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/codestyle.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AtLeastOneConstructor");
        addRule(RULESET, "AvoidFinalLocalVariable");
        addRule(RULESET, "AvoidPrefixingMethodParameters");
        addRule(RULESET, "AvoidUsingNativeCode");
        addRule(RULESET, "CallSuperInConstructor");
        addRule(RULESET, "CommentDefaultAccessModifier");
        addRule(RULESET, "DefaultPackage");
        addRule(RULESET, "ExtendsObject");
        addRule(RULESET, "ForLoopsMustUseBraces");
        addRule(RULESET, "ForLoopShouldBeWhileLoop");
        addRule(RULESET, "IfElseStmtsMustUseBraces");
        addRule(RULESET, "IfStmtsMustUseBraces");
        addRule(RULESET, "OnlyOneReturn");
        addRule(RULESET, "UnnecessaryConstructor");
        addRule(RULESET, "WhileLoopsMustUseBraces");
    }

}
