package net.sourceforge.pmd.ast;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;

import org.junit.Test;

public class ASTMethodDeclarationTest {

    @Test
    public void testGetVariableName() {
        int id = 0;

        ASTMethodDeclaration md = new ASTMethodDeclaration(id++);
        ASTMethodDeclarator de = new ASTMethodDeclarator(id++);
        de.setImage("foo");
        md.jjtAddChild(de, 0);

        assertEquals("foo", md.getMethodName());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ASTMethodDeclarationTest.class);
    }
}
