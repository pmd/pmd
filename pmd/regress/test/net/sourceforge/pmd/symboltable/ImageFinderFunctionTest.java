/*
 * User: tom
 * Date: Oct 30, 2002
 * Time: 10:50:59 AM
 */
package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.symboltable.ImageFinderFunction;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

public class ImageFinderFunctionTest extends TestCase {

    public void testSingleImage() {
        ImageFinderFunction f = new ImageFinderFunction("foo");
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("foo");
        NameDeclaration decl = new VariableNameDeclaration(node);
        f.applyTo(decl);
        assertEquals(decl, f.getDecl());
    }

    public void testSeveralImages() {
        List imgs = new ArrayList();
        imgs.add("Foo.foo");
        imgs.add("foo");
        ImageFinderFunction f = new ImageFinderFunction(imgs);
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("foo");
        NameDeclaration decl = new VariableNameDeclaration(node);
        f.applyTo(decl);
        assertEquals(decl, f.getDecl());
    }
}
