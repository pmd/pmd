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
        ASTNumericLiteral literal = new ASTNumericLiteral(1);
        literal.setIntLiteral();
        literal.setImage("1___234");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(1___234, literal.getValueAsInt());
    }
    
    @Test
    public void testIntValueParsingBinary() {
        ASTNumericLiteral literal = new ASTNumericLiteral(1);
        literal.setIntLiteral();
        literal.setImage("0b0000_0010");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(0b0000_0010, literal.getValueAsInt());
    }
    
    @Test
    public void testIntValueParsingNegativeHexa() {
        ASTNumericLiteral literal = new ASTNumericLiteral(1);
        literal.setIntLiteral();
        literal.setImage("-0X0000_000f");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(-0X0000_000f, literal.getValueAsInt());
    }
    
    @Test
    public void testFloatValueParsingNegative() {
        ASTNumericLiteral literal = new ASTNumericLiteral(1);
        literal.setFloatLiteral();
        literal.setImage("-3_456.123_456");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals(-3_456.123_456f, literal.getValueAsFloat(), 0);
    }
    
    @Test
    public void testStringUnicodeEscapesNotEscaped() {
        ASTStringLiteral literal = new ASTStringLiteral(1);
        literal.setImage("abcüabc");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(7);
        assertEquals("abcüabc", literal.getEscapedValue());
        assertEquals("abcüabc", literal.getImage());
    }

    @Test
    public void testStringUnicodeEscapesInvalid() {
        ASTStringLiteral literal = new ASTStringLiteral(1);
        literal.setImage("abc\\uXYZAabc");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(12);
        assertEquals("abc\\uXYZAabc", literal.getEscapedValue());
        assertEquals("abc\\uXYZAabc", literal.getImage());
    }

    @Test
    public void testStringUnicodeEscapesValid() {
        ASTStringLiteral literal = new ASTStringLiteral(1);
        literal.setImage("abc\u1234abc");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(12);
        assertEquals("abc\\u1234abc", literal.getEscapedValue());
        assertEquals("abcሴabc", literal.getImage());
    }

    @Test
    public void testCharacterUnicodeEscapesValid() {
        ASTCharLiteral literal = new ASTCharLiteral(1);
        literal.setImage("\u0030");
        literal.testingOnlySetBeginColumn(1);
        literal.testingOnlySetEndColumn(6);
        assertEquals("\\u0030", literal.getEscapedValue());
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
