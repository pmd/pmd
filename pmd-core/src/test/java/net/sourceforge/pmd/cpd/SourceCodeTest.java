/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class SourceCodeTest {

    private static final String SAMPLE_CODE =
            "Line 1\n" +
            "Line 2\n" +
            "Line 3\n" +
            "Line 4\n";

    @Test
    public void testSimple() throws Throwable {
        Tokenizer tokenizer = new AbstractTokenizer() {
            {
                this.stringToken = new ArrayList<String>();
                this.ignorableCharacter = new ArrayList<String>();
                this.ignorableStmt = new ArrayList<String>();
            }
        };
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(SAMPLE_CODE, "Foo.java"));
        assertEquals("Foo.java", sourceCode.getFileName());
        tokenizer.tokenize(sourceCode, new Tokens());

        assertEquals("Line 1", sourceCode.getSlice(1, 1));
        assertEquals("Line 2", sourceCode.getSlice(2, 2));
        assertEquals("Line 1\nLine 2", sourceCode.getSlice(1, 2));
    }
}
