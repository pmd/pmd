package test.net.sourceforge.pmd.ant;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Tests for the net.sourceforge.pmd.ant package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class AntTests
{
  /**
   * test suite
   *
   * @return test suite
   */
  public static Test suite(  )
  {
    TestSuite suite = new TestSuite( "Test for test.net.sourceforge.pmd.ant" );

    //$JUnit-BEGIN$
    suite.addTestSuite( FormatterTest.class );
    suite.addTestSuite( PMDTaskTest.class );

    //$JUnit-END$
    return suite;
  }
}


/*
 * $Log$
 * Revision 1.1  2003/09/29 14:32:30  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
