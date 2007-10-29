package test.net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.YAHTMLRenderer;

public class YAHTMLRendererTest extends AbstractRendererTst {

    private static final String OUTPUT_DIR = System.getProperty("java.io.tmpdir");

    public AbstractRenderer getRenderer() {
        return new YAHTMLRenderer(OUTPUT_DIR);
    }

    private static final String EXPECTED_OUTPUT = "<h3 align=\"center\">The HTML files are located in '" + OUTPUT_DIR + "'.</h3>";

    public String getExpected() {
        return EXPECTED_OUTPUT;
    }

    public String getExpectedEmpty() {
        return EXPECTED_OUTPUT;
    }
    
    public String getExpectedMultiple() {
        return EXPECTED_OUTPUT;
    }
    
    public String getExpectedError(ProcessingError error) {
        return EXPECTED_OUTPUT;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(YAHTMLRendererTest.class);
    }
}
