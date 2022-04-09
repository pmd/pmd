/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * tests for the net.sourceforge.pmd.renderers package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 */
@RunWith(Suite.class)
@SuiteClasses({
    CodeClimateRendererTest.class,
    CSVRendererTest.class,
    EmacsRendererTest.class,
    HTMLRendererTest.class,
    IDEAJRendererTest.class,
    JsonRendererTest.class,
    PapariTextRendererTest.class,
    SarifRendererTest.class,
    SummaryHTMLRendererTest.class,
    TextPadRendererTest.class,
    TextRendererTest.class,
    VBHTMLRendererTest.class,
    XMLRendererTest.class,
    XSLTRendererTest.class,
    YAHTMLRendererTest.class
})
public class RenderersTests {
}
