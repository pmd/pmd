/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the best practices category
 */
public class BestPracticesRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/bestpractices.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AbstractClassWithoutAbstractMethod");
        addRule(RULESET, "AccessorClassGeneration");
        addRule(RULESET, "AccessorMethodGeneration");
        addRule(RULESET, "AvoidReassigningParameters");
        addRule(RULESET, "AvoidUsingHardCodedIP");
        // addRule(RULESET, "AvoidUsingHardCodedURL");
        addRule(RULESET, "CheckResultSet");
        addRule(RULESET, "ConstantsInInterface");
        addRule(RULESET, "DefaultLabelNotLastInSwitchStmt");
        addRule(RULESET, "LooseCoupling");
        addRule(RULESET, "OneDeclarationPerLine");
        addRule(RULESET, "PositionLiteralsFirstInComparisons");
        addRule(RULESET, "PositionLiteralsFirstInCaseInsensitiveComparisons");
        addRule(RULESET, "PreserveStackTrace");
        addRule(RULESET, "SwitchStmtsShouldHaveDefault");
        addRule(RULESET, "UnusedImports");
        addRule(RULESET, "UseCollectionIsEmpty");
        addRule(RULESET, "UseVarargs");
    }

}
