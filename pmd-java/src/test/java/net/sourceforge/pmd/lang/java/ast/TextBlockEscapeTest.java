/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.ASTStringLiteral.determineTextBlockContent;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.util.StringUtil;

class TextBlockEscapeTest extends BaseParserTest {

    @Test
    void testStringEscapes() {
        testStringEscape("abcd", "abcd");
        testStringEscape("abcd\\f", "abcd\f");
        testStringEscape("abcd\\n", "abcd\n");
        testStringEscape("abcd\\t", "abcd\t");
        testStringEscape("abcd\\bx", "abcd\bx");
        testStringEscape("abcd\\'x", "abcd\'x");
        // note that this one is actually invalid syntax
        testStringEscape("abcd\\", "abcd\\");
        testStringEscape("abcd\\\\", "abcd\\");
    }

    @Test
    void testStringOctalEscapes() {
        testStringEscape("\\0123", "\0123");
        testStringEscape("\\01ab", "\01ab");
        testStringEscape("\\0", "\0");
    }

    @Test
    void testStringUnknownEscape() {
        testStringEscape("\\x", "\\x");
    }

    // note the argument order
    private void testStringEscape(String actual, String expected) {
        actual = StringUtil.inDoubleQuotes(actual);
        assertEquals(expected,
                     ASTStringLiteral.determineStringContent(Chars.wrap(actual)));
    }

    @Test
    void testTextBlockContent() {
        assertEquals("winter", determineTextBlockContent("\"\"\"\n                winter\"\"\""),
                "single line text block");
        assertEquals("winter\n", determineTextBlockContent("\"\"\"\n"
                                                   + "   winter\n"
                                                   + "   \"\"\""),
                "single line text block with LF");
        assertEquals(" winter\n", determineTextBlockContent("\"\"\"\n"
                                                   + "   winter\n"
                                                   + "  \"\"\""),
                "single line text block with LF, some indent preserved");
    }

    @Test
    void emptyTextBlock() {
        assertEquals("",
                     determineTextBlockContent("\"\"\"\n                       \"\"\""),
                "empty text block");
    }

    @Test
    void testEscapeBlockDelimiter() {
        assertEquals("String text = \"\"\"\n"
                         + "    A text block inside a text block\n"
                         + "\"\"\";\n",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "  String text = \\\"\"\"\n"
                                                   + "      A text block inside a text block\n"
                                                   + "  \\\"\"\";\n"
                                                   + "  \"\"\""),
                "escaped text block in inside text block");
    }

    @Test
    void testCrEscape() {
        assertEquals("<html>\r\n"
                         + "    <body>\r\n"
                         + "        <p>Hello, world</p>\r\n"
                         + "    </body>\r\n"
                         + "</html>\r\n",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "                      <html>\\r\n"
                                                   + "                          <body>\\r\n"
                                                   + "                              <p>Hello, world</p>\\r\n"
                                                   + "                          </body>\\r\n"
                                                   + "                      </html>\\r\n"
                                                   + "                      \"\"\""),
                "text block with escapes");
    }

    @Test
    void testBasicHtmlBlock() {
        assertEquals("<html>\n"
                         + "    <body>\n"
                         + "        <p>Hello, world</p>\n"
                         + "    </body>\n"
                         + "</html>\n",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "                      <html>   \n"
                                                   + "                          <body>\n"
                                                   + "                              <p>Hello, world</p>    \n"
                                                   + "                          </body> \n"
                                                   + "                      </html>   \n"
                                                   + "                      \"\"\""),
                "basic text block example with html");
    }

    @Test
    void testLineContinuation() {
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing "
                         + "elit, sed do eiusmod tempor incididunt ut labore "
                         + "et dolore magna aliqua.",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "  Lorem ipsum dolor sit amet, consectetur adipiscing \\\n"
                                                   + "  elit, sed do eiusmod tempor incididunt ut labore \\\n"
                                                   + "  et dolore magna aliqua.\\\n"
                                                   + "  \"\"\""),
                "new escape: line continuation");
    }

    @Test
    void testEscapeSpace() {
        assertEquals("red   \n"
                         + "green \n"
                         + "blue  \n",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "                        red  \\s\n"
                                                   + "                        green\\s\n"
                                                   + "                        blue \\s\n"
                                                   + "                        \"\"\""),
                "new escape: space escape");
    }

    @Test
    void testLineEndingNormalization() {
        assertEquals("<html>\n"
                         + "    <body>\n"
                         + "        <p>Hello, world</p>\n"
                         + "    </body>\n"
                         + "</html>\n",
                     determineTextBlockContent("\"\"\"\r\n"
                                                   + "                      <html>   \r\n"
                                                   + "                          <body>\r\n"
                                                   + "                              <p>Hello, world</p>    \r\n"
                                                   + "                          </body> \r\n"
                                                   + "                      </html>   \r\n"
                                                   + "                      \"\"\""),
                "with crlf line endings");
        assertEquals("<html>\n"
                         + "    <body>\n"
                         + "        <p>Hello, world</p>\n"
                         + "    </body>\n"
                         + "</html>\n",
                     determineTextBlockContent("\"\"\"\r"
                                                   + "                      <html>   \r"
                                                   + "                          <body>\r"
                                                   + "                              <p>Hello, world</p>    \r"
                                                   + "                          </body> \r"
                                                   + "                      </html>   \r"
                                                   + "                      \"\"\""),
                "with cr line endings");
    }

    @Test
    void testEmptyLines() {

        assertEquals("\ntest\n", determineTextBlockContent("\"\"\"\n"
                                                               + "    \n"
                                                               + "    test\n"
                                                               + "    \"\"\""),
                "empty line directly after opening");
        assertEquals("\ntest\n", determineTextBlockContent("\"\"\"\r\n"
                                                               + "    \r\n"
                                                               + "    test\r\n"
                                                               + "    \"\"\""),
                "empty crlf line directly after opening");
        assertEquals("\ntest\n", determineTextBlockContent("\"\"\"\n"
                                                               + "\n"
                                                               + "test\n"
                                                               + "\"\"\""),
                "empty line directly after opening without indentation");
        assertEquals("\ntest\n", determineTextBlockContent("\"\"\"\r\n"
                                                               + "\r\n"
                                                               + "test\r\n"
                                                               + "\"\"\""),
                "empty crlf line directly after opening without indentation");

    }

    @Test
    void testTextBlockWithBackslashEscape() {
        assertEquals("\\test\n",
                     determineTextBlockContent("\"\"\"\n                \\\\test\n                \"\"\""),
                "text block with backslash escape");
    }

}
