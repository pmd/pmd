/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.ast;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ASTTests
{
  /**
   * test suite
   *
   * @return test suite
   */
  public static Test suite(  )
  {
    TestSuite suite = new TestSuite( "Test for test.net.sourceforge.pmd.ast" );

    //$JUnit-BEGIN$
    suite.addTestSuite( ASTImportDeclarationTest.class );
    suite.addTestSuite( ASTVariableDeclaratorIdTest.class );
    suite.addTestSuite( AccessNodeTest.class );
    suite.addTestSuite( ClassDeclTest.class );
    suite.addTestSuite( FieldDeclTest.class );
    suite.addTestSuite( MethodDeclTest.class );
    suite.addTestSuite( SimpleNodeTest.class );

    //$JUnit-END$
    return suite;
  }
}


/*
 * $Log$
 * Revision 1.4  2006/02/10 14:15:19  tomcopeland
 * Latest source from Pieter, everything compiles and all the tests pass with the exception of a few missing rules in basic-jsp.xml
 *
 * Revision 1.3  2004/04/08 18:51:07  tomcopeland
 * Implemented RFE 925839 - Added some more detail to the UseSingletonRule.
 *
 * Revision 1.2  2003/11/20 16:01:02  tomcopeland
 * Changing over license headers in the source code
 *
 * Revision 1.1  2003/09/29 14:32:31  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
