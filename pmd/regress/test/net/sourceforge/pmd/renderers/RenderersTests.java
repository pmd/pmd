/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.renderers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * tests for the net.sourceforge.pmd.renderers package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
@RunWith(Suite.class)
@SuiteClasses({CSVRendererTest.class, EmacsRendererTest.class, XMLRendererTest.class, TextPadRendererTest.class})
public class RenderersTests {
}


/*
 * $Log$
 * Revision 1.7  2007/02/09 01:38:02  allancaplan
 * Moving to JUnit 4
 *
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
