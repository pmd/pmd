/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;

public class TextPadRendererTest extends AbstractRendererTst {

    @Override
    public Renderer getRenderer() {
        return new TextPadRenderer();
    }

    @Override
    public String getExpected() {
        return "n/a(1,  Foo):  blah" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "n/a(1,  Foo):  blah" + PMD.EOL + "n/a(1,  Foo):  blah" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TextPadRendererTest.class);
    }
}
