/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

public class CPPTokenizerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testUTFwithBOM() {
        Tokens tokens = parse("\ufeffint start()\n{ int ret = 1;\nreturn ret;\n}\n");
        assertNotSame(TokenEntry.getEOF(), tokens.getTokens().get(0));
        assertEquals(15, tokens.size());
    }

    @Test
    public void testUnicodeSupport() {
        String code = "\ufeff" + "#include <iostream>\n" + "#include <string>\n" + "\n" + "// example\n" + "\n"
                + "int main()\n" + "{\n" + "    std::string text(\"ąęćśźńó\");\n" + "    std::cout << text;\n"
                + "    return 0;\n" + "}\n";
        Tokens tokens = parse(code);
        assertNotSame(TokenEntry.getEOF(), tokens.getTokens().get(0));
        assertEquals(24, tokens.size());
    }

    @Test
    public void testIgnoreBetweenSpecialComments() {
        String code = "#include <iostream>\n" + "#include <string>\n" + "\n" + "// CPD-OFF\n"
                + "int main()\n" + "{\n" + "    std::string text(\"ąęćśźńó\");\n" + "    std::cout << text;\n"
                + "    return 0;\n" + "// CPD-ON\n" + "}\n";
        Tokens tokens = parse(code);
        assertNotSame(TokenEntry.getEOF(), tokens.getTokens().get(0));
        assertEquals(2, tokens.size()); // "}" + EOF
    }

    @Test
    public void testMultiLineMacros() {
        Tokens tokens = parse(TEST1);
        assertEquals(7, tokens.size());
    }

    @Test
    public void testDollarSignInIdentifier() {
        parse(TEST2);
    }

    @Test
    public void testDollarSignStartingIdentifier() {
        parse(TEST3);
    }

    @Test
    public void testWideCharacters() {
        parse(TEST4);
    }

    @Test
    public void testTokenizerWithSkipBlocks() throws Exception {
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/cpp_with_asm.cpp"), StandardCharsets.UTF_8);
        Tokens tokens = parse(test, true, new Tokens());
        assertEquals(19, tokens.size());
    }

    @Test
    public void testTokenizerWithSkipBlocksPattern() throws Exception {
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/cpp_with_asm.cpp"), StandardCharsets.UTF_8);
        Tokens tokens = new Tokens();
        try {
            parse(test, true, "#if debug|#endif", tokens);
        } catch (TokenMgrError ignored) {
            // ignored
        }
        assertEquals(31, tokens.size());
    }

    @Test
    public void testTokenizerWithoutSkipBlocks() throws Exception {
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/cpp_with_asm.cpp"), StandardCharsets.UTF_8);
        Tokens tokens = new Tokens();
        try {
            parse(test, false, tokens);
        } catch (TokenMgrError ignored) {
            // ignored
        }
        assertEquals(37, tokens.size());
    }

    @Test
    // ASM code containing the '@' character
    public void testAsmWithAtSign() {
        Tokens tokens = parse(TEST7);
        assertEquals(22, tokens.size());
    }

    @Test
    public void testEOLCommentInPreprocessingDirective() {
        parse("#define LSTFVLES_CPP  //*" + PMD.EOL);
    }

    @Test
    public void testEmptyCharacter() {
        Tokens tokens = parse("std::wstring wsMessage( sMessage.length(), L'');" + PMD.EOL);
        assertEquals(15, tokens.size());
    }

    @Test
    public void testHexCharacter() {
        Tokens tokens = parse("if (*pbuf == '\\0x05')" + PMD.EOL);
        assertEquals(8, tokens.size());
    }

    @Test
    public void testWhiteSpaceEscape() {
        Tokens tokens = parse("szPath = m_sdcacheDir + _T(\"\\    oMedia\");" + PMD.EOL);
        assertEquals(10, tokens.size());
    }

    @Test
    public void testRawStringLiteral() {
        String code = "const char* const KDefaultConfig = R\"(\n" + "    [Sinks.1]\n" + "    Destination=Console\n"
                + "    AutoFlush=true\n"
                + "    Format=\"[%TimeStamp%] %ThreadId% %QueryIdHigh% %QueryIdLow% %LoggerFile%:%Line% (%Severity%) - %Message%\"\n"
                + "    Filter=\"%Severity% >= WRN\"\n" + ")\";\n";
        Tokens tokens = parse(code);
        assertTrue(TokenEntry.getEOF() != tokens.getTokens().get(0));
        assertEquals(9, tokens.size());
    }

    @Test
    public void testLexicalErrorFilename() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS, Boolean.toString(false));
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/issue-1559.cpp"), StandardCharsets.UTF_8);
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(test, "issue-1559.cpp"));
        CPPTokenizer tokenizer = new CPPTokenizer();
        tokenizer.setProperties(properties);

        expectedException.expect(TokenMgrError.class);
        expectedException.expectMessage("Lexical error in file issue-1559.cpp at");
        tokenizer.tokenize(code, new Tokens());
    }

    public void testStringPrefix(String code, String expToken, int tokenIndex, int expNoTokens) {
        final Tokens tokens = parse(code);
        final TokenEntry token = tokens.getTokens().get(tokenIndex);
        assertEquals(expNoTokens, tokens.size());
        assertEquals(expToken, token.toString());
    }

    public void testCharacterPrefix(String code, String expToken) {
        testStringPrefix(code, expToken, 3, 6);
    }

    public void testStringPrefix(String code, String expToken) {
        testStringPrefix(code, expToken, 5, 8);
    }

    @Test
    public void testCharacterPrefixNoPrefix() {
        testCharacterPrefix("char a =  '\\x30';", "'\\x30'");
    }

    @Test
    public void testCharacterPrefixWideCharacter() {
        testCharacterPrefix("wchar_t b = L'\\xFFEF';", "L'\\xFFEF'");
    }

    @Test
    public void testCharacterPrefixChar16() {
        testCharacterPrefix("char16_t c = u'\\u00F6';", "u'\\u00F6'");
    }

    @Test
    public void testCharacterPrefixChar32() {
        testCharacterPrefix("char32_t d = U'\\U0010FFFF';", "U'\\U0010FFFF'");
    }

    @Test
    public void testStringPrefixNoPrefix() {
        testStringPrefix("char A[] = \"Hello\\x0A\";", "\"Hello\\x0A\"");
    }

    @Test
    public void testStringPrefixWideString() {
        testStringPrefix("wchar_t B[] = L\"Hell\\xF6\\x0A\";", "L\"Hell\\xF6\\x0A\"");
    }

    @Test
    public void testStringPrefixChar16() {
        testStringPrefix("char16_t C[] = u\"Hell\\u00F6\";", "u\"Hell\\u00F6\"");
    }

    @Test
    public void testStringPrefixChar32() {
        testStringPrefix("char32_t D[] = U\"Hell\\U000000F6\\U0010FFFF\";", "U\"Hell\\U000000F6\\U0010FFFF\"");
    }

    @Test
    public void testStringPrefixUtf8() {
        testStringPrefix("auto E[] = u8\"\\u00F6\\U0010FFFF\";", "u8\"\\u00F6\\U0010FFFF\"");
    }

    @Test
    public void testRawStringLiterals() throws IOException {
        final String code = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/issue-1784.cpp"), StandardCharsets.UTF_8);
        Tokens tokens = parse(code);
        assertTrue(TokenEntry.getEOF() != tokens.getTokens().get(0));
        assertEquals(16, tokens.size());
    }

    @Test
    public void testDigitSeparators() {
        final String code = "auto integer_literal = 1'000'000;" + PMD.EOL
                + "auto floating_point_literal = 0.000'015'3;" + PMD.EOL
                + "auto hex_literal = 0x0F00'abcd'6f3d;" + PMD.EOL
                + "auto silly_example = 1'0'0'000'00;";
        Tokens tokens = parse(code);
        assertTrue(TokenEntry.getEOF() != tokens.getTokens().get(0));
        assertEquals("1'000'000", tokens.getTokens().get(3).toString());
        assertEquals(21, tokens.size());
    }

    private Tokens parse(String snippet) {
        try {
            return parse(snippet, false, new Tokens());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Tokens parse(String snippet, boolean skipBlocks, Tokens tokens) throws IOException {
        return parse(snippet, skipBlocks, null, tokens);
    }

    private Tokens parse(String snippet, boolean skipBlocks, String skipPattern, Tokens tokens) throws IOException {
        Properties properties = new Properties();
        properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS, Boolean.toString(skipBlocks));
        if (skipPattern != null) {
            properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS_PATTERN, skipPattern);
        }

        CPPTokenizer tokenizer = new CPPTokenizer();
        tokenizer.setProperties(properties);

        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(snippet));
        tokenizer.tokenize(code, tokens);
        return tokens;
    }

    private static final String TEST1 = "#define FOO a +\\" + PMD.EOL + "            b +\\" + PMD.EOL
            + "            c +\\" + PMD.EOL + "            d +\\" + PMD.EOL + "            e +\\" + PMD.EOL
            + "            f +\\" + PMD.EOL + "            g" + PMD.EOL + " void main() {}";

    private static final String TEST2 = " void main() { int x$y = 42; }";

    private static final String TEST3 = " void main() { int $x = 42; }";

    private static final String TEST4 = " void main() { char x = L'a'; }";

    private static final String TEST7 = "asm void eSPI_boot()" + PMD.EOL + "{" + PMD.EOL + "  // setup stack pointer"
            + PMD.EOL + "  lis r1, _stack_addr@h" + PMD.EOL + "  ori r1, r1, _stack_addr@l" + PMD.EOL + "}";
}
