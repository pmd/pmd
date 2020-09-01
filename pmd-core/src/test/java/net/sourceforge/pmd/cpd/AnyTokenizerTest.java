/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;

import net.sourceforge.pmd.internal.util.IteratorUtil;

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
        TokenEntry string = IteratorUtil.getNth(tokens.iterator(), 2);
        assertEquals("\"oo\n\"", getTokenImage(string));
        assertEquals(1, string.getBeginLine());
        assertEquals(5, string.getBeginColumn());
        assertEquals(2, string.getEndColumn()); // ends on line 2

        TokenEntry semi = IteratorUtil.getNth(tokens.iterator(), 3);
        assertEquals(";", getTokenImage(semi));
        assertEquals(2, semi.getBeginLine());
        assertEquals(2, semi.getBeginColumn());
        assertEquals(3, semi.getEndColumn());
    }

    private Tokens compareResult(AnyTokenizer tokenizer, String source, List<String> expectedImages) {
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(source));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(code, tokens);
        List<String> tokenStrings = tokens.getTokens().stream()
                                          .map(this::getTokenImage)
                                          .collect(Collectors.toList());

        assertEquals(expectedImages, tokenStrings);
        return tokens;
    }

    private @NonNull String getTokenImage(TokenEntry t) {
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
