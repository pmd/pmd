/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.SourceCode.StringCodeLoader;
import net.sourceforge.pmd.lang.cpp.CppTokenManager;
import net.sourceforge.pmd.lang.cpp.ast.Token;

public class CPPTokenizerContinuationTest {

    @Test
    public void parseWithContinuation() throws Exception {
        String code = load("cpp_with_continuation.cpp");
        Tokens tokens = parse(code);
        if (tokens.size() < 52) {
            printTokens(tokens);
            fail("Not enough tokens - probably parsing error. Tokens: " + tokens.size());
        }

        assertEquals("static", findByLine(8, tokens).get(0).toString());
        assertEquals("int", findByLine(8, tokens).get(1).toString());

        // special case, if the continuation is *within* a token
        // see also test #testContinuationIntraToken
        TokenEntry tokenEntry = findByLine(8, tokens).get(2);
        assertEquals("ab", tokenEntry.toString());

        assertEquals("int", findByLine(12, tokens).get(0).toString());
        assertEquals("main", findByLine(12, tokens).get(1).toString());
        assertEquals("(", findByLine(12, tokens).get(2).toString());
        assertEquals(")", findByLine(12, tokens).get(3).toString());
        assertEquals("{", findByLine(13, tokens).get(0).toString());
        assertEquals("\"world!\\n\"", findByLine(16, tokens).get(0).toString());
        assertEquals("\"3 Hello, \\world!\\n\"", findByLine(22, tokens).get(4).toString());
        assertEquals("}", findByLine(29, tokens).get(0).toString());
    }

    /**
     * Verifies the begin/end of a token. Uses the underlaying JavaCC Token and
     * not TokenEntry.
     */
    @Test
    public void parseWithContinuationCppTokenManager() throws Exception {
        String code = load("cpp_with_continuation.cpp");
        CppTokenManager tokenManager = new CppTokenManager(new StringReader(code));
        List<Token> tokens = new ArrayList<>();

        Token token = (Token) tokenManager.getNextToken();
        while (!token.image.isEmpty()) {
            tokens.add(token);
            token = (Token) tokenManager.getNextToken();
        }

        assertEquals(51, tokens.size());

        assertToken(tokens.get(2), "ab", 8, 12, 9, 1);
        assertToken(tokens.get(22), "\"2 Hello, world!\\n\"", 18, 16, 19, 9);
    }


    private void assertToken(Token token, String image, int beginLine, int beginColumn, int endLine, int endColumn) {
        assertEquals(image, token.image);
        assertEquals(beginLine, token.beginLine);
        assertEquals(beginColumn, token.beginColumn);
        assertEquals(endLine, token.endLine);
        assertEquals(endColumn, token.endColumn);
    }

    @Test
    public void testContinuationIntraToken() throws Exception {
        Tokens tokens = parse(load("cpp_continuation_intra_token.cpp"));
        assertEquals(7, tokens.size());
    }

    @Test
    public void testContinuationInterToken() throws Exception {
        Tokens tokens = parse(load("cpp_continuation_inter_token.cpp"));
        assertEquals(17, tokens.size());
    }

    private void printTokens(Tokens tokens) {
        for (TokenEntry entry : tokens.getTokens()) {
            System.out.printf("%02d: %s%s", entry.getBeginLine(), entry.toString(), PMD.EOL);
        }
    }

    private List<TokenEntry> findByLine(int line, Tokens tokens) {
        List<TokenEntry> result = new ArrayList<>();
        for (TokenEntry entry : tokens.getTokens()) {
            if (entry.getBeginLine() == line) {
                result.add(entry);
            }
        }
        if (result.isEmpty()) {
            fail("No tokens at line " + line + " found");
        }
        return result;
    }

    private String load(String name) throws Exception {
        return IOUtils.toString(CPPTokenizerContinuationTest.class
                .getResourceAsStream("cpp/" + name), StandardCharsets.UTF_8);
    }

    private Tokens parse(String code) throws IOException {
        CPPTokenizer tokenizer = new CPPTokenizer();
        tokenizer.setProperties(new Properties());
        Tokens tokens = new Tokens();
        tokenizer.tokenize(new SourceCode(new StringCodeLoader(code)), tokens);
        return tokens;
    }
}
