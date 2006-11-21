package test.net.sourceforge.pmd.ast;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import test.net.sourceforge.pmd.testframework.ParserTst;

public class ASTFormalParameterTest extends ParserTst {

    public void testVarargs() throws Throwable {
        int nrOfVarArgs   = 0;
        int nrOfNoVarArgs = 0;
        
        Set ops = getNodes(new TargetJDK1_5(), ASTFormalParameter.class, TEST1);
        for (Iterator iter = ops.iterator(); iter.hasNext();) {
            ASTFormalParameter b = (ASTFormalParameter) iter.next();
            ASTVariableDeclaratorId variableDeclId = (ASTVariableDeclaratorId)b.getFirstChildOfType(ASTVariableDeclaratorId.class);
            if (!"x".equals(variableDeclId.getImage())) {
                assertTrue(b.isVarargs());
                nrOfVarArgs++;
            } else {
                assertFalse(b.isVarargs());
                nrOfNoVarArgs++;
            }
        }
        
        //Ensure that both possibilities are tested
        assertEquals(1, nrOfVarArgs);
        assertEquals(1, nrOfNoVarArgs);
    }

    private static final String TEST1 =
            "class Foo {" + PMD.EOL +
            " void bar(int x, int... others) {}" + PMD.EOL +
            "}";

}
