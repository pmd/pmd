package net.sourceforge.pmd.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.testframework.ParserTst;

import org.junit.Test;


public class ASTFormalParameterTest extends ParserTst {

    @Test
    public void testVarargs() throws Throwable {
        int nrOfVarArgs   = 0;
        int nrOfNoVarArgs = 0;
        
        Set<ASTFormalParameter> ops = getNodes(LanguageVersion.JAVA_15, ASTFormalParameter.class, TEST1);
        for (Iterator<ASTFormalParameter> iter = ops.iterator(); iter.hasNext();) {
            ASTFormalParameter b = iter.next();
            ASTVariableDeclaratorId variableDeclId = b.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTFormalParameterTest.class);
    }
}
