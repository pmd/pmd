/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ASTLiteralTest extends BaseParserTest {

    @Test
    public void testIsStringLiteral() {
        List<ASTLiteral> literals = java.getNodes(ASTLiteral.class, TEST1);
        assertTrue(literals.get(0).isStringLiteral());
    }

    @Test
    public void testIsNotStringLiteral() {
        List<ASTLiteral> literals = java.getNodes(ASTLiteral.class, TEST2);
        assertFalse(literals.get(0).isStringLiteral());
    }

    @Test
    public void testIsIntIntLiteral() {
        List<ASTLiteral> literals = java.getNodes(ASTLiteral.class, TEST3);
        assertTrue(literals.get(0).isIntLiteral());
    }

    @Test
    public void testIsIntLongLiteral() {
        List<ASTLiteral> literals = java.getNodes(ASTLiteral.class, TEST4);
        assertTrue(literals.get(0).isLongLiteral());
    }

    @Test
    public void testIsFloatFloatLiteral() {
        List<ASTLiteral> literals = java.getNodes(ASTLiteral.class, TEST5);
        assertTrue(literals.get(0).isFloatLiteral());
    }

    @Test
    public void testIsFloatDoubleLiteral() {
        List<ASTLiteral> literals = java.getNodes(ASTLiteral.class, TEST6);
        assertTrue(literals.get(0).isDoubleLiteral());
    }

    @Test
    public void testIsCharLiteral() {
        List<ASTLiteral> literals = java.getNodes(ASTLiteral.class, TEST7);
        assertTrue(literals.get(0).isCharLiteral());
    }

    @Test
    public void testIntValueParsing() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("1___234");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(1___234, literal.getValueAsInt());
    }

    @Test
    public void testIntValueParsingBinary() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("0b0000_0010");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(0b0000_0010, literal.getValueAsInt());
    }

    @Test
    public void testIntValueParsingNegativeHexa() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("-0X0000_000f");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(-0X0000_000f, literal.getValueAsInt());
    }

    @Test
    public void testFloatValueParsingNegative() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setFloatLiteral();
        literal.setImage("-3_456.123_456");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(-3_456.123_456f, literal.getValueAsFloat(), 0);
    }

    @Test
    public void testStringUnicodeEscapesNotEscaped() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setStringLiteral();
        literal.setImage("abcüabc");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals("abcüabc", literal.getEscapedStringLiteral());
        assertEquals("abcüabc", literal.getImage());
    }

    @Test
    public void testStringUnicodeEscapesInvalid() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setStringLiteral();
        literal.setImage("abc\\uXYZAabc");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(12);
        assertEquals("abc\\uXYZAabc", literal.getEscapedStringLiteral());
        assertEquals("abc\\uXYZAabc", literal.getImage());
    }

    @Test
    public void testStringUnicodeEscapesValid() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setStringLiteral();
        literal.setImage("abc\u1234abc");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(12);
        assertEquals("abc\\u1234abc", literal.getEscapedStringLiteral());
        assertEquals("abcሴabc", literal.getImage());
    }

    @Test
    public void testCharacterUnicodeEscapesValid() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setCharLiteral();
        literal.setImage("\u0030");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(6);
        assertEquals("\\u0030", literal.getEscapedStringLiteral());
        assertEquals("0", literal.getImage());
    }

    private static final String TEST1 = "public class Foo {\n  String x = \"foo\";\n}";

    private static final String TEST2 = "public class Foo {\n  int x = 42;\n}";

    private static final String TEST3 = "public class Foo {\n  int x = 42;\n}";

    private static final String TEST4 = "public class Foo {\n  long x = 42L;\n}";

    private static final String TEST5 = "public class Foo {\n  float x = 3.14159f;\n}";

    private static final String TEST6 = "public class Foo {\n  double x = 3.14159;\n}";

    private static final String TEST7 = "public class Foo {\n  char x = 'x';\n}";

    @Test
    public void testTextBlockContent() {
        assertEquals("empty text block", "",
                ASTLiteral.determineTextBlockContent("\"\"\"\n                       \"\"\""));
        assertEquals("single line text block", "winter",
                ASTLiteral.determineTextBlockContent("\"\"\"\n                winter\"\"\""));
        assertEquals("single line text block with LF", "winter\n",
                ASTLiteral.determineTextBlockContent("\"\"\"\n"
                                                   + "                        winter\n"
                                                   + "                        \"\"\""));
        assertEquals("basic text block example with html",
                  "<html>\n"
                + "    <body>\n"
                + "        <p>Hello, world</p>\n"
                + "    </body>\n"
                + "</html>\n",
                ASTLiteral.determineTextBlockContent("\"\"\"\n"
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
                ASTLiteral.determineTextBlockContent("\"\"\"\n"
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
                ASTLiteral.determineTextBlockContent("\"\"\"\n"
                                                   + "            String text = \\\"\"\"\n"
                                                   + "                A text block inside a text block\n"
                                                   + "            \\\"\"\";\n"
                                                   + "            \"\"\""));
        assertEquals("new escape: line continuation",
                  "Lorem ipsum dolor sit amet, consectetur adipiscing "
                + "elit, sed do eiusmod tempor incididunt ut labore "
                + "et dolore magna aliqua.",
                ASTLiteral.determineTextBlockContent("\"\"\"\n"
                                    + "                      Lorem ipsum dolor sit amet, consectetur adipiscing \\\n"
                                    + "                      elit, sed do eiusmod tempor incididunt ut labore \\\n"
                                    + "                      et dolore magna aliqua.\\\n"
                                    + "                      \"\"\""));
        assertEquals("new escape: space escape",
                  "red   \n"
                + "green \n"
                + "blue  \n",
                ASTLiteral.determineTextBlockContent("\"\"\"\n"
                        + "                        red  \\s\n"
                        + "                        green\\s\n"
                        + "                        blue \\s\n"
                        + "                        \"\"\""));
        assertEquals("with crlf line endings",
                  "<html>\n"
                + "    <body>\n"
                + "        <p>Hello, world</p>\n"
                + "    </body>\n"
                + "</html>\n", ASTLiteral.determineTextBlockContent("\"\"\"\r\n"
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
                + "</html>\n", ASTLiteral.determineTextBlockContent("\"\"\"\r"
                        + "                      <html>   \r"
                        + "                          <body>\r"
                        + "                              <p>Hello, world</p>    \r"
                        + "                          </body> \r"
                        + "                      </html>   \r"
                        + "                      \"\"\""));
        assertEquals("empty line directly after opening",
                "\ntest\n", ASTLiteral.determineTextBlockContent("\"\"\"\n"
                        + "    \n"
                        + "    test\n"
                        + "    \"\"\""));
        assertEquals("empty crlf line directly after opening",
                "\ntest\n", ASTLiteral.determineTextBlockContent("\"\"\"\r\n"
                        + "    \r\n"
                        + "    test\r\n"
                        + "    \"\"\""));
        assertEquals("empty line directly after opening without indentation",
                "\ntest\n", ASTLiteral.determineTextBlockContent("\"\"\"\n"
                        + "\n"
                        + "test\n"
                        + "\"\"\""));
        assertEquals("empty crlf line directly after opening without indentation",
                "\ntest\n", ASTLiteral.determineTextBlockContent("\"\"\"\r\n"
                        + "\r\n"
                        + "test\r\n"
                        + "\"\"\""));
        assertEquals("text block with backslash escape", "\\test\n",
                ASTLiteral.determineTextBlockContent("\"\"\"\n                \\\\test\n                \"\"\""));
    }
}
