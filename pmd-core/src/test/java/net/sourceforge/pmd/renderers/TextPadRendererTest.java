/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

class TextPadRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new TextPadRenderer();
    }

    @Override
    String getExpected() {
        return getSourceCodeFilename() + "(1,  Foo):  blah" + EOL;
    }

    @Override
    String getExpectedEmpty() {
        return "";
    }

    @Override
    String getExpectedMultiple() {
        return getSourceCodeFilename() + "(1,  Foo):  blah" + EOL + getSourceCodeFilename() + "(1,  Boo):  blah" + EOL;
    }
}
