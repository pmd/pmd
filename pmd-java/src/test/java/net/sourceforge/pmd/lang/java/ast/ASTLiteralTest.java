package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTst;

import org.junit.Test;

public class ASTLiteralTest extends ParserTst {

    @Test
    public void testIsStringLiteral() throws Throwable {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST1);
        assertTrue((literals.iterator().next()).isStringLiteral());
    }

    @Test
    public void testIsNotStringLiteral() throws Throwable {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST2);
        assertFalse((literals.iterator().next()).isStringLiteral());
    }

    @Test
    public void testIsIntIntLiteral() throws Throwable {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST3);
        assertTrue((literals.iterator().next()).isIntLiteral());
    }

    @Test
    public void testIsIntLongLiteral() throws Throwable {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST4);
        assertTrue((literals.iterator().next()).isLongLiteral());
    }

    @Test
    public void testIsFloatFloatLiteral() throws Throwable {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST5);
        assertTrue((literals.iterator().next()).isFloatLiteral());
    }

    @Test
    public void testIsFloatDoubleLiteral() throws Throwable {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST6);
        assertTrue((literals.iterator().next()).isDoubleLiteral());
    }

    @Test
    public void testIsCharLiteral() throws Throwable {
        Set<ASTLiteral> literals = getNodes(ASTLiteral.class, TEST7);
        assertTrue((literals.iterator().next()).isCharLiteral());
    }

    @Test
    public void testStringUnicodeEscapesNotEscaped() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setStringLiteral();
        literal.setImage("abcüabc");
        literal.testingOnly__setBeginColumn(1);
        literal.testingOnly__setEndColumn(7);
        assertEquals("abcüabc", literal.getEscapedStringLiteral());
        assertEquals("abcüabc", literal.getImage());
    }

    @Test
    public void testStringUnicodeEscapesInvalid() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setStringLiteral();
        literal.setImage("abc\\uXYZAabc");
        literal.testingOnly__setBeginColumn(1);
        literal.testingOnly__setEndColumn(12);
        assertEquals("abc\\uXYZAabc", literal.getEscapedStringLiteral());
        assertEquals("abc\\uXYZAabc", literal.getImage());
    }

    @Test
    public void testStringUnicodeEscapesValid() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setStringLiteral();
        literal.setImage("abc\u1234abc");
        literal.testingOnly__setBeginColumn(1);
        literal.testingOnly__setEndColumn(12);
        assertEquals("abc\\u1234abc", literal.getEscapedStringLiteral());
        assertEquals("abcሴabc", literal.getImage());
    }

    @Test
    public void testCharacterUnicodeEscapesValid() {
        ASTLiteral literal = new ASTLiteral(1);
        literal.setCharLiteral();
        literal.setImage("\u0030");
        literal.testingOnly__setBeginColumn(1);
        literal.testingOnly__setEndColumn(6);
        assertEquals("\\u0030", literal.getEscapedStringLiteral());
        assertEquals("0", literal.getImage());
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "  String x = \"foo\";" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "  int x = 42;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    "  int x = 42;" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    "  long x = 42L;" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    "  float x = 3.14159f;" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    "  double x = 3.14159;" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public class Foo {" + PMD.EOL +
    "  char x = 'x';" + PMD.EOL +
    "}";
}
