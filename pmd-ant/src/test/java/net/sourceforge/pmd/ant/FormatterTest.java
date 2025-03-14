/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

class FormatterTest {

    @Test
    void testType() {
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
    void testNull() {
        Formatter f = new Formatter();
        assertTrue(f.isNoOutputSupplied(), "Formatter toFile should start off null!");
        f.setToFile(new File("foo"));
        assertFalse(f.isNoOutputSupplied(), "Formatter toFile should not be null!");
    }
}
