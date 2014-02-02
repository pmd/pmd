/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class DesignRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-design";

    @Before
    public void setUp() {
        addRule(RULESET, "AbstractClassWithoutAbstractMethod");
        addRule(RULESET, "AbstractClassWithoutAnyMethod");
        addRule(RULESET, "AccessorClassGeneration");
        addRule(RULESET, "AssignmentToNonFinalStatic");
        addRule(RULESET, "AvoidConstantsInterface");
        addRule(RULESET, "AvoidDeeplyNestedIfStmts");
        addRule(RULESET, "AvoidInstanceofChecksInCatchClause");
        addRule(RULESET, "AvoidProtectedFieldInFinalClass");
        addRule(RULESET, "AvoidProtectedMethodInFinalClassNotExtending");
        addRule(RULESET, "AvoidReassigningParameters");
        addRule(RULESET, "AvoidSynchronizedAtMethodLevel");
        addRule(RULESET, "BadComparison");
        addRule(RULESET, "ClassWithOnlyPrivateConstructorsShouldBeFinal");
        addRule(RULESET, "CloseResource");
        addRule(RULESET, "CompareObjectsWithEquals");
        addRule(RULESET, "ConfusingTernary");
        addRule(RULESET, "ConstructorCallsOverridableMethod");
        addRule(RULESET, "DefaultLabelNotLastInSwitchStmt");
        addRule(RULESET, "EmptyMethodInAbstractClassShouldBeAbstract");
        addRule(RULESET, "EqualsNull");
        addRule(RULESET, "FieldDeclarationsShouldBeAtStartOfClass");
        addRule(RULESET, "FinalFieldCouldBeStatic");
        addRule(RULESET, "GodClass");
        addRule(RULESET, "IdempotentOperations");
        addRule(RULESET, "ImmutableField");
        addRule(RULESET, "InstantiationToGetClass");
        addRule(RULESET, "LogicInversion");
        addRule(RULESET, "MissingBreakInSwitch");
        addRule(RULESET, "MissingStaticMethodInNonInstantiatableClass");
        addRule(RULESET, "NonCaseLabelInSwitchStatement");
        addRule(RULESET, "NonStaticInitializer");
        addRule(RULESET, "NonThreadSafeSingleton");
        addRule(RULESET, "OptimizableToArrayCall");
        //addRule(RULESET, "PositionalIteratorRule"); This rule does not yet exist
        addRule(RULESET, "PositionLiteralsFirstInComparisons");
        addRule(RULESET, "PositionLiteralsFirstInCaseInsensitiveComparisons");
        addRule(RULESET, "PreserveStackTrace");
        addRule(RULESET, "ReturnEmptyArrayRatherThanNull");
        addRule(RULESET, "SimpleDateFormatNeedsLocale");
        addRule(RULESET, "SimplifyBooleanExpressions");
        addRule(RULESET, "SimplifyBooleanReturns");
        addRule(RULESET, "SimplifyConditional");
        addRule(RULESET, "SingularField");
        addRule(RULESET, "SwitchDensity");
        addRule(RULESET, "SwitchStmtsShouldHaveDefault");
        addRule(RULESET, "TooFewBranchesForASwitchStatement");
        //addRule(RULESET, "TooManyHttpFilter"); This rule does not yet exist
        addRule(RULESET, "UncommentedEmptyConstructor");
        addRule(RULESET, "UncommentedEmptyMethod");
        addRule(RULESET, "UnnecessaryLocalBeforeReturn");
        addRule(RULESET, "UnsynchronizedStaticDateFormatter");
        addRule(RULESET, "UseCollectionIsEmpty");
        addRule(RULESET, "UseLocaleWithCaseConversions");
        addRule(RULESET, "UseNotifyAllInsteadOfNotify");
        addRule(RULESET, "UseUtilityClass");
        addRule(RULESET, "UseVarargs");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DesignRulesTest.class);
    }
}
