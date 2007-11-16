package test.net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.IOException;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.renderers.AbstractRenderer;
import net.sourceforge.pmd.renderers.YAHTMLRenderer;

import org.junit.After;
import org.junit.Before;

public class YAHTMLRendererTest extends AbstractRendererTst {

    private String outputDir;

    @Before
    public void setUp() throws IOException {
        outputDir = getTemporaryDirectory("pmdtest").getAbsolutePath();
    }

    @After
    public void cleanUp() {
        deleteDirectory(new File(outputDir));
    }

    private File getTemporaryDirectory(String prefix) throws IOException {
        // TODO: move to util class?
        File dir = File.createTempFile(prefix, "");
        dir.delete();
        dir.mkdir();
        return dir;
    }

    private void deleteDirectory(File dir) {
        // TODO: move to util class?
        File[] a = dir.listFiles();
        if (a != null) {
            for (File f: a) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();        
    }

    public AbstractRenderer getRenderer() {
        return new YAHTMLRenderer(outputDir);
    }

    public String getExpected() {
        return "<h3 align=\"center\">The HTML files are located in '" + outputDir + "'.</h3>";
    }

    public String getExpectedEmpty() {
        return getExpected();
    }
    
    public String getExpectedMultiple() {
        return getExpected();
    }
    
    public String getExpectedError(ProcessingError error) {
        return getExpected();
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(YAHTMLRendererTest.class);
    }
}
