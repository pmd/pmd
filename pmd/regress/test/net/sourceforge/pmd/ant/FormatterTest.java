package test.net.sourceforge.pmd.ant;

import junit.framework.TestCase;
import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import org.apache.tools.ant.BuildException;

public class FormatterTest extends TestCase {

    public void testType() {
        Formatter f = new Formatter();
        f.setType("xml");
        assertTrue(f.getRenderer() instanceof XMLRenderer);
        f.setType("text");
        assertTrue(f.getRenderer() instanceof TextRenderer);
        f.setType("html");
        assertTrue(f.getRenderer() instanceof HTMLRenderer);
        try {
            f.setType("FAIL");
            throw new RuntimeException("Should have failed!");
        } catch (BuildException be) {
            // cool
        }
    }

}
