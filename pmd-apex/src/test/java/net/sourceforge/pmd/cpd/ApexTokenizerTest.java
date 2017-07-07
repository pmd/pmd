/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.SourceCode.StringCodeLoader;

public class ApexTokenizerTest {

    @Test
    public void testTokenize() throws IOException {
        Tokens tokens = tokenize(load("Simple.cls"));
        if (tokens.size() != 28) {
            printTokens(tokens);
        }
        assertEquals(28, tokens.size());
        assertEquals("someparam", findTokensByLine(8, tokens).get(0).toString());
    }

    @Test
    public void testTokenizeCaseSensitive() throws IOException {
        Tokens tokens = tokenize(load("Simple.cls"), true);
        if (tokens.size() != 28) {
            printTokens(tokens);
        }
        assertEquals(28, tokens.size());
        assertEquals("someParam", findTokensByLine(8, tokens).get(0).toString());
    }

    /**
     * Comments are ignored since using ApexLexer.
     */
    @Test
    public void testTokenizeWithComments() throws IOException {
        Tokens tokens = tokenize(load("issue427/SFDCEncoder.cls"));
        assertEquals(17, tokens.size());

        Tokens tokens2 = tokenize(load("issue427/SFDCEncoderConstants.cls"));
        assertEquals(17, tokens2.size());
    }

    private List<TokenEntry> findTokensByLine(int line, Tokens tokens) {
        List<TokenEntry> result = new ArrayList<>();
        for (TokenEntry entry : tokens.getTokens()) {
            if (entry.getBeginLine() == line) {
                result.add(entry);
            }
        }
        if (result.isEmpty()) {
            fail("Not tokens found at line " + line);
        }
        return result;
    }

    private Tokens tokenize(String code) {
        return tokenize(code, false);
    }

    private Tokens tokenize(String code, boolean caseSensitive) {
        ApexTokenizer tokenizer = new ApexTokenizer();
        Properties properties = new Properties();
        properties.setProperty(ApexTokenizer.CASE_SENSITIVE, Boolean.toString(caseSensitive));
        tokenizer.setProperties(properties);
        Tokens tokens = new Tokens();
        tokenizer.tokenize(new SourceCode(new StringCodeLoader(code)), tokens);
        return tokens;
    }

    private void printTokens(Tokens tokens) {
        for (TokenEntry entry : tokens.getTokens()) {
            System.out.printf("%02d: %s%s", entry.getBeginLine(), entry.toString(), PMD.EOL);
        }
    }

    private String load(String name) throws IOException {
        return IOUtils.toString(ApexTokenizerTest.class.getResourceAsStream(name), StandardCharsets.UTF_8);
    }
}
