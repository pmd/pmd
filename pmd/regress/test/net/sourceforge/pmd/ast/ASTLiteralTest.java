package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTLiteral;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

public class ASTLiteralTest extends ParserTst {

    public void testIsStringLiteral() throws Throwable {
        Set literals = getNodes(ASTLiteral.class, TEST1);
        assertTrue(((ASTLiteral)(literals.iterator().next())).isStringLiteral());
    }

    public void testIsNotStringLiteral() throws Throwable {
        Set literals = getNodes(ASTLiteral.class, TEST2);
        assertTrue(!((ASTLiteral)(literals.iterator().next())).isStringLiteral());
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    "  String x = \"foo\";" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    "  int x = 42;" + PMD.EOL +
    "}";

}
