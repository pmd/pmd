package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;

public class ASTMethodDeclarationTest extends TestCase {

    public void testGetVariableName() {
        int id = 0;

        ASTMethodDeclaration md = new ASTMethodDeclaration(id++);
        ASTMethodDeclarator de = new ASTMethodDeclarator(id++);
        de.setImage("foo");
        md.jjtAddChild(de, 0);

        assertEquals("foo", md.getMethodName());
    }
}
