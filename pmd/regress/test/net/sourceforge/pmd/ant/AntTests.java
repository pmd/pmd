/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
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
 * Revision 1.4  2006/02/10 14:15:19  tomcopeland
 * Latest source from Pieter, everything compiles and all the tests pass with the exception of a few missing rules in basic-jsp.xml
 *
 * Revision 1.3  2003/11/20 16:01:01  tomcopeland
 * Changing over license headers in the source code
 *
 * Revision 1.2  2003/10/07 18:49:19  tomcopeland
 * Added copyright headers
 *
 * Revision 1.1  2003/09/29 14:32:30  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
