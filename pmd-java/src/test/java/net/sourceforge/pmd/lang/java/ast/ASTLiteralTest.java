package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTst;

import org.junit.Test;

public class ASTLiteralTest extends ParserTst {

    @Test
    public void testIsStringLiteral() throws Throwable {
        Set literals = getNodes(ASTLiteral.class, TEST1);
        assertTrue(((ASTLiteral)(literals.iterator().next())).isStringLiteral());
    }

    @Test
    public void testIsNotStringLiteral() throws Throwable {
        Set literals = getNodes(ASTLiteral.class, TEST2);
        assertFalse(((ASTLiteral)(literals.iterator().next())).isStringLiteral());
    }

    @Test
    public void testIsIntIntLiteral() throws Throwable {
        Set literals = getNodes(ASTLiteral.class, TEST3);
        assertTrue(((ASTLiteral)(literals.iterator().next())).isIntLiteral());
    }

    @Test
    public void testIsIntLongLiteral() throws Throwable {
        Set literals = getNodes(ASTLiteral.class, TEST4);
        assertTrue(((ASTLiteral)(literals.iterator().next())).isLongLiteral());
    }

    @Test
    public void testIsFloatFloatLiteral() throws Throwable {
        Set literals = getNodes(ASTLiteral.class, TEST5);
        assertTrue(((ASTLiteral)(literals.iterator().next())).isFloatLiteral());
    }

    @Test
    public void testIsFloatDoubleLiteral() throws Throwable {
        Set literals = getNodes(ASTLiteral.class, TEST6);
        assertTrue(((ASTLiteral)(literals.iterator().next())).isDoubleLiteral());
    }

    @Test
    public void testIsCharLiteral() throws Throwable {
        Set literals = getNodes(ASTLiteral.class, TEST7);
        assertTrue(((ASTLiteral)(literals.iterator().next())).isCharLiteral());
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTLiteralTest.class);
    }
}
