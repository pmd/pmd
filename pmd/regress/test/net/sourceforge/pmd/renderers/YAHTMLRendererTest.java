package test.net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.YAHTMLRenderer;

public class YAHTMLRendererTest extends AbstractRendererTst {

    public AbstractRenderer getRenderer() {
        return new YAHTMLRenderer();
    }

    public String getExpected() {
        return "<h3 align=\"center\">The HTML files are created above the project directory.</h3>";
    }

    public String getExpectedEmpty() {
        return "<h3 align=\"center\">The HTML files are created above the project directory.</h3>";
    }
    
    public String getExpectedMultiple() {
        return "<h3 align=\"center\">The HTML files are created above the project directory.</h3>";
    }
    
    public String getExpectedError(ProcessingError error) {
        return "<h3 align=\"center\">The HTML files are created above the project directory.</h3>";
    }
}
