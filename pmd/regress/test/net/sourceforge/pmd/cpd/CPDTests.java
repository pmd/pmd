package test.net.sourceforge.pmd.cpd;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * tests for the net.sourceforge.pmd.cpd package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
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
    suite.addTestSuite( MarkComparatorTest.class );
    suite.addTestSuite( MarkTest.class );
    suite.addTestSuite( MatchAlgorithmTest.class );
    suite.addTestSuite( MatchTest.class );
    suite.addTestSuite( SourceCodeTest.class );
    suite.addTestSuite( XMLRendererTest.class );

    //$JUnit-END$
    return suite;
  }
}


/*
 * $Log$
 * Revision 1.1  2003/09/29 14:32:31  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
