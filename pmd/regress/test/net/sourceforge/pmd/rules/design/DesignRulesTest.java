package test.net.sourceforge.pmd.rules.design;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class DesignRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("design", "AbstractClassWithoutAbstractMethod");
        addRule("design", "AbstractClassWithoutAnyMethod");
        addRule("design", "AccessorClassGeneration");
        addRule("design", "AssignmentToNonFinalStatic");
        addRule("design", "AvoidConstantsInterface");
        addRule("design", "AvoidDeeplyNestedIfStmts");
        addRule("design", "AvoidInstanceofChecksInCatchClause");
        addRule("design", "AvoidProtectedFieldInFinalClass");
        addRule("design", "AvoidReassigningParameters");
        addRule("design", "AvoidSynchronizedAtMethodLevel");
        addRule("design", "BadComparison");
        addRule("design", "ClassWithOnlyPrivateConstructorsShouldBeFinal");
        addRule("design", "CloseResource");
        addRule("design", "CompareObjectsWithEquals");
        addRule("design", "DefaultLabelNotLastInSwitchStmt");
        addRule("design", "EmptyMethodInAbstractClassShouldBeAbstract");
        addRule("design", "EqualsNull");
        addRule("design", "FinalFieldCouldBeStatic");
        addRule("design", "IdempotentOperations");
        addRule("design", "ImmutableField");
        addRule("design", "InstantiationToGetClass");
        addRule("design", "MissingBreakInSwitch");
        addRule("design", "MissingStaticMethodInNonInstantiatableClass");
        addRule("design", "NonCaseLabelInSwitchStatement");
        addRule("design", "NonStaticInitializer");
        addRule("design", "NonThreadSafeSingleton");
        addRule("design", "OptimizableToArrayCall");
        addRule("design", "PositionLiteralsFirstInComparisons");
        addRule("design", "PreserveStackTrace");
        addRule("design", "ReturnEmptyArrayRatherThanNull");
        addRule("design", "SimpleDateFormatNeedsLocale");
        addRule("design", "SimplifyBooleanExpressions");
        addRule("design", "SimplifyBooleanReturns");
        addRule("design", "SimplifyConditional");
        addRule("design", "SingularField");
        addRule("design", "SwitchDensity");
        addRule("design", "SwitchStmtsShouldHaveDefault");
        addRule("design", "UncommentedEmptyMethod");
        addRule("design", "UnnecessaryLocalBeforeReturn");
        addRule("design", "UnsynchronizedStaticDateFormatter");
        addRule("design", "UseCollectionIsEmpty");
        addRule("design", "UseLocaleWithCaseConversions");
        addRule("design", "UseNotifyAllInsteadOfNotify");
        addRule("design", "UseSingleton");
        addRule("design", "TooFewBranchesForASwitchStatement");
//        addRule("design", "TooManyHttpFilter.xml");

    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DesignRulesTest.class);
    }
}
