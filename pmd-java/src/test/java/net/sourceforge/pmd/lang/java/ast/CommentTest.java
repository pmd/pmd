/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class CommentTest extends BaseParserTest {
    @Test
    void testMultiLinesInSingleLine() {
        String comment = "/* single line. */";
        String filtered = filter(comment);
        assertEquals(1, lineCount(filtered));
        assertEquals("single line.", filtered);
    }

    @Test
    void testMultiLinesInSingleLineSimple() {
        String comment = "// single line.";
        String filtered = filter(comment);
        assertEquals(1, lineCount(filtered));
        assertEquals("single line.", filtered);
    }

    @Test
    void testMultiLinesInSingleLineFormal() {
        String comment = "/** single line. */";
        String filtered = filter(comment);
        assertEquals(1, lineCount(filtered));
        assertEquals("single line.", filtered);
    }

    @Test
    void testMultiLinesInMultiLine() {
        String comment =
                  "/*\n"
                + " * line 1\n"
                + " * line 2\n"
                + " */\n";
        String filtered = filter(comment);
        assertEquals(2, lineCount(filtered));
        assertEquals("line 1\nline 2", filtered);
    }

    @Test
    void testMultiLinesInMultiLineCrLf() {
        String comment =
                  "/*\r\n"
                + " * line 1\r\n"
                + " * line 2\r\n"
                + " */\r\n";
        String filtered = filter(comment);
        assertEquals(2, lineCount(filtered));
        assertEquals("line 1\nline 2", filtered);
    }

    @Test
    void testMultiLinesInMultiLineFormal() {
        String comment =
                  "/**\n"
                + " * line 1\n"
                + " * line 2\n"
                + " */\n";
        String filtered = filter(comment);
        assertEquals(2, lineCount(filtered));
        assertEquals("line 1\nline 2", filtered);
    }

    @Test
    void testMultiLinesInMultiLineFormalCrLf() {
        String comment =
                  "/**\r\n"
                + " * line 1\r\n"
                + " * line 2\r\n"
                + " */\r\n";
        String filtered = filter(comment);
        assertEquals(2, lineCount(filtered));
        assertEquals("line 1\nline 2", filtered);
    }

    @Test
    void testMultiLinesInMultiLineNoAsteriskEmpty() {
        String comment =
                  "/**\n"
                + " * line 1\n"
                + "line 2\n"
                + "\n"
                + " */\n";
        String filtered = filter(comment);
        assertEquals(2, lineCount(filtered));
        assertEquals("line 1\nline 2", filtered);
    }

    private String filter(String comment) {
        JavaComment firstComment = java.parse(comment).getComments().get(0);
        return StringUtils.join(firstComment.getFilteredLines(), '\n');
    }

    private int lineCount(String filtered) {
        return StringUtils.countMatches(filtered, '\n') + 1;
    }
}
