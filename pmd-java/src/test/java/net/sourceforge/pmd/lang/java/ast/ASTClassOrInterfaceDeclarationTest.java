/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;


/**
 * @author Clément Fournier
 * @since 6.1.0
 */
public class ASTClassOrInterfaceDeclarationTest {

    private static final String LOCAL_CLASS_IN_METHOD
            = "class Foo { void bar() { class Local {}}}";

    private static final String NESTED_CLASS_IS_NOT_LOCAL
            = "class Foo { class Nested {} void bar() {}}";

    private static final String LOCAL_CLASS_IN_INITIALIZER
            = "class Foo { { class Local {} } }";

    private static final String LOCAL_CLASS_WITH_MODIFIERS
            = "class Foo { { abstract class Local {} } }";

    private static final String LOCAL_CLASS_WITH_MIXED_MODIFIER_ANNOTATIONS
            = "class Foo { { final @F class Local {} } }";

    private static final String LOCAL_CHILDREN_ARE_NOT_ALWAYS_LOCAL
            = "class Foo { { class Local { class Nested {} void bar() {class Local2 {}}}}}";


    @Test
    public void testLocalInMethod() {
        List<ASTClassOrInterfaceDeclaration> classes = getClassDecls(LOCAL_CLASS_IN_METHOD);
        assertTrue(classes.size() == 2);

        assertFalse("Local class false-positive", classes.get(0).isLocal());
        assertTrue("Local class false-negative", classes.get(1).isLocal());
    }


    @Test
    public void testLocalInInitializer() {
        List<ASTClassOrInterfaceDeclaration> classes = getClassDecls(LOCAL_CLASS_IN_INITIALIZER);
        assertTrue(classes.size() == 2);

        assertFalse("Local class false-positive", classes.get(0).isLocal());
        assertTrue("Local class false-negative", classes.get(1).isLocal());
    }



    @Test
    public void testLocalAbstractClass() {
        List<ASTClassOrInterfaceDeclaration> classes = getClassDecls(LOCAL_CLASS_WITH_MODIFIERS);
        assertTrue(classes.size() == 2);

        assertFalse("Local class false-positive", classes.get(0).isLocal());
        assertTrue("Local class false-negative", classes.get(1).isLocal());
        assertTrue("Local class should preserve its modifiers", classes.get(1).isAbstract());
    }


    @Test
    public void testLocalClassWithMixedModifiers() {
        List<ASTClassOrInterfaceDeclaration> classes = getClassDecls(LOCAL_CLASS_WITH_MIXED_MODIFIER_ANNOTATIONS);
        assertTrue(classes.size() == 2);

        assertFalse("Local class false-positive", classes.get(0).isLocal());
        assertTrue("Local class false-negative", classes.get(1).isLocal());
        assertTrue("Local class should preserve its modifiers", classes.get(1).isFinal());
    }



    @Test
    public void testLocalClassVisibility() {
        List<ASTClassOrInterfaceDeclaration> classes = getClassDecls(LOCAL_CLASS_WITH_MODIFIERS);
        assertTrue(classes.size() == 2);

        assertFalse("Local class false-positive", classes.get(0).isLocal());
        assertTrue("Local class false-negative", classes.get(1).isLocal());
        assertFalse("Local class is not public", classes.get(1).isPublic());
        assertFalse("Local class is not private", classes.get(1).isPrivate());
        assertFalse("Local class is not protected", classes.get(1).isProtected());
        assertFalse("Local class is not package-private", classes.get(1).isPackagePrivate());
    }


    @Test
    public void testNestedClassIsNotLocal() {
        List<ASTClassOrInterfaceDeclaration> classes = getClassDecls(NESTED_CLASS_IS_NOT_LOCAL);
        assertTrue(classes.size() == 2);

        assertFalse("Local class false-positive", classes.get(0).isLocal());
        assertFalse("Local class false-positive", classes.get(1).isLocal());
    }


    @Test
    public void testLocalChildrenAreNotAlwaysLocal() {
        List<ASTClassOrInterfaceDeclaration> classes = getClassDecls(LOCAL_CHILDREN_ARE_NOT_ALWAYS_LOCAL);
        assertTrue(classes.size() == 4);

        assertFalse("Local class false-positive", classes.get(0).isLocal()); // class Foo
        assertTrue("Local class false-negative", classes.get(1).isLocal());  // class Local
        assertFalse("Local class false-positive", classes.get(2).isLocal()); // class Nested
        assertTrue("Local class false-negative", classes.get(3).isLocal());  // class Local2
    }


    private List<ASTClassOrInterfaceDeclaration> getClassDecls(String code) {
        return JavaParsingHelper.WITH_PROCESSING.getNodes(ASTClassOrInterfaceDeclaration.class, code);
    }
}
