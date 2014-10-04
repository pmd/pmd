package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;

public class EmacsRendererTest extends AbstractRendererTst {

    @Override
    public Renderer getRenderer() {
        return new EmacsRenderer();
    }

    @Override
    public String getExpected() {
        return "n/a:1: blah" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "n/a:1: blah" + PMD.EOL + "n/a:1: blah" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(EmacsRendererTest.class);
    }
}
