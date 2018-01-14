/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.PMD;

public class ASTLiteralTest {

    @Test
    public void testIsStringLiteral() {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST1);
        assertTrue((literals.iterator().next()).isStringLiteral());
    }

    @Test
    public void testIsNotStringLiteral() {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST2);
        assertFalse((literals.iterator().next()).isStringLiteral());
    }

    @Test
    public void testIsIntIntLiteral() {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST3);
        assertTrue((literals.iterator().next()).isIntLiteral());
    }

    @Test
    public void testIsIntLongLiteral() {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST4);
        assertTrue((literals.iterator().next()).isLongLiteral());
    }

    @Test
    public void testIsFloatFloatLiteral() {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST5);
        assertTrue((literals.iterator().next()).isFloatLiteral());
    }

    @Test
    public void testIsFloatDoubleLiteral() {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST6);
        assertTrue((literals.iterator().next()).isDoubleLiteral());
    }

    @Test
    public void testIsCharLiteral() {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST7);
        assertTrue((literals.iterator().next()).isCharLiteral());
    }

    @Test
    public void testIntValueParsing() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("1___234");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(1234, literal.getValueAsInt());
    }
    
    @Test
    public void testIntValueParsingBinary() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("0b0000_0010");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(2, literal.getValueAsInt());
    }
    
    @Test
    public void testIntValueParsingNegativeHexa() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("-0X0000_000f");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(-15, literal.getValueAsInt());
    }
    
    @Test
    public void testFloatValueParsingNegative() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setIntLiteral();
        literal.setImage("-3_456.123_456");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(-3456.123456f, literal.getValueAsFloat(), 0);
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

    private static final String TEST1 = "public class Foo {" + PMD.EOL + "  String x = \"foo\";" + PMD.EOL + "}";

    private static final String TEST2 = "public class Foo {" + PMD.EOL + "  int x = 42;" + PMD.EOL + "}";

    private static final String TEST3 = "public class Foo {" + PMD.EOL + "  int x = 42;" + PMD.EOL + "}";

    private static final String TEST4 = "public class Foo {" + PMD.EOL + "  long x = 42L;" + PMD.EOL + "}";

    private static final String TEST5 = "public class Foo {" + PMD.EOL + "  float x = 3.14159f;" + PMD.EOL + "}";

    private static final String TEST6 = "public class Foo {" + PMD.EOL + "  double x = 3.14159;" + PMD.EOL + "}";

    private static final String TEST7 = "public class Foo {" + PMD.EOL + "  char x = 'x';" + PMD.EOL + "}";
}
