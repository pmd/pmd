/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * tests for the net.sourceforge.pmd.symboltable package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class SymbolTableTests
{
  /**
   * test suite
   *
   * @return test suite
   */
  public static Test suite(  )
  {
    TestSuite suite =
      new TestSuite( "Test for test.net.sourceforge.pmd.symboltable" );

    //$JUnit-BEGIN$
    suite.addTestSuite( AbstractScopeTest.class );
    suite.addTestSuite( AcceptanceTest.class );
    suite.addTestSuite( BasicScopeFactoryTest.class );
    suite.addTestSuite( ClassScopeTest.class );
    suite.addTestSuite( DeclarationFinderTest.class );
    suite.addTestSuite( ImageFinderFunctionTest.class );
    suite.addTestSuite( LocalScopeTest.class );
    suite.addTestSuite( NameOccurrenceTest.class );
    suite.addTestSuite( NameOccurrencesTest.class );
    suite.addTestSuite( ScopeCreationVisitorTest.class );
    suite.addTestSuite( TypeSetTest.class );
    suite.addTestSuite( VariableNameDeclarationTest.class );

    //$JUnit-END$
    return suite;
  }
}


/*
 * $Log$
 * Revision 1.2  2003/11/21 21:22:17  tomcopeland
 * Continuing to clean up license headers
 *
 * Revision 1.1  2003/09/29 14:32:33  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
