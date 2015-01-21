/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
        mark.setSoureCodeSlice(codeFragment);

        assertEquals(token, mark.getToken());
        assertEquals(filename, mark.getFilename());
        assertEquals(beginLine, mark.getBeginLine());
        assertEquals(lineCount, mark.getLineCount());
        assertEquals(beginLine + lineCount - 1, mark.getEndLine());
        assertEquals(codeFragment, mark.getSourceCodeSlice());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MarkTest.class);
    }
}
