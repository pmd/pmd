package test.net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.renderers.Renderer;
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

    @Override
    public Renderer getRenderer() {
	Properties properties = new  Properties();
	properties.put(YAHTMLRenderer.OUTPUT_DIR, outputDir);
        return new YAHTMLRenderer(properties);
    }

    @Override
    public String getExpected() {
        return "<h3 align=\"center\">The HTML files are located in '" + outputDir + "'.</h3>" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return getExpected();
    }

    @Override
    public String getExpectedMultiple() {
        return getExpected();
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return getExpected();
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(YAHTMLRendererTest.class);
    }
}
