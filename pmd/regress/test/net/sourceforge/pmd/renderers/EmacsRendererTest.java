package test.net.sourceforge.pmd.renderers;

import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.Renderer;

public class EmacsRendererTest extends AbstractRendererTst {

    @Override
    public Renderer getRenderer() {
        return new EmacsRenderer(new Properties());
    }

    @Override
    public String getExpected() {
        return "n/a:1: msg" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "n/a:1: msg" + PMD.EOL + "n/a:1: msg" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(EmacsRendererTest.class);
    }
}
