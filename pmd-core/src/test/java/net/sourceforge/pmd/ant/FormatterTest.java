/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

public class FormatterTest {

    @Test
    public void testType() {
        Formatter f = new Formatter();
        f.setType("xml");
        assertTrue(f.createRenderer() instanceof XMLRenderer);
        f.setType("text");
        assertTrue(f.createRenderer() instanceof TextRenderer);
        f.setType("csv");
        assertTrue(f.createRenderer() instanceof CSVRenderer);
        f.setType("html");
        assertTrue(f.createRenderer() instanceof HTMLRenderer);
        try {
            f.setType("FAIL");
            f.createRenderer();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException be) {
            assertTrue(be.getMessage().startsWith("Can't find the custom format FAIL"));
        }
    }

    @Test
    public void testNull() {
        Formatter f = new Formatter();
        assertTrue("Formatter toFile should start off null!", f.isNoOutputSupplied());
        f.setToFile(new File("foo"));
        assertFalse("Formatter toFile should not be null!", f.isNoOutputSupplied());
    }
}
