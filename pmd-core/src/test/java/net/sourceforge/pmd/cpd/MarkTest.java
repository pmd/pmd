/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;

class MarkTest {

    @Test
    void testSimple() {
        final FileId filename = CpdTestUtils.FOO_FILE_ID;
        Tokens tokens = new Tokens();
        TokenEntry token = tokens.addToken("public", filename, 1, 2, 3, 4);

        Mark mark = new Mark(token);
        FileLocation loc = mark.getLocation();
        assertEquals(token, mark.getToken());
        assertEquals(filename, loc.getFileId());
        assertEquals(1, loc.getStartLine());
        assertEquals(3, loc.getLineCount());
        assertEquals(3, loc.getEndLine());
        assertEquals(2, loc.getStartColumn());
        assertEquals(4, loc.getEndColumn());
    }

    @Test
    void testColumns() {
        final FileId filename = CpdTestUtils.FOO_FILE_ID;
        Tokens tokens = new Tokens();
        final int beginLine = 1;
        final int beginColumn = 2;
        final int endColumn = 2;
        final int lineCount = 10;
        TokenEntry token = tokens.addToken("public", filename, beginLine, beginColumn, beginLine,
                                           beginColumn + "public".length());
        TokenEntry endToken = tokens.addToken("}", filename,
                                              beginLine + lineCount, 1, beginLine + lineCount - 1, endColumn);

        final Mark mark = new Mark(token);
        mark.setEndToken(endToken);
        FileLocation loc = mark.getLocation();

        assertEquals(token, mark.getToken());
        assertEquals(filename, loc.getFileId());
        assertEquals(beginLine, loc.getStartLine());
        assertEquals(lineCount, loc.getLineCount());
        assertEquals(beginLine + lineCount - 1, loc.getEndLine());
        assertEquals(beginColumn, loc.getStartColumn());
        assertEquals(endColumn, loc.getEndColumn());
    }
}
