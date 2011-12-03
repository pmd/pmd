/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.ant.Formatter;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class FormatterTest {

    @Ignore
    @Test
    public void testType() {
/*
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
            f.getRenderer();
            throw new RuntimeException("Should have failed!");
        } catch (BuildException be) {
            // cool
        }
*/
    }

    @Test
    public void testNull() {
        Formatter f = new Formatter();
        assertTrue("Formatter toFile should start off null!", f.isNoOutputSupplied());
        f.setToFile(new File("foo"));
        assertFalse("Formatter toFile should not be null!", f.isNoOutputSupplied());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FormatterTest.class);
    }
}
