/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules.design;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * tests for the net.sourceforge.pmd.rules.design package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class DesignRulesTests
{
  /**
   * test suite
   *
   * @return test suite
   */
  public static Test suite(  )
  {
    TestSuite suite =
      new TestSuite( "Test for test.net.sourceforge.pmd.rules.design" );

    //$JUnit-BEGIN$
    suite.addTestSuite( LongClassRuleTest.class );
    suite.addTestSuite( LongMethodRuleTest.class );
    suite.addTestSuite( LongParameterListRuleTest.class );
    suite.addTestSuite( LooseCouplingRuleTest.class );
    suite.addTestSuite( NullAssignmentRuleTest.class );
    suite.addTestSuite( OnlyOneReturnRuleTest.class );
    suite.addTestSuite( PositionalIteratorRuleTest.class );
    suite.addTestSuite( SwitchDensityTest.class );
    suite.addTestSuite( UseSingletonRuleTest.class );

    //$JUnit-END$
    return suite;
  }
}


/*
 * $Log$
 * Revision 1.2  2003/11/20 16:53:10  tomcopeland
 * Changing over license headers in the source code, cleaned up a test
 *
 * Revision 1.1  2003/09/29 14:32:32  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
