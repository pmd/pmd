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

public class AnyTokenizerTest {

    @Test
    public void testMultiLineMacros() {
        AnyTokenizer tokenizer = new AnyTokenizer("//");
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(TEST1));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(code, tokens);
        assertEquals(31, tokens.size());
        List<String> tokenStrings = tokens.getTokens().stream()
                                          .map(this::getTokenImage)
                                          .collect(Collectors.toList());

        assertEquals(EXPECTED, tokenStrings);
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
