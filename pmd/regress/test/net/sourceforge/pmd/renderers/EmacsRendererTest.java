package test.net.sourceforge.pmd.renderers;

import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.renderers.EmacsRenderer;
import net.sourceforge.pmd.renderers.Renderer;

public class EmacsRendererTest extends AbstractRendererTst {

    public Renderer getRenderer() {
        return new EmacsRenderer(new Properties());
    }

    public String getExpected() {
        return PMD.EOL + "n/a:1: msg";
    }

    public String getExpectedEmpty() {
        return "";
    }

    public String getExpectedMultiple() {
        return PMD.EOL + "n/a:1: msg" + PMD.EOL + "n/a:1: msg";
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(EmacsRendererTest.class);
    }
}
