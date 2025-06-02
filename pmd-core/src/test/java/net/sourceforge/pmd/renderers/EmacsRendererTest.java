/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

class EmacsRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new EmacsRenderer();
    }

    @Override
    String getExpected() {
        return getSourceCodeFilename() + ":1: blah" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return "";
    }

    @Override
    String getExpectedMultiple() {
        return getSourceCodeFilename() + ":1: blah" + EOL + getSourceCodeFilename() + ":1: blah" + EOL;
    }
}
