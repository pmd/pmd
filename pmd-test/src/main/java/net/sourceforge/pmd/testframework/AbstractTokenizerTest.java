/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

/**
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public abstract class AbstractTokenizerTest {

    protected int expectedTokenCount;
    protected Tokenizer tokenizer;
    protected SourceCode sourceCode;

    public abstract void buildTokenizer() throws IOException;

    public abstract String getSampleCode() throws IOException;

    protected void tokenizeTest() throws IOException {
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        List<TokenEntry> entries = tokens.getTokens();
        assertEquals(expectedTokenCount, entries.size());
    }

}
