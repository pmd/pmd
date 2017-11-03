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
        addRule(RULESET, "AvoidAssertAsIdentifier");
        addRule(RULESET, "AvoidBranchingStatementAsLastInLoop");
        addRule(RULESET, "AvoidCallingFinalize");
        addRule(RULESET, "AvoidCatchingNPE");
        addRule(RULESET, "AvoidCatchingThrowable");
        addRule(RULESET, "AvoidDecimalLiteralsInBigDecimalConstructor");
        addRule(RULESET, "AvoidDuplicateLiterals");
        addRule(RULESET, "AvoidEnumAsIdentifier");
        addRule(RULESET, "AvoidFieldNameMatchingMethodName");
        addRule(RULESET, "AvoidFieldNameMatchingTypeName");
        addRule(RULESET, "AvoidInstanceofChecksInCatchClause");
        addRule(RULESET, "AvoidLiteralsInIfCondition");
        addRule(RULESET, "AvoidLosingExceptionInformation");
        addRule(RULESET, "AvoidMultipleUnaryOperators");
        addRule(RULESET, "AvoidUsingOctalValues");
        addRule(RULESET, "BadComparison");
        addRule(RULESET, "BeanMembersShouldSerialize");
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
        addRule(RULESET, "DoNotCallSystemExit");
        addRule(RULESET, "DoNotExtendJavaLangThrowable");
        addRule(RULESET, "DoNotHardCodeSDCard");
        addRule(RULESET, "DoNotThrowExceptionInFinally");
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
        addRule(RULESET, "InvalidSlf4jMessageFormat");
        addRule(RULESET, "JumbledIncrementer");
        addRule(RULESET, "JUnitSpelling");
        addRule(RULESET, "JUnitStaticSuite");
        addRule(RULESET, "LoggerIsNotStaticFinal");
        addRule(RULESET, "MethodWithSameNameAsEnclosingClass");
        addRule(RULESET, "MisplacedNullCheck");
        addRule(RULESET, "MissingBreakInSwitch");
        addRule(RULESET, "MissingSerialVersionUID");
        addRule(RULESET, "MissingStaticMethodInNonInstantiatableClass");
        addRule(RULESET, "MoreThanOneLogger");
        addRule(RULESET, "NonCaseLabelInSwitchStatement");
        addRule(RULESET, "NonStaticInitializer");
        addRule(RULESET, "NullAssignment");
        addRule(RULESET, "OverrideBothEqualsAndHashcode");
        addRule(RULESET, "ProperCloneImplementation");
        addRule(RULESET, "ProperLogger");
        addRule(RULESET, "ReturnEmptyArrayRatherThanNull");
        addRule(RULESET, "ReturnFromFinallyBlock");
        addRule(RULESET, "SimpleDateFormatNeedsLocale");
        addRule(RULESET, "SingleMethodSingleton");
        addRule(RULESET, "SingletonClassReturningNewInstance");
        addRule(RULESET, "StaticEJBFieldShouldBeFinal");
        addRule(RULESET, "StringBufferInstantiationWithChar");
        addRule(RULESET, "SuspiciousEqualsMethodName");
        addRule(RULESET, "SuspiciousHashcodeMethodName");
        addRule(RULESET, "SuspiciousOctalEscape");
        addRule(RULESET, "TestClassWithoutTestCases");
        addRule(RULESET, "UnconditionalIfStatement");
        addRule(RULESET, "UnnecessaryBooleanAssertion");
        addRule(RULESET, "UnnecessaryCaseChange");
        addRule(RULESET, "UnnecessaryConversionTemporary");
        addRule(RULESET, "UnusedNullCheckInEquals");
        addRule(RULESET, "UseCorrectExceptionLogging");
        addRule(RULESET, "UseEqualsToCompareStrings");
        addRule(RULESET, "UselessOperationOnImmutable");
        addRule(RULESET, "UseLocaleWithCaseConversions");
        addRule(RULESET, "UseProperClassLoader");
    }

}
