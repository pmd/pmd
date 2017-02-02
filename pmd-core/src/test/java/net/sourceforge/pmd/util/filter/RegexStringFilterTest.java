/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RegexStringFilterTest {

    @Test
    public void testFilterAbsoluteWithExtension() {
        String pattern = "C:/workspace/project/X.java";
        verifyFilterFalse("same relative path w/ extension", pattern, "X.java");
        verifyFilterFalse("same relative path w/o extension", pattern, "X");
        verifyFilterFalse("different relative path w/ extension", pattern, "Y.java");
        verifyFilterFalse("different relative path w/o extension", pattern, "Y");
        verifyFilterTrue("same absolute path w/ extension", pattern, "C:/workspace/project/X.java");
        verifyFilterFalse("same absolute path w/o extension", pattern, "C:/workspace/project/X");
        verifyFilterFalse("different absolute path w/ extension", pattern, "C:/workspace/project/Y.java");
        verifyFilterFalse("different absolute path w/o extension", pattern, "C:/workspace/project/Y");
    }

    @Test
    public void testFilterAbsoluteWithoutExtension() {
        String pattern = "C:/workspace/project/X";
        verifyFilterFalse("same relative path w/ extension", pattern, "X.java");
        verifyFilterFalse("same relative path w/o extension", pattern, "X");
        verifyFilterFalse("different relative path w/ extension", pattern, "Y.java");
        verifyFilterFalse("different relative path w/o extension", pattern, "Y");
        verifyFilterFalse("same absolute path w/ extension", pattern, "C:/workspace/project/X.java");
        verifyFilterTrue("same absolute path w/o extension", pattern, "C:/workspace/project/X");
        verifyFilterFalse("different absolute path w/ extension", pattern, "C:/workspace/project/Y.java");
        verifyFilterFalse("different absolute path w/o extension", pattern, "C:/workspace/project/Y");
    }

    @Test
    public void testFilterRelativeWithExtension() {
        String pattern = ".*X.java";
        verifyFilterTrue("same relative path w/ extension", pattern, "X.java");
        verifyFilterFalse("same relative path w/o extension", pattern, "X");
        verifyFilterFalse("different relative path w/ extension", pattern, "Y.java");
        verifyFilterFalse("different relative path w/o extension", pattern, "Y");
        verifyFilterTrue("same absolute path w/ extension", pattern, "C:/workspace/project/X.java");
        verifyFilterFalse("same absolute path w/o extension", pattern, "C:/workspace/project/X");
        verifyFilterFalse("different absolute path w/ extension", pattern, "C:/workspace/project/Y.java");
        verifyFilterFalse("different absolute path w/o extension", pattern, "C:/workspace/project/Y");
    }

    @Test
    public void testFilterRelativeWithoutExtension() {
        String pattern = ".*X";
        verifyFilterFalse("same relative path w/ extension", pattern, "X.java");
        verifyFilterTrue("same relative path w/o extension", pattern, "X");
        verifyFilterFalse("different relative path w/ extension", pattern, "Y.java");
        verifyFilterFalse("different relative path w/o extension", pattern, "Y");
        verifyFilterFalse("same absolute path w/ extension", pattern, "C:/workspace/project/X.java");
        verifyFilterTrue("same absolute path w/o extension", pattern, "C:/workspace/project/X");
        verifyFilterFalse("different absolute path w/ extension", pattern, "C:/workspace/project/Y.java");
        verifyFilterFalse("different absolute path w/o extension", pattern, "C:/workspace/project/Y");
    }

    @Test
    public void testEndsWith() {
        // These patterns cannot be optimized to use String.endsWith
        verifyEndsWith("no literal path", ".*", null);
        verifyEndsWith("not ends with", "x", null);
        verifyEndsWith("glob on end", ".*XXX.*", null);
        verifyEndsWith("special character \\", ".*X\\Y", null);
        verifyEndsWith("special character [", ".*X[Y", null);
        verifyEndsWith("special character (", ".*X(Y", null);
        verifyEndsWith("special character *", ".*X*Y", null);
        verifyEndsWith("special character ?", ".*X?Y", null);
        verifyEndsWith("special character +", ".*X+Y", null);
        verifyEndsWith("special character |", ".*X|Y", null);
        verifyEndsWith("special character {", ".*X{Y", null);
        verifyEndsWith("special character $", ".*X$Y", null);
        verifyEndsWith("too many .", ".*X.Y.java", null);

        // These patterns can be optimized to use String.endsWith
        verifyEndsWith("unescaped .", ".*X.java", "X.java");
        verifyEndsWith("escaped .", ".*X\\.java", "X.java");
        verifyEndsWith("no extension", ".*X", "X");
        verifyEndsWith("begin anchor, unescaped .", "^.*X.java", "X.java");
        verifyEndsWith("begin anchor, escaped .", "^.*X\\.java", "X.java");
        verifyEndsWith("begin anchor, no extension", "^.*X", "X");
        verifyEndsWith("end anchor, unescaped .", ".*X.java$", "X.java");
        verifyEndsWith("end anchor, escaped .", ".*X\\.java$", "X.java");
        verifyEndsWith("end anchor, no extension", ".*X$", "X");
        verifyEndsWith("begin and end anchors, unescaped .", "^.*X.java$", "X.java");
        verifyEndsWith("begin and end anchors, escaped .", "^.*X\\.java$", "X.java");
        verifyEndsWith("begin and end anchors, no extension", "^.*X$", "X");
    }

    private static void verifyFilterTrue(final String message, final String pattern, final String file) {
        assertTrue("filter: " + message, new RegexStringFilter(pattern).filter(file));
    }

    private static void verifyFilterFalse(final String message, final String pattern, final String file) {
        assertFalse("filter: " + message, new RegexStringFilter(pattern).filter(file));
    }

    private static void verifyEndsWith(final String message, final String pattern, final String endsWith) {
        assertEquals("endsWith: " + message, endsWith, new RegexStringFilter(pattern).getEndsWith());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(RegexStringFilterTest.class);
    }
}
