package test.net.sourceforge.pmd.ast;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * tests for the net.sourceforge.pmd.ast package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
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
    suite.addTestSuite( AssertTest.class );
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
 * Revision 1.1  2003/09/29 14:32:31  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
