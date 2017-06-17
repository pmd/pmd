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
        ApexTokenizer tokenizer = new ApexTokenizer();
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
