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
        assertEquals("winter\n", determineTextBlockContent("""
                                                   ""\"
                                                      winter
                                                      ""\"\
                                                   """),
                "single line text block with LF");
        assertEquals(" winter\n", determineTextBlockContent("""
                                                   ""\"
                                                      winter
                                                     ""\"\
                                                   """),
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
        assertEquals("""
                         String text = ""\"
                             A text block inside a text block
                         ""\";
                         """,
                     determineTextBlockContent("""
                                                   ""\"
                                                     String text = \\""\"
                                                         A text block inside a text block
                                                     \\""\";
                                                     ""\"\
                                                   """),
                "escaped text block in inside text block");
    }

    @Test
    void testCrEscape() {
        assertEquals("""
                         <html>
                             <body>
                                 <p>Hello, world</p>
                             </body>
                         </html>
                         """,
                     determineTextBlockContent("""
                                                   ""\"
                                                                         <html>\\r
                                                                             <body>\\r
                                                                                 <p>Hello, world</p>\\r
                                                                             </body>\\r
                                                                         </html>\\r
                                                                         ""\"\
                                                   """),
                "text block with escapes");
    }

    @Test
    void testBasicHtmlBlock() {
        assertEquals("""
                         <html>
                             <body>
                                 <p>Hello, world</p>
                             </body>
                         </html>
                         """,
                     determineTextBlockContent("""
                                                   ""\"
                                                                         <html>  \s
                                                                             <body>
                                                                                 <p>Hello, world</p>   \s
                                                                             </body>\s
                                                                         </html>  \s
                                                                         ""\"\
                                                   """),
                "basic text block example with html");
    }

    @Test
    void testLineContinuation() {
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing "
                         + "elit, sed do eiusmod tempor incididunt ut labore "
                         + "et dolore magna aliqua.",
                     determineTextBlockContent("""
                                                   ""\"
                                                     Lorem ipsum dolor sit amet, consectetur adipiscing \\
                                                     elit, sed do eiusmod tempor incididunt ut labore \\
                                                     et dolore magna aliqua.\\
                                                     ""\"\
                                                   """),
                "new escape: line continuation");
    }

    @Test
    void testEscapeSpace() {
        assertEquals("""
                         red  \s
                         green\s
                         blue \s
                         """,
                     determineTextBlockContent("""
                                                   ""\"
                                                                           red  \\s
                                                                           green\\s
                                                                           blue \\s
                                                                           ""\"\
                                                   """),
                "new escape: space escape");
    }

    @Test
    void testLineEndingNormalization() {
        assertEquals("""
                         <html>
                             <body>
                                 <p>Hello, world</p>
                             </body>
                         </html>
                         """,
                     determineTextBlockContent("""
                                                   ""\"
                                                                         <html>   
                                                                             <body>
                                                                                 <p>Hello, world</p>    
                                                                             </body> 
                                                                         </html>   
                                                                         ""\"\
                                                   """),
                "with crlf line endings");
        assertEquals("""
                         <html>
                             <body>
                                 <p>Hello, world</p>
                             </body>
                         </html>
                         """,
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

        assertEquals("\ntest\n", determineTextBlockContent("""
                                                               ""\"
                                                                  \s
                                                                   test
                                                                   ""\"\
                                                               """),
                "empty line directly after opening");
        assertEquals("\ntest\n", determineTextBlockContent("""
                                                               ""\"
                                                                   
                                                                   test
                                                                   ""\"\
                                                               """),
                "empty crlf line directly after opening");
        assertEquals("\ntest\n", determineTextBlockContent("""
                                                               ""\"
                                                               
                                                               test
                                                               ""\"\
                                                               """),
                "empty line directly after opening without indentation");
        assertEquals("\ntest\n", determineTextBlockContent("""
                                                               ""\"
                                                               
                                                               test
                                                               ""\"\
                                                               """),
                "empty crlf line directly after opening without indentation");

    }

    @Test
    void testTextBlockWithBackslashEscape() {
        assertEquals("\\test\n",
                     determineTextBlockContent("\"\"\"\n                \\\\test\n                \"\"\""),
                "text block with backslash escape");
    }

}
