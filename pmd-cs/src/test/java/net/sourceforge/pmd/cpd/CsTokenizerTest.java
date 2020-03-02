/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.TokenMgrError;

public class CsTokenizerTest {

    private CsTokenizer tokenizer;

    private Tokens tokens;

    @Before
    public void init() {
        tokenizer = new CsTokenizer();
        tokens = new Tokens();
        TokenEntry.clearImages();
    }

    @Test
    public void testSimpleClass() {
        tokenizer.tokenize(toSourceCode("class Foo {}"), tokens);
        assertEquals(5, tokens.size());
    }

    @Test
    public void testSimpleClassDuplicatedTokens() {
        tokenizer.tokenize(toSourceCode("class Foo { class Foo { } }"), tokens);
        assertEquals(9, tokens.size());
        List<TokenEntry> tokenList = tokens.getTokens();
        assertEquals(tokenList.get(0).getIdentifier(), tokenList.get(3).getIdentifier());
        assertEquals(tokenList.get(1).getIdentifier(), tokenList.get(4).getIdentifier());
        assertEquals(tokenList.get(2).getIdentifier(), tokenList.get(5).getIdentifier());
        assertEquals(tokenList.get(6).getIdentifier(), tokenList.get(7).getIdentifier());
    }

    @Test
    public void testSimpleClassMethodMultipleLines() {
        tokenizer.tokenize(toSourceCode("class Foo {\n" + "  public String foo(int a) {\n" + "    int i = a;\n"
                + "    return \"x\" + a;\n" + "  }\n" + "}"), tokens);
        assertEquals(24, tokens.size());
        List<TokenEntry> tokenList = tokens.getTokens();
        assertEquals(1, tokenList.get(0).getBeginLine());
        assertEquals(2, tokenList.get(4).getBeginLine());
        assertEquals(3, tokenList.get(11).getBeginLine());
    }

    @Test
    public void testStrings() {
        tokenizer.tokenize(toSourceCode("String s =\"aaa \\\"b\\n\";"), tokens);
        assertEquals(6, tokens.size());
    }

    @Test(expected = TokenMgrError.class)
    public void testOpenString() {
        tokenizer.tokenize(toSourceCode("String s =\"aaa \\\"b\\"), tokens);
    }

    @Test
    public void testCommentsIgnored1() {
        tokenizer.tokenize(toSourceCode("class Foo { /* class * ** X */ }"), tokens);
        assertEquals(5, tokens.size());
    }

    @Test
    public void testCommentsIgnored2() {
        tokenizer.tokenize(toSourceCode("class Foo { // class X /* aaa */ \n }"), tokens);
        assertEquals(5, tokens.size());
    }

    @Test
    public void testCommentsIgnored3() {
        tokenizer.tokenize(toSourceCode("class Foo { /// class X /* aaa */ \n }"), tokens);
        assertEquals(5, tokens.size());
    }

    @Test
    public void testMoreTokens() {
        tokenizer
                .tokenize(
                        toSourceCode("class Foo {\n" + "  void bar() {\n" + "    int a = 1 >> 2; \n" + "    a += 1; \n"
                                + "    a++; \n" + "    a /= 3e2; \n" + "    float f = -3.1; \n" + "    f *= 2; \n"
                                + "    bool b = ! (f == 2.0 || f >= 1.0 && f <= 2.0) \n" + "  }\n" + "}"),
                        tokens);
        assertEquals(57, tokens.size());
    }

    @Test
    public void testLineNumberAfterMultilineComment() {
        tokenizer
                .tokenize(
                        toSourceCode("/* This is a multiline comment \n" + " * \n" + " * Lorem ipsum dolor sit amet, \n"
                                + " * consectetur adipiscing elit \n" + " */\n" + "\n" + "class Foo {\n" + "\n" + "}"),
                        tokens);
        assertEquals(5, tokens.size());
        assertEquals(7, tokens.getTokens().get(0).getBeginLine());
    }

    @Test
    public void testLineNumberAfterMultilineString() {
        tokenizer.tokenize(toSourceCode(
                "class Foo {\n" + "  void bar() {\n" + "    String query = \n" + "      @\"SELECT foo, bar\n"
                        + "         FROM table \n" + "         WHERE id = 42\"; \n" + "  }\n" + "}"),
                tokens);
        assertEquals(16, tokens.size());
        assertEquals(8, tokens.getTokens().get(14).getBeginLine());
    }

    @Test
    public void testIgnoreUsingDirectives() {
        tokenizer.setIgnoreUsings(true);
        tokenizer.tokenize(toSourceCode("using System.Text;\n"), tokens);
        assertEquals(1, tokens.size());
        assertNotEquals("using", tokens.getTokens().get(0).toString());
        assertEquals(TokenEntry.EOF, tokens.getTokens().get(0));
    }

    @Test
    public void testUsingStatementsAreNotIgnored() {
        tokenizer.setIgnoreUsings(true);
        tokenizer.tokenize(toSourceCode(
                "using (Font font1 = new Font(\"Arial\", 10.0f)) {\n" + "  byte charset = font1.GdiCharSet;\n" + "}\n"),
                tokens);
        assertEquals("using", tokens.getTokens().get(0).toString());
    }

    @Test
    public void testUsingVarStatementsAreNotIgnored() {
        tokenizer.setIgnoreUsings(true);
        tokenizer.tokenize(toSourceCode(
                "using var font1 = new Font(\"Arial\", 10.0f);\n" + "  byte charset = font1.GdiCharSet;\n"),
                tokens);
        assertEquals("using", tokens.getTokens().get(0).toString());
    }

    private SourceCode toSourceCode(String source) {
        return new SourceCode(new SourceCode.StringCodeLoader(source));
    }
}
