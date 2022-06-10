/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.SourceCode.StringCodeLoader;

class MarkTest {

    @Test
    void testSimple() {
        String filename = "/var/Foo.java";
        int beginLine = 1;
        TokenEntry token = new TokenEntry("public", "/var/Foo.java", 1);

        Mark mark = new Mark(token);
        int lineCount = 10;
        mark.setLineCount(lineCount);
        String codeFragment = "code fragment";
        mark.setSourceCode(new SourceCode(new StringCodeLoader(codeFragment)));

        Assertions.assertEquals(token, mark.getToken());
        Assertions.assertEquals(filename, mark.getFilename());
        Assertions.assertEquals(beginLine, mark.getBeginLine());
        Assertions.assertEquals(lineCount, mark.getLineCount());
        Assertions.assertEquals(beginLine + lineCount - 1, mark.getEndLine());
        Assertions.assertEquals(-1, mark.getBeginColumn());
        Assertions.assertEquals(-1, mark.getEndColumn());
        Assertions.assertEquals(codeFragment, mark.getSourceCodeSlice());
    }

    @Test
    void testColumns() {
        final String filename = "/var/Foo.java";
        final int beginLine = 1;
        final int beginColumn = 2;
        final int endColumn = 3;
        final TokenEntry token = new TokenEntry("public", "/var/Foo.java", 1, beginColumn, beginColumn + "public".length());
        final TokenEntry endToken = new TokenEntry("}", "/var/Foo.java", 5, endColumn - 1, endColumn);

        final Mark mark = new Mark(token);
        final int lineCount = 10;
        mark.setLineCount(lineCount);
        mark.setEndToken(endToken);
        final String codeFragment = "code fragment";
        mark.setSourceCode(new SourceCode(new StringCodeLoader(codeFragment)));

        Assertions.assertEquals(token, mark.getToken());
        Assertions.assertEquals(filename, mark.getFilename());
        Assertions.assertEquals(beginLine, mark.getBeginLine());
        Assertions.assertEquals(lineCount, mark.getLineCount());
        Assertions.assertEquals(beginLine + lineCount - 1, mark.getEndLine());
        Assertions.assertEquals(beginColumn, mark.getBeginColumn());
        Assertions.assertEquals(endColumn, mark.getEndColumn());
        Assertions.assertEquals(codeFragment, mark.getSourceCodeSlice());
    }
}
