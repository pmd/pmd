/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MarkTest {

    @Test
    void testSimple() {
        String filename = "/var/Foo.java";
        Tokens tokens = new Tokens();
        int beginLine = 1;
        TokenEntry token = tokens.addToken("public", filename, beginLine, 2, 3, 4);

        Mark mark = new Mark(token);

        assertEquals(token, mark.getToken());
        assertEquals(filename, mark.getFilename());
        assertEquals(beginLine, mark.getBeginLine());
        assertEquals(1, mark.getLineCount());
        assertEquals(beginLine, mark.getEndLine());
        assertEquals(2, mark.getBeginColumn());
        assertEquals(4, mark.getEndColumn());
    }

    @Test
    void testColumns() {
        final String filename = "/var/Foo.java";
        Tokens tokens = new Tokens();
        final int beginLine = 1;
        final int beginColumn = 2;
        final int endColumn = 2;
        final int lineCount = 10;
        TokenEntry token = tokens.addToken("public", filename, beginLine, beginColumn, beginLine,
                                           beginColumn + "public".length());
        TokenEntry endToken = tokens.addToken("}", filename,
                                              beginLine + lineCount, 1, beginLine + lineCount, endColumn);

        final Mark mark = new Mark(token);
        mark.setEndToken(endToken);

        assertEquals(token, mark.getToken());
        assertEquals(filename, mark.getFilename());
        assertEquals(beginLine, mark.getBeginLine());
        assertEquals(lineCount, mark.getLineCount());
        assertEquals(beginLine + lineCount - 1, mark.getEndLine());
        assertEquals(beginColumn, mark.getBeginColumn());
        assertEquals(endColumn, mark.getEndColumn());
    }
}
