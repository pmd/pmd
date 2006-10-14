package test.net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.EmacsRenderer;

public class EmacsRendererTest extends AbstractRendererTst {

    public AbstractRenderer getRenderer() {
        return new EmacsRenderer();
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
}
