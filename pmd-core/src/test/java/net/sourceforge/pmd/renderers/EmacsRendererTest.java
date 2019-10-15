/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;

public class EmacsRendererTest extends AbstractRendererTest {

    @Override
    public Renderer getRenderer() {
        return new EmacsRenderer();
    }

    @Override
    public String getExpected() {
        return getSourceCodeFilename() + ":1: blah" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return getSourceCodeFilename() + ":1: blah" + PMD.EOL + getSourceCodeFilename() + ":1: blah" + PMD.EOL;
    }
}
