/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.cpd.SourceCode.StringCodeLoader;

public class MarkTest {

    @Test
    public void testSimple() {
        String filename = "/var/Foo.java";
        int beginLine = 1;
        TokenEntry token = new TokenEntry("public", "/var/Foo.java", 1);

        Mark mark = new Mark(token);
        int lineCount = 10;
        mark.setLineCount(lineCount);
        String codeFragment = "code fragment";
        mark.setSourceCode(new SourceCode(new StringCodeLoader(codeFragment)));

        assertEquals(token, mark.getToken());
        assertEquals(filename, mark.getFilename());
        assertEquals(beginLine, mark.getBeginLine());
        assertEquals(lineCount, mark.getLineCount());
        assertEquals(beginLine + lineCount - 1, mark.getEndLine());
        assertEquals(-1, mark.getBeginColumn());
        assertEquals(-1, mark.getEndColumn());
        assertEquals(codeFragment, mark.getSourceCodeSlice());
    }

    @Test
    public void testColumns() {
        final String filename = "/var/Foo.java";
        final int beginLine = 1;
        final int beginColumn = 2;
        final int endColumn = 3;
        final TokenEntry token = new TokenEntry("public", "/var/Foo.java", 1, beginColumn, 0);
        final TokenEntry endToken = new TokenEntry("}", "/var/Foo.java", 5, 0, endColumn);

        final Mark mark = new Mark(token);
        final int lineCount = 10;
        mark.setLineCount(lineCount);
        mark.setEndToken(endToken);
        final String codeFragment = "code fragment";
        mark.setSourceCode(new SourceCode(new StringCodeLoader(codeFragment)));

        assertEquals(token, mark.getToken());
        assertEquals(filename, mark.getFilename());
        assertEquals(beginLine, mark.getBeginLine());
        assertEquals(lineCount, mark.getLineCount());
        assertEquals(beginLine + lineCount - 1, mark.getEndLine());
        assertEquals(beginColumn, mark.getBeginColumn());
        assertEquals(endColumn, mark.getEndColumn());
        assertEquals(codeFragment, mark.getSourceCodeSlice());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MarkTest.class);
    }
}
