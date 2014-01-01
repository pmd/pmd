/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;

import org.junit.Test;

public class SourceCodeTest {

    @Test
    public void testSimple() throws Throwable {
        JavaTokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(MatchAlgorithmTest.getSampleCode(), "Foo.java"));
        assertEquals("Foo.java", sourceCode.getFileName());
        tokenizer.tokenize(sourceCode, new Tokens());

        assertEquals(MatchAlgorithmTest.LINE_1, sourceCode.getSlice(1, 1));
        assertEquals(MatchAlgorithmTest.LINE_2, sourceCode.getSlice(2, 2));
        assertEquals(MatchAlgorithmTest.LINE_1 + PMD.EOL + MatchAlgorithmTest.LINE_2, sourceCode.getSlice(1, 2));
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(SourceCodeTest.class);
    }
}
