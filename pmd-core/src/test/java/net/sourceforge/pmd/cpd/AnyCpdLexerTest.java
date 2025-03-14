/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;

class AnyCpdLexerTest {

    @Test
    void testMultiLineMacros() throws IOException {
        AnyCpdLexer tokenizer = new AnyCpdLexer("//");
        compareResult(tokenizer, TEST1, EXPECTED);
    }

    @Test
    void testStringEscape() throws IOException {
        AnyCpdLexer tokenizer = new AnyCpdLexer("//");
        compareResult(tokenizer, "a = \"oo\\n\"", listOf("a", "=", "\"oo\\n\"", "EOF"));
    }

    @Test
    void testMultilineString() throws IOException {
        AnyCpdLexer tokenizer = new AnyCpdLexer("//");
        Tokens tokens = compareResult(tokenizer, "a = \"oo\n\";", listOf("a", "=", "\"oo\n\"", ";", "EOF"));
        TokenEntry string = tokens.getTokens().get(2);
        assertEquals("\"oo\n\"", string.getImage(tokens));
        assertEquals(1, string.getBeginLine());
        assertEquals(5, string.getBeginColumn());
        assertEquals(2, string.getEndColumn()); // ends on line 2

        TokenEntry semi = tokens.getTokens().get(3);
        assertEquals(";", semi.getImage(tokens));
        assertEquals(2, semi.getBeginLine());
        assertEquals(2, semi.getBeginColumn());
        assertEquals(3, semi.getEndColumn());
    }

    /**
     * Tests that [core][cpd] AnyTokenizer doesn't count columns correctly #2760 is actually fixed.
     */
    @Test
    void testTokenPosition() throws IOException {
        AnyCpdLexer tokenizer = new AnyCpdLexer();
        TextDocument code = TextDocument.readOnlyString("a;\nbbbb\n;", FileId.UNKNOWN, DummyLanguageModule.getInstance().getDefaultVersion());
        Tokens tokens = new Tokens();
        CpdLexer.tokenize(tokenizer, code, tokens);
        TokenEntry bbbbToken = tokens.getTokens().get(2);
        assertEquals(2, bbbbToken.getBeginLine());
        assertEquals(1, bbbbToken.getBeginColumn());
        assertEquals(5, bbbbToken.getEndColumn());
    }


    private Tokens compareResult(AnyCpdLexer tokenizer, String source, List<String> expectedImages) throws IOException {
        TextDocument code = TextDocument.readOnlyString(source, FileId.UNKNOWN, DummyLanguageModule.getInstance().getDefaultVersion());
        Tokens tokens = new Tokens();
        CpdLexer.tokenize(tokenizer, code, tokens);

        List<String> tokenStrings = new ArrayList<>();
        for (TokenEntry token : tokens.getTokens()) {
            tokenStrings.add(token.getImage(tokens));
        }

        assertEquals(expectedImages, tokenStrings);
        return tokens;
    }

    private String getTokenImage(TokenEntry t) {
        return t.toString();
    }

    private static final List<String> EXPECTED = listOf(
        "using", "System", ";",
        "namespace", "HelloNameSpace", "{",
        "public", "class", "HelloWorld", "{", // note: comment is excluded
        "static", "void", "Main", "(", "string", "[", "]", "args", ")", "{",
        "Console", ".", "WriteLine", "(", "\"Hello World!\"", ")", ";",
        "}", "}", "}", "EOF"
    );

    private static final String TEST1 =
        "using System;\n"
            + "namespace HelloNameSpace {\n"
            + "\n"
            + "    public class HelloWorld { // A comment\n"
            + "        static void Main(string[] args) {\n"
            + "\n"
            + "            Console.WriteLine(\"Hello World!\");\n"
            + "        }\n"
            + "    }\n"
            + "\n"
            + "}\n";

}
