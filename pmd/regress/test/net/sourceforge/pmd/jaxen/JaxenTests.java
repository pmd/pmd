/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.jaxen;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * tests for the net.sourceforge.pmd.jaxen package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class JaxenTests
{
  /**
   * test suite
   *
   * @return test suite
   */
  public static Test suite(  )
  {
    TestSuite suite =
      new TestSuite( "Test for test.net.sourceforge.pmd.jaxen" );

    //$JUnit-BEGIN$
    suite.addTestSuite( AttributeAxisIteratorTest.class );
    suite.addTestSuite( AttributeTest.class );
    suite.addTestSuite( DocumentNavigatorTest.class );

    //$JUnit-END$
    return suite;
  }
}


/*
 * $Log$
 * Revision 1.2  2003/11/20 16:01:03  tomcopeland
 * Changing over license headers in the source code
 *
 * Revision 1.1  2003/09/29 14:32:31  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
