package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTBooleanLiteral;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

public class ASTBooleanLiteralTest extends ParserTst {

    public void testTrue() throws Throwable {
        Set ops = getNodes(ASTBooleanLiteral.class, TEST1);
        ASTBooleanLiteral b = (ASTBooleanLiteral) ops.iterator().next();
        assertTrue(b.isTrue());
    }

    public void testFalse() throws Throwable {
        Set ops = getNodes(ASTBooleanLiteral.class, TEST2);
        ASTBooleanLiteral b = (ASTBooleanLiteral) ops.iterator().next();
        assertTrue(!b.isTrue());
    }

    private static final String TEST1 =
            "class Foo { " + PMD.EOL +
            " boolean bar = true; " + PMD.EOL +
            "} ";

    private static final String TEST2 =
            "class Foo { " + PMD.EOL +
            " boolean bar = false; " + PMD.EOL +
            "} ";

}
