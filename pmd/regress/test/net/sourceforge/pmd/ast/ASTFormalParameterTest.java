package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

public class ASTFormalParameterTest extends ParserTst {

    public void testVarargs() throws Throwable {
        Set ops = getNodes(new TargetJDK1_5(), ASTFormalParameter.class, TEST1);
        ASTFormalParameter b = (ASTFormalParameter) ops.iterator().next();
        if (!"x".equals(b.getImage())) {
            assertTrue(b.isVarargs());
        } else {
            assertFalse(b.isVarargs());
        }
    }

    private static final String TEST1 =
            "class Foo { " + PMD.EOL +
            " void bar(int x, int... others) {} " + PMD.EOL +
            "} ";

}
