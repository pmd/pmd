/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class AnyTokenizerTest {

    @Test
    public void testMultiLineMacros() {
        AnyTokenizer tokenizer = new AnyTokenizer("//");
        compareResult(tokenizer, TEST1, EXPECTED);
    }

    @Test
    public void testStringEscape() {
        AnyTokenizer tokenizer = new AnyTokenizer("//");
        compareResult(tokenizer, "a = \"oo\\n\"", listOf("a", "=", "\"oo\\n\"", "EOF"));
    }

    @Test
    public void testMultilineString() {
        AnyTokenizer tokenizer = new AnyTokenizer("//");
        Tokens tokens = compareResult(tokenizer, "a = \"oo\n\";", listOf("a", "=", "\"oo\n\"", ";", "EOF"));
        TokenEntry string = tokens.getTokens().get(2);
        assertEquals("\"oo\n\"", getTokenImage(string));
        assertEquals(1, string.getBeginLine());
        assertEquals(5, string.getBeginColumn());
        assertEquals(1, string.getEndColumn()); // ends on line 2

        TokenEntry semi = tokens.getTokens().get(3);
        assertEquals(";", getTokenImage(semi));
        assertEquals(2, semi.getBeginLine());
        assertEquals(2, semi.getBeginColumn());
        assertEquals(2, semi.getEndColumn());
    }

    private Tokens compareResult(AnyTokenizer tokenizer, String source, List<String> expectedImages) {
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(source));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(code, tokens);

        List<String> tokenStrings = new ArrayList<>();
        for (TokenEntry token : tokens.getTokens()) {
            tokenStrings.add(getTokenImage(token));
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
