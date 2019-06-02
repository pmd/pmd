/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getNodes;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class AccessNodeTest {

    public static class MyAccessNode extends AbstractJavaAccessNode {
        public MyAccessNode(int i) {
            super(i);
        }

        public MyAccessNode(JavaParser parser, int i) {
            super(parser, i);
        }
    }

    @Test
    public void testModifiersOnClassDecl() {
        Set<ASTClassOrInterfaceDeclaration> ops = getNodes(ASTClassOrInterfaceDeclaration.class, TEST1);
        assertTrue(ops.iterator().next().isPublic());
    }

    private static final String TEST1 = "public class Foo {}";

    @Test
    public void testStatic() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not static.", node.isStatic());
        InternalApiBridge.setModifier(node, AccessNode.STATIC);
        assertTrue("Node set to static, not static.", node.isStatic());
    }

    @Test
    public void testPublic() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not public.", node.isPublic());
        InternalApiBridge.setModifier(node, AccessNode.PUBLIC);
        assertTrue("Node set to public, not public.", node.isPublic());
    }

    @Test
    public void testProtected() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not protected.", node.isProtected());
        InternalApiBridge.setModifier(node, AccessNode.PROTECTED);
        assertTrue("Node set to protected, not protected.", node.isProtected());
    }

    @Test
    public void testPrivate() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not private.", node.isPrivate());
        InternalApiBridge.setModifier(node, AccessNode.PRIVATE);
        assertTrue("Node set to private, not private.", node.isPrivate());
    }

    @Test
    public void testFinal() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not final.", node.isFinal());
        InternalApiBridge.setModifier(node, AccessNode.FINAL);
        assertTrue("Node set to final, not final.", node.isFinal());
    }

    @Test
    public void testSynchronized() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not synchronized.", node.isSynchronized());
        InternalApiBridge.setModifier(node, AccessNode.SYNCHRONIZED);
        assertTrue("Node set to synchronized, not synchronized.", node.isSynchronized());
    }

    @Test
    public void testVolatile() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not volatile.", node.isVolatile());
        InternalApiBridge.setModifier(node, AccessNode.VOLATILE);
        assertTrue("Node set to volatile, not volatile.", node.isVolatile());
    }

    @Test
    public void testTransient() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not transient.", node.isTransient());
        InternalApiBridge.setModifier(node, AccessNode.TRANSIENT);
        assertTrue("Node set to transient, not transient.", node.isTransient());
    }

    @Test
    public void testNative() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not native.", node.isNative());
        InternalApiBridge.setModifier(node, AccessNode.NATIVE);
        assertTrue("Node set to native, not native.", node.isNative());
    }

    @Test
    public void testAbstract() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not abstract.", node.isAbstract());
        InternalApiBridge.setModifier(node, AccessNode.ABSTRACT);
        assertTrue("Node set to abstract, not abstract.", node.isAbstract());
    }

    @Test
    public void testStrict() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not strict.", node.isStrictfp());
        InternalApiBridge.setModifier(node, AccessNode.STRICTFP);
        assertTrue("Node set to strict, not strict.", node.isStrictfp());
    }

    @Test
    public void testPackagePrivate() {
        AccessNode node = new MyAccessNode(1);
        assertTrue("Node should default to package private.", node.isPackagePrivate());
        InternalApiBridge.setModifier(node, AccessNode.PRIVATE);
        assertFalse("Node set to private, still package private.", node.isPackagePrivate());
        node = new MyAccessNode(1);
        InternalApiBridge.setModifier(node, AccessNode.PUBLIC);
        assertFalse("Node set to public, still package private.", node.isPackagePrivate());
        node = new MyAccessNode(1);
        InternalApiBridge.setModifier(node, AccessNode.PROTECTED);
        assertFalse("Node set to protected, still package private.", node.isPackagePrivate());
    }
}
