package test.net.sourceforge.pmd.rules;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.net.sourceforge.pmd.rules.design.DesignRulesTests;
import test.net.sourceforge.pmd.rules.junit.JUnitRulesTests;
import test.net.sourceforge.pmd.rules.strictexception.StrictExceptionRulesTests;


/**
 * tests for the net.sourceforge.pmd.rules package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class RulesTests
{
  /**
   * test suite
   *
   * @return test suite
   */
  public static Test suite(  )
  {
    TestSuite suite =
      new TestSuite( "Test for test.net.sourceforge.pmd.rules" );

    //$JUnit-BEGIN$
    // test suites for the sub packages
    suite.addTest( DesignRulesTests.suite(  ) );
    suite.addTest( JUnitRulesTests.suite(  ) );
    suite.addTest( StrictExceptionRulesTests.suite(  ) );

    suite.addTestSuite( AccessorClassGenerationRuleTest.class );
    suite.addTestSuite( AssignmentInOperandRuleTest.class );
    suite.addTestSuite( AtLeastOneConstructorRuleTest.class );
    suite.addTestSuite( AvoidDeeplyNestedIfStmtsRuleTest.class );
    suite.addTestSuite( AvoidDuplicateLiteralsRuleTest.class );
    suite.addTestSuite( AvoidReassigningParametersRuleTest.class );
    suite.addTestSuite( BeanMembersShouldSerializeRuleTest.class );
    suite.addTestSuite( BooleanInstantiationRuleTest.class );
    suite.addTestSuite( ClassNamingConventionsRuleTest.class );
    suite.addTestSuite( ConstructorCallsOverridableMethodRuleTest.class );
    suite.addTestSuite( CouplingBetweenObjectsRuleTest.class );
    suite.addTestSuite( CyclomaticComplexityRuleTest.class );
    suite.addTestSuite( DontImportJavaLangRuleTest.class );
    suite.addTestSuite( DoubleCheckedLockingRuleTest.class );
    suite.addTestSuite( DuplicateImportsRuleTest.class );
    suite.addTestSuite( EmptyCatchBlockRuleTest.class );
    suite.addTestSuite( EmptyFinallyBlockRuleTest.class );
    suite.addTestSuite( EmptyIfStmtRuleTest.class );
    suite.addTestSuite( EmptySwitchStmtRuleTest.class );
    suite.addTestSuite( EmptyTryBlockRuleTest.class );
    suite.addTestSuite( EmptyWhileStmtRuleTest.class );
    suite.addTestSuite( ExcessiveImportsRuleTest.class );
    suite.addTestSuite( ExcessivePublicCountRuleTest.class );
    suite.addTestSuite( FinalFieldCouldBeStaticRuleTest.class );
    suite.addTestSuite( FinalizeShouldBeProtectedRuleTest.class );
    suite.addTestSuite( ForLoopShouldBeWhileLoopRuleTest.class );
    suite.addTestSuite( ForLoopsMustUseBracesRuleTest.class );
    suite.addTestSuite( IdempotentOperationsRuleTest.class );
    suite.addTestSuite( IfElseStmtsMustUseBracesRuleTest.class );
    suite.addTestSuite( IfStmtsMustUseBracesRuleTest.class );
    suite.addTestSuite( ImportFromSamePackageRuleTest.class );
    suite.addTestSuite( JumbledIncrementerRuleTest.class );
    suite.addTestSuite( LongVariableRuleTest.class );
    suite.addTestSuite( MethodNamingConventionsRuleTest.class );
    suite.addTestSuite( OverrideBothEqualsAndHashcodeRuleTest.class );
    suite.addTestSuite( ReturnFromFinallyBlockTest.class );
    suite.addTestSuite( ShortMethodNameRuleTest.class );
    suite.addTestSuite( ShortVariableRuleTest.class );
    suite.addTestSuite( SimplifyBooleanExpressionsRuleTest.class );
    suite.addTestSuite( SimplifyBooleanReturnsRuleTest.class );
    suite.addTestSuite( StringInstantiationRuleTest.class );
    suite.addTestSuite( StringToStringRuleTest.class );
    suite.addTestSuite( SwitchStmtsShouldHaveDefaultRuleTest.class );
    suite.addTestSuite( UnnecessaryConstructorRuleTest.class );
    suite.addTestSuite( UnnecessaryTemporariesRuleTest.class );
    suite.addTestSuite( UnusedFormalParameterRuleTest.class );
    suite.addTestSuite( UnusedImportsRuleTest.class );
    suite.addTestSuite( UnusedLocalVariableTest.class );
    suite.addTestSuite( UnusedModifierRuleTest.class );
    suite.addTestSuite( UnusedPrivateFieldRuleTest.class );
    suite.addTestSuite( UnusedPrivateMethodRuleTest.class );
    suite.addTestSuite( VariableNamingConventionsRuleTest.class );
    suite.addTestSuite( WhileLoopsMustUseBracesRuleTest.class );
    suite.addTestSuite( XPathRuleTest.class );

    //$JUnit-END$
    return suite;
  }
}


/*
 * $Log$
 * Revision 1.2  2003/11/13 20:28:03  tomcopeland
 * Implemented AbstractNamingRule
 *
 * Revision 1.1  2003/09/29 14:32:32  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
