/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.ASTStringLiteral.determineTextBlockContent;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.util.StringUtil;

public class TextBlockEscapeTest extends BaseParserTest {

    @Test
    public void testStringEscapes() {
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
    public void testStringOctalEscapes() {
        testStringEscape("\\0123", "\0123");
        testStringEscape("\\01ab", "\01ab");
        testStringEscape("\\0", "\0");
    }

    @Test
    public void testStringUnknownEscape() {
        testStringEscape("\\x", "\\x");
    }

    // note the argument order
    private void testStringEscape(String actual, String expected) {
        actual = StringUtil.inDoubleQuotes(actual);
        assertEquals(expected,
                     ASTStringLiteral.determineStringContent(Chars.wrap(actual)));
    }

    @Test
    public void testTextBlockContent() {
        assertEquals("single line text block", "winter",
                     determineTextBlockContent("\"\"\"\n                winter\"\"\""));
        assertEquals("single line text block with LF", "winter\n",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "   winter\n"
                                                   + "   \"\"\""));
        assertEquals("single line text block with LF, some indent preserved", " winter\n",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "   winter\n"
                                                   + "  \"\"\""));
    }

    @Test
    public void emptyTextBlock() {
        assertEquals("empty text block", "",
                     determineTextBlockContent("\"\"\"\n                       \"\"\""));
    }

    @Test
    public void testEscapeBlockDelimiter() {
        assertEquals("escaped text block in inside text block",
                     "String text = \"\"\"\n"
                         + "    A text block inside a text block\n"
                         + "\"\"\";\n",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "  String text = \\\"\"\"\n"
                                                   + "      A text block inside a text block\n"
                                                   + "  \\\"\"\";\n"
                                                   + "  \"\"\""));
    }

    @Test
    public void testCrEscape() {
        assertEquals("text block with escapes",
                     "<html>\r\n"
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
                                                   + "                      \"\"\""));
    }

    @Test
    public void testBasicHtmlBlock() {
        assertEquals("basic text block example with html",
                     "<html>\n"
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
                                                   + "                      \"\"\""));
    }

    @Test
    public void testLineContinuation() {
        assertEquals("new escape: line continuation",
                     "Lorem ipsum dolor sit amet, consectetur adipiscing "
                         + "elit, sed do eiusmod tempor incididunt ut labore "
                         + "et dolore magna aliqua.",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "  Lorem ipsum dolor sit amet, consectetur adipiscing \\\n"
                                                   + "  elit, sed do eiusmod tempor incididunt ut labore \\\n"
                                                   + "  et dolore magna aliqua.\\\n"
                                                   + "  \"\"\""));
    }

    @Test
    public void testEscapeSpace() {

        assertEquals("new escape: space escape",
                     "red   \n"
                         + "green \n"
                         + "blue  \n",
                     determineTextBlockContent("\"\"\"\n"
                                                   + "                        red  \\s\n"
                                                   + "                        green\\s\n"
                                                   + "                        blue \\s\n"
                                                   + "                        \"\"\""));

    }

    @Test
    public void testLineEndingNormalization() {
        assertEquals("with crlf line endings",
                     "<html>\n"
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
                                                   + "                      \"\"\""));
        assertEquals("with cr line endings",
                     "<html>\n"
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
                                                   + "                      \"\"\""));

    }

    @Test
    public void testEmptyLines() {

        assertEquals("empty line directly after opening",
                     "\ntest\n", determineTextBlockContent("\"\"\"\n"
                                                               + "    \n"
                                                               + "    test\n"
                                                               + "    \"\"\""));
        assertEquals("empty crlf line directly after opening",
                     "\ntest\n", determineTextBlockContent("\"\"\"\r\n"
                                                               + "    \r\n"
                                                               + "    test\r\n"
                                                               + "    \"\"\""));
        assertEquals("empty line directly after opening without indentation",
                     "\ntest\n", determineTextBlockContent("\"\"\"\n"
                                                               + "\n"
                                                               + "test\n"
                                                               + "\"\"\""));
        assertEquals("empty crlf line directly after opening without indentation",
                     "\ntest\n", determineTextBlockContent("\"\"\"\r\n"
                                                               + "\r\n"
                                                               + "test\r\n"
                                                               + "\"\"\""));

    }

    @Test
    public void testTextBlockWithBackslashEscape() {
        assertEquals("text block with backslash escape", "\\test\n",
                     determineTextBlockContent("\"\"\"\n                \\\\test\n                \"\"\""));
    }

}
