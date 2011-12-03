/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ast;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.testframework.ParserTst;

import org.junit.Test;


public class AccessNodeTest extends ParserTst {

    public static class MyAccessNode extends AbstractJavaAccessNode {
	public MyAccessNode(int i) {
	    super(i);
	}

	public MyAccessNode(JavaParser parser, int i) {
	    super(parser, i);
	}
    }

    @Test
    public void testModifiersOnClassDecl() throws Throwable {
        Set ops = getNodes(ASTClassOrInterfaceDeclaration.class, TEST1);
        assertTrue(((ASTClassOrInterfaceDeclaration) ops.iterator().next()).isPublic());
    }

    private static final String TEST1 =
            "public class Foo {}";


    @Test
    public void testStatic() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not static.", node.isStatic());
        node.setStatic(true);
        assertTrue("Node set to static, not static.", node.isStatic());
    }

    @Test
    public void testPublic() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not public.", node.isPublic());
        node.setPublic(true);
        assertTrue("Node set to public, not public.", node.isPublic());
    }

    @Test
    public void testProtected() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not protected.", node.isProtected());
        node.setProtected(true);
        assertTrue("Node set to protected, not protected.", node.isProtected());
    }

    @Test
    public void testPrivate() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not private.", node.isPrivate());
        node.setPrivate(true);
        assertTrue("Node set to private, not private.", node.isPrivate());
    }

    @Test
    public void testFinal() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not final.", node.isFinal());
        node.setFinal(true);
        assertTrue("Node set to final, not final.", node.isFinal());
    }

    @Test
    public void testSynchronized() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not synchronized.", node.isSynchronized());
        node.setSynchronized(true);
        assertTrue("Node set to synchronized, not synchronized.", node.isSynchronized());
    }

    @Test
    public void testVolatile() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not volatile.", node.isVolatile());
        node.setVolatile(true);
        assertTrue("Node set to volatile, not volatile.", node.isVolatile());
    }

    @Test
    public void testTransient() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not transient.", node.isTransient());
        node.setTransient(true);
        assertTrue("Node set to transient, not transient.", node.isTransient());
    }

    @Test
    public void testNative() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not native.", node.isNative());
        node.setNative(true);
        assertTrue("Node set to native, not native.", node.isNative());
    }

    @Test
    public void testAbstract() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not abstract.", node.isAbstract());
        node.setAbstract(true);
        assertTrue("Node set to abstract, not abstract.", node.isAbstract());
    }

    @Test
    public void testStrict() {
        AccessNode node = new MyAccessNode(1);
        assertFalse("Node should default to not strict.", node.isStrictfp());
        node.setStrictfp(true);
        assertTrue("Node set to strict, not strict.", node.isStrictfp());
    }

    @Test
    public void testPackagePrivate() {
        AccessNode node = new MyAccessNode(1);
        assertTrue("Node should default to package private.", node.isPackagePrivate());
        node.setPrivate(true);
        assertFalse("Node set to private, still package private.", node.isPackagePrivate());
        node = new MyAccessNode(1);
        node.setPublic(true);
        assertFalse("Node set to public, still package private.", node.isPackagePrivate());
        node = new MyAccessNode(1);
        node.setProtected(true);
        assertFalse("Node set to protected, still package private.", node.isPackagePrivate());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AccessNodeTest.class);
    }
}
