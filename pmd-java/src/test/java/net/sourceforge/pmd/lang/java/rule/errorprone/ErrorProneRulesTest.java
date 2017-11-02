/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the error prone category
 */
public class ErrorProneRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/errorprone.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AssignmentInOperand");
        addRule(RULESET, "AssignmentToNonFinalStatic");
        //addRule(RULESET, "AvoidAccessibilityAlteration");
        addRule(RULESET, "AvoidBranchingStatementAsLastInLoop");
        addRule(RULESET, "AvoidCallingFinalize");
        addRule(RULESET, "AvoidDecimalLiteralsInBigDecimalConstructor");
        addRule(RULESET, "AvoidInstanceofChecksInCatchClause");
        addRule(RULESET, "AvoidLiteralsInIfCondition");
        addRule(RULESET, "AvoidMultipleUnaryOperators");
        addRule(RULESET, "AvoidUsingOctalValues");
        addRule(RULESET, "BadComparison");
        addRule(RULESET, "BrokenNullCheck");
        addRule(RULESET, "CallSuperFirst");
        addRule(RULESET, "CallSuperLast");
        addRule(RULESET, "CheckSkipResult");
        addRule(RULESET, "ClassCastExceptionWithToArray");
        addRule(RULESET, "CloneMethodMustBePublic");
        addRule(RULESET, "CloneMethodMustImplementCloneable");
        addRule(RULESET, "CloneMethodReturnTypeMustMatchClassName");
        addRule(RULESET, "CloneThrowsCloneNotSupportedException");
        addRule(RULESET, "CloseResource");
        addRule(RULESET, "CompareObjectsWithEquals");
        addRule(RULESET, "ConstructorCallsOverridableMethod");
        addRule(RULESET, "DataflowAnomalyAnalysis");
        addRule(RULESET, "DoNotCallGarbageCollectionExplicitly");
        addRule(RULESET, "DoNotHardCodeSDCard");
        addRule(RULESET, "DontImportSun");
        addRule(RULESET, "DontUseFloatTypeForLoopIndices");
        addRule(RULESET, "EmptyCatchBlock");
        addRule(RULESET, "EmptyFinalizer");
        addRule(RULESET, "EmptyFinallyBlock");
        addRule(RULESET, "EmptyIfStmt");
        addRule(RULESET, "EmptyInitializer");
        addRule(RULESET, "EmptyStatementBlock");
        addRule(RULESET, "EmptyStatementNotInLoop");
        addRule(RULESET, "EmptyStaticInitializer");
        addRule(RULESET, "EmptySwitchStatements");
        addRule(RULESET, "EmptySynchronizedBlock");
        addRule(RULESET, "EmptyTryBlock");
        addRule(RULESET, "EmptyWhileStmt");
        addRule(RULESET, "EqualsNull");
        addRule(RULESET, "FinalizeDoesNotCallSuperFinalize");
        addRule(RULESET, "FinalizeOnlyCallsSuperFinalize");
        addRule(RULESET, "FinalizeOverloaded");
        addRule(RULESET, "FinalizeShouldBeProtected");
        addRule(RULESET, "IdempotentOperations");
        addRule(RULESET, "ImportFromSamePackage");
        addRule(RULESET, "InstantiationToGetClass");
        addRule(RULESET, "JumbledIncrementer");
        addRule(RULESET, "MisplacedNullCheck");
        addRule(RULESET, "MissingBreakInSwitch");
        addRule(RULESET, "MissingStaticMethodInNonInstantiatableClass");
        addRule(RULESET, "NonCaseLabelInSwitchStatement");
        addRule(RULESET, "NonStaticInitializer");
        addRule(RULESET, "NullAssignment");
        addRule(RULESET, "OverrideBothEqualsAndHashcode");
        addRule(RULESET, "ProperCloneImplementation");
        addRule(RULESET, "ReturnEmptyArrayRatherThanNull");
        addRule(RULESET, "ReturnFromFinallyBlock");
        addRule(RULESET, "SimpleDateFormatNeedsLocale");
        addRule(RULESET, "SingleMethodSingleton");
        addRule(RULESET, "SingletonClassReturningNewInstance");
        addRule(RULESET, "SuspiciousOctalEscape");
        addRule(RULESET, "UnconditionalIfStatement");
        addRule(RULESET, "UseLocaleWithCaseConversions");
    }

}
