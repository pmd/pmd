package test.net.sourceforge.pmd.renderers;

import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.TextRenderer;

public class TextRendererTest extends AbstractRendererTst {

    @Override
    public Renderer getRenderer() {
        return new TextRenderer(new Properties());
    }

    @Override
    public String getExpected() {
        return "n/a:1\tmsg" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "";
    }

    @Override
    public String getExpectedMultiple() {
        return "n/a:1\tmsg" + PMD.EOL + "n/a:1\tmsg" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return "file\t-\tError" + PMD.EOL;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TextRendererTest.class);
    }
}
