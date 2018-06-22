/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class CommentTest {
    @Test
    public void testMultiLinesInSingleLine() {
        String comment = "/* single line. */";
        String filtered = filter(comment);
        Assert.assertEquals(1, lineCount(filtered));
        Assert.assertEquals("single line.", filtered);
    }

    @Test
    public void testMultiLinesInSingleLineSimple() {
        String comment = "// single line.";
        String filtered = filter(comment);
        Assert.assertEquals(1, lineCount(filtered));
        Assert.assertEquals("single line.", filtered);
    }

    @Test
    public void testMultiLinesInSingleLineFormal() {
        String comment = "/** single line. */";
        String filtered = filter(comment);
        Assert.assertEquals(1, lineCount(filtered));
        Assert.assertEquals("single line.", filtered);
    }

    @Test
    public void testMultiLinesInMultiLine() {
        String comment =
                  "/*\n"
                + " * line 1\n"
                + " * line 2\n"
                + " */\n";
        String filtered = filter(comment);
        Assert.assertEquals(2, lineCount(filtered));
        Assert.assertEquals("line 1" + PMD.EOL + "line 2", filtered);
    }

    @Test
    public void testMultiLinesInMultiLineCrLf() {
        String comment =
                  "/*\r\n"
                + " * line 1\r\n"
                + " * line 2\r\n"
                + " */\r\n";
        String filtered = filter(comment);
        Assert.assertEquals(2, lineCount(filtered));
        Assert.assertEquals("line 1" + PMD.EOL + "line 2", filtered);
    }

    @Test
    public void testMultiLinesInMultiLineFormal() {
        String comment =
                  "/**\n"
                + " * line 1\n"
                + " * line 2\n"
                + " */\n";
        String filtered = filter(comment);
        Assert.assertEquals(2, lineCount(filtered));
        Assert.assertEquals("line 1" + PMD.EOL + "line 2", filtered);
    }

    @Test
    public void testMultiLinesInMultiLineFormalCrLf() {
        String comment =
                  "/**\r\n"
                + " * line 1\r\n"
                + " * line 2\r\n"
                + " */\r\n";
        String filtered = filter(comment);
        Assert.assertEquals(2, lineCount(filtered));
        Assert.assertEquals("line 1" + PMD.EOL + "line 2", filtered);
    }

    @Test
    public void testMultiLinesInMultiLineNoAsteriskEmpty() {
        String comment =
                  "/**\n"
                + " * line 1\n"
                + "line 2\n"
                + "\n"
                + " */\n";
        String filtered = filter(comment);
        Assert.assertEquals(2, lineCount(filtered));
        Assert.assertEquals("line 1" + PMD.EOL + "line 2", filtered);
    }

    private String filter(String comment) {
        Token t = new Token();
        t.image = comment;
        Comment node = new Comment(t) {
        };
        return node.getFilteredComment();
    }

    private int lineCount(String filtered) {
        return StringUtils.countMatches(filtered, PMD.EOL) + 1;
    }
}
