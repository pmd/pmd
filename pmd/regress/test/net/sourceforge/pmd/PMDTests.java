package test.net.sourceforge.pmd;

import junit.framework.Test;
import junit.framework.TestSuite;

import test.net.sourceforge.pmd.ant.AntTests;
import test.net.sourceforge.pmd.ast.ASTTests;
import test.net.sourceforge.pmd.cpd.CPDTests;
import test.net.sourceforge.pmd.jaxen.JaxenTests;
import test.net.sourceforge.pmd.renderers.RenderersTests;
import test.net.sourceforge.pmd.rules.RulesTests;
import test.net.sourceforge.pmd.stat.StatTests;
import test.net.sourceforge.pmd.symboltable.SymbolTableTests;
import test.net.sourceforge.pmd.util.UtilTests;


/**
 * tests for the net.sourceforge.pmd package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class PMDTests
{
  /**
   * all tests for PMD packaged in one suite
   *
   * @return test suite
   */
  public static Test suite(  )
  {
    TestSuite suite = new TestSuite( "Test for test.net.sourceforge.pmd" );

    // tests for the subpackages
    suite.addTest( AntTests.suite(  ) );
    suite.addTest( ASTTests.suite(  ) );
    suite.addTest( CPDTests.suite(  ) );
    suite.addTest( JaxenTests.suite(  ) );
    suite.addTest( RenderersTests.suite(  ) );
    suite.addTest( RulesTests.suite(  ) );
    suite.addTest( StatTests.suite(  ) );
    suite.addTest( SymbolTableTests.suite(  ) );
    suite.addTest( UtilTests.suite(  ) );

    //$JUnit-BEGIN$
    suite.addTestSuite( CommandLineOptionsTest.class );
    suite.addTestSuite( ExternalRuleIDTest.class );
    suite.addTestSuite( ReportTest.class );
    suite.addTestSuite( RuleContextTest.class );
    suite.addTestSuite( RuleSetFactoryTest.class );
    suite.addTestSuite( RuleSetTest.class );
    suite.addTestSuite( RuleViolationTest.class );

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
