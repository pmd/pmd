/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.ant;

import junit.framework.TestCase;
import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import org.apache.tools.ant.BuildException;

import java.io.File;

public class FormatterTest extends TestCase {

    public void testType() {
        Formatter f = new Formatter();
        f.setType("xml");
        assertTrue(f.getRenderer() instanceof XMLRenderer);
        f.setType("text");
        assertTrue(f.getRenderer() instanceof TextRenderer);
        f.setType("csv");
        assertTrue(f.getRenderer() instanceof CSVRenderer);
        f.setType("html");
        assertTrue(f.getRenderer() instanceof HTMLRenderer);
        try {
            f.setType("FAIL");
            throw new RuntimeException("Should have failed!");
        } catch (BuildException be) {
            // cool
        }
    }

    public void testNull() {
        Formatter f = new Formatter();
        assertTrue("Formatter toFile should start off null!", f.isToFileNull());
        f.setToFile(new File("foo"));
        assertFalse("Formatter toFile should not be null!", f.isToFileNull());
    }

}
