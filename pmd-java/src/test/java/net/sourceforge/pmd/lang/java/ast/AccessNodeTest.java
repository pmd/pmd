/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class AccessNodeTest extends BaseParserTest {

    public static class MyAccessNode extends AbstractJavaAccessNode {
        public MyAccessNode(int i) {
            super(i);
        }

        @Override
        public Object jjtAccept(JavaParserVisitor visitor, Object data) {
            return visitor.visit(this, data);
        }


        @Override
        public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
            visitor.visit(this, data);
        }
    }

    @Test
    public void testModifiersOnClassDecl() {
        List<ASTClassOrInterfaceDeclaration> ops = java.getNodes(ASTClassOrInterfaceDeclaration.class, TEST1);
        assertTrue(ops.get(0).isPublic());
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


    private static String makeAccessJavaCode(String[] access, String declRest) {
        String result = "public class Test { ";
        for (String s : access) {
            result += s + " ";
        }
        return result + " " + declRest + " }";
    }


    public static <T extends Node> T getDeclWithModifiers(String[] access, Class<T> target, String declRest) {
        ASTCompilationUnit acu = JavaParsingHelper.JUST_PARSE.parse(makeAccessJavaCode(access, declRest));

        List<T> declarations = acu.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class)
                            .findDescendantsOfType(target);

        assertEquals("Wrong number of declarations", 1, declarations.size());
        return declarations.get(0);
    }
}
