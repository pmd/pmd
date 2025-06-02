/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * tests for the net.sourceforge.pmd.renderers package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 */
@Suite
@SelectClasses({
    CodeClimateRendererTest.class,
    CSVRendererTest.class,
    EmacsRendererTest.class,
    HTMLRendererTest.class,
    IDEAJRendererTest.class,
    JsonRendererTest.class,
    TextColorRendererTest.class,
    SarifRendererTest.class,
    SummaryHTMLRendererTest.class,
    TextPadRendererTest.class,
    TextRendererTest.class,
    VBHTMLRendererTest.class,
    XMLRendererTest.class,
    XSLTRendererTest.class,
    YAHTMLRendererTest.class
})
class RenderersTests {
}
