/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.AccessNode;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

public class AccessNodeTest extends ParserTst {

    public void testModifiersOnClassDecl() throws Throwable {
        Set ops = getNodes(ASTClassOrInterfaceDeclaration.class, TEST1);
        assertTrue(((ASTClassOrInterfaceDeclaration) (ops.iterator().next())).isPublic());
    }

    private static final String TEST1 =
            "public class Foo {}";


    public void testStatic() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not static.", node.isStatic());
        node.setStatic();
        assertTrue("Node set to static, not static.", node.isStatic());
    }

    public void testPublic() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not public.", node.isPublic());
        node.setPublic();
        assertTrue("Node set to public, not public.", node.isPublic());
    }

    public void testProtected() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not protected.", node.isProtected());
        node.setProtected();
        assertTrue("Node set to protected, not protected.", node.isProtected());
    }

    public void testPrivate() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not private.", node.isPrivate());
        node.setPrivate();
        assertTrue("Node set to private, not private.", node.isPrivate());
    }

    public void testFinal() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not final.", node.isFinal());
        node.setFinal();
        assertTrue("Node set to final, not final.", node.isFinal());
    }

    public void testSynchronized() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not synchronized.", node.isSynchronized());
        node.setSynchronized();
        assertTrue("Node set to synchronized, not synchronized.", node.isSynchronized());
    }

    public void testVolatile() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not volatile.", node.isVolatile());
        node.setVolatile();
        assertTrue("Node set to volatile, not volatile.", node.isVolatile());
    }

    public void testTransient() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not transient.", node.isTransient());
        node.setTransient();
        assertTrue("Node set to transient, not transient.", node.isTransient());
    }

    public void testNative() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not native.", node.isNative());
        node.setNative();
        assertTrue("Node set to native, not native.", node.isNative());
    }

    public void testAbstract() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not abstract.", node.isAbstract());
        node.setAbstract();
        assertTrue("Node set to abstract, not abstract.", node.isAbstract());
    }

    public void testStrict() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not strict.", node.isStrictfp());
        node.setStrictfp();
        assertTrue("Node set to strict, not strict.", node.isStrictfp());
    }

    public void testPackagePrivate() {
        AccessNode node = new AccessNode(1);
        assertTrue("Node should default to package private.", node.isPackagePrivate());
        node.setPrivate();
        assertFalse("Node set to private, still package private.", node.isPackagePrivate());
        node = new AccessNode(1);
        node.setPublic();
        assertFalse("Node set to public, still package private.", node.isPackagePrivate());
        node = new AccessNode(1);
        node.setProtected();
        assertFalse("Node set to protected, still package private.", node.isPackagePrivate());
    }
}
