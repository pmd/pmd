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
        addRule(RULESET, "ArrayIsStoredDirectly");
        addRule(RULESET, "AvoidPrintStackTrace");
        addRule(RULESET, "AvoidReassigningParameters");
        addRule(RULESET, "AvoidStringBufferField");
        addRule(RULESET, "AvoidUsingHardCodedIP");
        // addRule(RULESET, "AvoidUsingHardCodedURL");
        addRule(RULESET, "CheckResultSet");
        addRule(RULESET, "ConstantsInInterface");
        addRule(RULESET, "DefaultLabelNotLastInSwitchStmt");
        addRule(RULESET, "ForLoopCanBeForeach");
        addRule(RULESET, "GuardLogStatement");
        addRule(RULESET, "JUnit4SuitesShouldUseSuiteAnnotation");
        addRule(RULESET, "JUnit4TestShouldUseAfterAnnotation");
        addRule(RULESET, "JUnit4TestShouldUseBeforeAnnotation");
        addRule(RULESET, "JUnit4TestShouldUseTestAnnotation");
        addRule(RULESET, "JUnitAssertionsShouldIncludeMessage");
        addRule(RULESET, "JUnitTestContainsTooManyAsserts");
        addRule(RULESET, "JUnitTestsShouldIncludeAssert");
        addRule(RULESET, "JUnitUseExpected");
        addRule(RULESET, "LooseCoupling");
        addRule(RULESET, "MethodReturnsInternalArray");
        addRule(RULESET, "MissingOverride");
        addRule(RULESET, "OneDeclarationPerLine");
        addRule(RULESET, "PositionLiteralsFirstInComparisons");
        addRule(RULESET, "PositionLiteralsFirstInCaseInsensitiveComparisons");
        addRule(RULESET, "PreserveStackTrace");
        addRule(RULESET, "ReplaceEnumerationWithIterator");
        addRule(RULESET, "ReplaceHashtableWithMap");
        addRule(RULESET, "ReplaceVectorWithList");
        addRule(RULESET, "SwitchStmtsShouldHaveDefault");
        addRule(RULESET, "SystemPrintln");
        addRule(RULESET, "UnusedFormalParameter");
        addRule(RULESET, "UnusedImports");
        addRule(RULESET, "UnusedLocalVariable");
        addRule(RULESET, "UnusedPrivateField");
        addRule(RULESET, "UnusedPrivateMethod");
        addRule(RULESET, "UseAssertEqualsInsteadOfAssertTrue");
        addRule(RULESET, "UseAssertNullInsteadOfAssertTrue");
        addRule(RULESET, "UseAssertSameInsteadOfAssertTrue");
        addRule(RULESET, "UseAssertTrueInsteadOfAssertEquals");
        addRule(RULESET, "UseCollectionIsEmpty");
        addRule(RULESET, "UseVarargs");
    }

}
