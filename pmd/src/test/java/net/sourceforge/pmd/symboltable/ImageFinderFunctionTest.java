/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symboltable.ImageFinderFunction;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

import org.junit.Test;
public class ImageFinderFunctionTest {

    @Test
    public void testSingleImage() {
        ImageFinderFunction f = new ImageFinderFunction("foo");
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("foo");
        NameDeclaration decl = new VariableNameDeclaration(node);
        f.applyTo(decl);
        assertEquals(decl, f.getDecl());
    }

    @Test
    public void testSeveralImages() {
        List<String> imgs = new ArrayList<String>();
        imgs.add("Foo.foo");
        imgs.add("foo");
        ImageFinderFunction f = new ImageFinderFunction(imgs);
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("foo");
        NameDeclaration decl = new VariableNameDeclaration(node);
        f.applyTo(decl);
        assertEquals(decl, f.getDecl());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ImageFinderFunctionTest.class);
    }
}
