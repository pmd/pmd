/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.cpd;

import junit.framework.Test;
import junit.framework.TestSuite;


public class CPDTests
{
  /**
   * test suite
   *
   * @return test suite
   */
  public static Test suite(  )
  {
    TestSuite suite = new TestSuite( "Test for test.net.sourceforge.pmd.cpd" );

    //$JUnit-BEGIN$
    suite.addTestSuite( FileReporterTest.class );
    suite.addTestSuite( JavaTokensTokenizerTest.class );
    suite.addTestSuite( LanguageFactoryTest.class );
    suite.addTestSuite( MatchAlgorithmTest.class );
    suite.addTestSuite( MatchTest.class );
    suite.addTestSuite( SourceCodeTest.class );
    suite.addTestSuite( XMLRendererTest.class );

    //$JUnit-END$
    return suite;
  }
}
