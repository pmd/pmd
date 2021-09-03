/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextBlockEscapeTest extends BaseParserTest {

    @Test
    public void testTextBlockContent() {
        assertEquals("empty text block", "",
                     ASTStringLiteral.determineTextBlockContent("\"\"\"\n                       \"\"\""));
        assertEquals("single line text block", "winter",
                     ASTStringLiteral.determineTextBlockContent("\"\"\"\n                winter\"\"\""));
        assertEquals("single line text block with LF", "winter\n",
                     ASTStringLiteral.determineTextBlockContent("\"\"\"\n"
                                                              + "                        winter\n"
                                                              + "                        \"\"\""));
        assertEquals("basic text block example with html",
                     "<html>\n"
                         + "    <body>\n"
                         + "        <p>Hello, world</p>\n"
                         + "    </body>\n"
                         + "</html>\n",
                     ASTStringLiteral.determineTextBlockContent("\"\"\"\n"
                                                              + "                      <html>   \n"
                                                              + "                          <body>\n"
                                                              + "                              <p>Hello, world</p>    \n"
                                                              + "                          </body> \n"
                                                              + "                      </html>   \n"
                                                              + "                      \"\"\""));
        assertEquals("text block with escapes",
                     "<html>\r\n"
                         + "    <body>\r\n"
                         + "        <p>Hello, world</p>\r\n"
                         + "    </body>\r\n"
                         + "</html>\r\n",
                     ASTStringLiteral.determineTextBlockContent("\"\"\"\n"
                                                              + "                      <html>\\r\n"
                                                              + "                          <body>\\r\n"
                                                              + "                              <p>Hello, world</p>\\r\n"
                                                              + "                          </body>\\r\n"
                                                              + "                      </html>\\r\n"
                                                              + "                      \"\"\""));
        assertEquals("escaped text block in inside text block",
                     "String text = \"\"\"\n"
                         + "    A text block inside a text block\n"
                         + "\"\"\";\n",
                     ASTStringLiteral.determineTextBlockContent("\"\"\"\n"
                                                              + "            String text = \\\"\"\"\n"
                                                              + "                A text block inside a text block\n"
                                                              + "            \\\"\"\";\n"
                                                              + "            \"\"\""));
        assertEquals("new escape: line continuation",
                     "Lorem ipsum dolor sit amet, consectetur adipiscing "
                         + "elit, sed do eiusmod tempor incididunt ut labore "
                         + "et dolore magna aliqua.",
                     ASTStringLiteral.determineTextBlockContent("\"\"\"\n"
                                                              + "                      Lorem ipsum dolor sit amet, consectetur adipiscing \\\n"
                                                              + "                      elit, sed do eiusmod tempor incididunt ut labore \\\n"
                                                              + "                      et dolore magna aliqua.\\\n"
                                                              + "                      \"\"\""));
        assertEquals("new escape: space escape",
                     "red   \n"
                         + "green \n"
                         + "blue  \n",
                     ASTStringLiteral.determineTextBlockContent("\"\"\"\n"
                                                              + "                        red  \\s\n"
                                                              + "                        green\\s\n"
                                                              + "                        blue \\s\n"
                                                              + "                        \"\"\""));
        assertEquals("with crlf line endings",
                     "<html>\n"
                         + "    <body>\n"
                         + "        <p>Hello, world</p>\n"
                         + "    </body>\n"
                         + "</html>\n", ASTStringLiteral.determineTextBlockContent("\"\"\"\r\n"
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
                         + "</html>\n", ASTStringLiteral.determineTextBlockContent("\"\"\"\r"
                                                                                 + "                      <html>   \r"
                                                                                 + "                          <body>\r"
                                                                                 + "                              <p>Hello, world</p>    \r"
                                                                                 + "                          </body> \r"
                                                                                 + "                      </html>   \r"
                                                                                 + "                      \"\"\""));
        assertEquals("empty line directly after opening",
                     "\ntest\n", ASTStringLiteral.determineTextBlockContent("\"\"\"\n"
                                                                          + "    \n"
                                                                          + "    test\n"
                                                                          + "    \"\"\""));
        assertEquals("empty crlf line directly after opening",
                     "\ntest\n", ASTStringLiteral.determineTextBlockContent("\"\"\"\r\n"
                                                                          + "    \r\n"
                                                                          + "    test\r\n"
                                                                          + "    \"\"\""));
        assertEquals("empty line directly after opening without indentation",
                     "\ntest\n", ASTStringLiteral.determineTextBlockContent("\"\"\"\n"
                                                                          + "\n"
                                                                          + "test\n"
                                                                          + "\"\"\""));
        assertEquals("empty crlf line directly after opening without indentation",
                     "\ntest\n", ASTStringLiteral.determineTextBlockContent("\"\"\"\r\n"
                                                                          + "\r\n"
                                                                          + "test\r\n"
                                                                          + "\"\"\""));
        assertEquals("text block with backslash escape", "\\test\n",
                     ASTStringLiteral.determineTextBlockContent("\"\"\"\n                \\\\test\n                \"\"\""));
    }
}
