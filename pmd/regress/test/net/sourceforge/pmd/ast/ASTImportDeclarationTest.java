/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTName;

public class ASTImportDeclarationTest extends TestCase {

    public void testBasic() {
        ASTImportDeclaration i = new ASTImportDeclaration(1);
        assertTrue(!i.isImportOnDemand());
        i.setImportOnDemand();
        assertTrue(i.isImportOnDemand());
    }

    public void testGetImportedNameNode() {
        ASTImportDeclaration i = new ASTImportDeclaration(1);
        ASTName name = new ASTName(2);
        i.jjtAddChild(name, 0);
        assertEquals(name, i.getImportedNameNode());
    }
}
