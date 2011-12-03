/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.TextPadRenderer;


public class TextPadRendererTest extends AbstractRendererTst{

    @Override
    public Renderer getRenderer() {
        return new TextPadRenderer(new Properties());
    }

    @Override
    public String getExpected() {
        return "n/a(1,  Foo):  msg" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "n/a(1,  Foo):  msg" + PMD.EOL + "n/a(1,  Foo):  msg" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TextPadRendererTest.class);
    }
}









