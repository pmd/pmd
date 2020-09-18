/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.symboltable.ImageFinderFunction;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

@Ignore
public class ImageFinderFunctionTest {

    @Test
    public void testSingleImage() {
        ImageFinderFunction f = new ImageFinderFunction("foo");
        // These tests were completely broken, they built a PrimaryPrefix
        // that is a child of a Name (not the reverse)
        // This is an example of why tests should never build nodes manually
        ASTVariableDeclaratorId node = InternalApiBridge.newVarId("foo");
        NameDeclaration decl = new VariableNameDeclaration(node);
        f.test(decl);
        assertEquals(decl, f.getDecl());
    }

    @Test
    public void testSeveralImages() {
        List<String> imgs = new ArrayList<>();
        imgs.add("Foo.foo");
        imgs.add("foo");
        ImageFinderFunction f = new ImageFinderFunction(imgs);
        ASTVariableDeclaratorId node = InternalApiBridge.newVarId("foo");
        NameDeclaration decl = new VariableNameDeclaration(node);
        f.test(decl);
        assertEquals(decl, f.getDecl());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ImageFinderFunctionTest.class);
    }
}
