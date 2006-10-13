/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.renderers;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * tests for the net.sourceforge.pmd.renderers package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class RenderersTests {
    /**
     * test suite
     *
     * @return test suite
     */
    public static Test suite() {
        TestSuite suite =
                new TestSuite("Test for test.net.sourceforge.pmd.renderers");

        //$JUnit-BEGIN$
        suite.addTestSuite(CSVRendererTest.class);
        suite.addTestSuite(EmacsRendererTest.class);
        suite.addTestSuite(XMLRendererTest.class);
        suite.addTestSuite(TextPadRendererTest.class);

        //$JUnit-END$
        return suite;
    }
}


/*
 * $Log$
 * Revision 1.6  2006/10/13 23:49:38  allancaplan
 * Improving JUnit tests
 *
 * I went over the output, it looks correct. For many, having a test will help down the road to throw a flag if anything changes
 *
 * Revision 1.5  2006/02/10 14:26:25  tomcopeland
 * Huge reformatting checkin
 *
 * Revision 1.4  2006/02/10 14:15:20  tomcopeland
 * Latest source from Pieter, everything compiles and all the tests pass with the exception of a few missing rules in basic-jsp.xml
 *
 * Revision 1.3  2003/11/20 16:53:10  tomcopeland
 * Changing over license headers in the source code, cleaned up a test
 *
 * Revision 1.2  2003/10/01 14:55:55  tomcopeland
 * Added unit tests for TextPad integration; thanks to Jeff Epstein
 *
 * Revision 1.1  2003/09/29 14:32:32  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
