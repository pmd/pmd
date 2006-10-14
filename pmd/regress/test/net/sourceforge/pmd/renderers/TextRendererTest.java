package test.net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;

public class TextRendererTest extends AbstractRendererTst {

    public AbstractRenderer getRenderer() {
        return new TextRenderer();
    }

    public String getExpected() {
        return PMD.EOL + "n/a:1\tmsg";
    }

    public String getExpectedEmpty() {
        return "No problems found!";
    }

    public String getExpectedMultiple() {
        return PMD.EOL + "n/a:1\tmsg" + PMD.EOL + "n/a:1\tmsg";
    }

    public String getExpectedError(ProcessingError error) {
        return PMD.EOL + "file\t-\tError";
    }
    
}
