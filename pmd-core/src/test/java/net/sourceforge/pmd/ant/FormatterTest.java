/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import java.io.File;

import org.junit.jupiter.api.Assertions;
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
        Assertions.assertTrue(f.createRenderer() instanceof XMLRenderer);
        f.setType("text");
        Assertions.assertTrue(f.createRenderer() instanceof TextRenderer);
        f.setType("csv");
        Assertions.assertTrue(f.createRenderer() instanceof CSVRenderer);
        f.setType("html");
        Assertions.assertTrue(f.createRenderer() instanceof HTMLRenderer);
        try {
            f.setType("FAIL");
            f.createRenderer();
            Assertions.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException be) {
            Assertions.assertTrue(be.getMessage().startsWith("Can't find the custom format FAIL"));
        }
    }

    @Test
    void testNull() {
        Formatter f = new Formatter();
        Assertions.assertTrue(f.isNoOutputSupplied(), "Formatter toFile should start off null!");
        f.setToFile(new File("foo"));
        Assertions.assertFalse(f.isNoOutputSupplied(), "Formatter toFile should not be null!");
    }
}
