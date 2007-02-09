package test.net.sourceforge.pmd.ast;

import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTBooleanLiteral;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

public class ASTBooleanLiteralTest extends ParserTst {

    @Test
    public void testTrue() throws Throwable {
        Set ops = getNodes(ASTBooleanLiteral.class, TEST1);
        ASTBooleanLiteral b = (ASTBooleanLiteral) ops.iterator().next();
        assertTrue(b.isTrue());
    }

    @Test
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTBooleanLiteralTest.class);
    }
}
