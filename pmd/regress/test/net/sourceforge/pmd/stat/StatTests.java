package test.net.sourceforge.pmd.stat;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * tests for the net.sourceforge.pmd.stat package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class StatTests {
    /**
     * test suite
     *
     * @return test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for test.net.sourceforge.pmd.stat");

        //$JUnit-BEGIN$
        suite.addTestSuite(MetricTest.class);
        suite.addTestSuite(StatisticalRuleTest.class);

        //$JUnit-END$
        return suite;
    }
}


/*
 * $Log$
 * Revision 1.3  2006/02/10 14:26:27  tomcopeland
 * Huge reformatting checkin
 *
 * Revision 1.2  2006/02/10 14:15:22  tomcopeland
 * Latest source from Pieter, everything compiles and all the tests pass with the exception of a few missing rules in basic-jsp.xml
 *
 * Revision 1.1  2003/09/29 14:32:32  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
