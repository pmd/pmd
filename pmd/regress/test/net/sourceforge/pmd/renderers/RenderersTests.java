package test.net.sourceforge.pmd.renderers;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * tests for the net.sourceforge.pmd.renderers package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class RenderersTests
{
  /**
   * test suite
   *
   * @return test suite
   */
  public static Test suite(  )
  {
    TestSuite suite =
      new TestSuite( "Test for test.net.sourceforge.pmd.renderers" );

    //$JUnit-BEGIN$
    suite.addTestSuite( XMLRendererTest.class );

    //$JUnit-END$
    return suite;
  }
}


/*
 * $Log$
 * Revision 1.1  2003/09/29 14:32:32  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
