/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.AccessNode;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Set;

public class AccessNodeTest extends ParserTst {

    @Test
    public void testModifiersOnClassDecl() throws Throwable {
        Set ops = getNodes(ASTClassOrInterfaceDeclaration.class, TEST1);
        assertTrue(((ASTClassOrInterfaceDeclaration) (ops.iterator().next())).isPublic());
    }

    private static final String TEST1 =
            "public class Foo {}";


    @Test
    public void testStatic() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not static.", node.isStatic());
        node.setStatic();
        assertTrue("Node set to static, not static.", node.isStatic());
    }

    @Test
    public void testPublic() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not public.", node.isPublic());
        node.setPublic();
        assertTrue("Node set to public, not public.", node.isPublic());
    }

    @Test
    public void testProtected() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not protected.", node.isProtected());
        node.setProtected();
        assertTrue("Node set to protected, not protected.", node.isProtected());
    }

    @Test
    public void testPrivate() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not private.", node.isPrivate());
        node.setPrivate();
        assertTrue("Node set to private, not private.", node.isPrivate());
    }

    @Test
    public void testFinal() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not final.", node.isFinal());
        node.setFinal();
        assertTrue("Node set to final, not final.", node.isFinal());
    }

    @Test
    public void testSynchronized() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not synchronized.", node.isSynchronized());
        node.setSynchronized();
        assertTrue("Node set to synchronized, not synchronized.", node.isSynchronized());
    }

    @Test
    public void testVolatile() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not volatile.", node.isVolatile());
        node.setVolatile();
        assertTrue("Node set to volatile, not volatile.", node.isVolatile());
    }

    @Test
    public void testTransient() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not transient.", node.isTransient());
        node.setTransient();
        assertTrue("Node set to transient, not transient.", node.isTransient());
    }

    @Test
    public void testNative() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not native.", node.isNative());
        node.setNative();
        assertTrue("Node set to native, not native.", node.isNative());
    }

    @Test
    public void testAbstract() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not abstract.", node.isAbstract());
        node.setAbstract();
        assertTrue("Node set to abstract, not abstract.", node.isAbstract());
    }

    @Test
    public void testStrict() {
        AccessNode node = new AccessNode(1);
        assertFalse("Node should default to not strict.", node.isStrictfp());
        node.setStrictfp();
        assertTrue("Node set to strict, not strict.", node.isStrictfp());
    }

    @Test
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AccessNodeTest.class);
    }
}
