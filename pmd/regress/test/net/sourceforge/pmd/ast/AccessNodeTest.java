package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.AccessNode;

public class AccessNodeTest extends TestCase {
    public void testStatic() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not static.", !node.isStatic());

        node.setStatic(true);
        assertTrue("Node set to static, not static.", node.isStatic());


        node.setStatic(false);
        assertTrue("Node set to not static, is static.", !node.isStatic());
    }

    public void testPublic() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not public.", !node.isPublic());

        node.setPublic(true);
        assertTrue("Node set to public, not public.", node.isPublic());

        node.setPublic(false);
        assertTrue("Node set to not public, is public.", !node.isPublic());
    }

    public void testProtected() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not protected.", !node.isProtected());

        node.setProtected(true);
        assertTrue("Node set to protected, not protected.", node.isProtected());

        node.setProtected(false);
        assertTrue("Node set to not protected, is protected.", !node.isProtected());
    }

    public void testPrivate() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not private.", !node.isPrivate());

        node.setPrivate(true);
        assertTrue("Node set to private, not private.", node.isPrivate());

        node.setPrivate(false);
        assertTrue("Node set to not private, is private.", !node.isPrivate());
    }

    public void testFinal() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not final.", !node.isFinal());

        node.setFinal(true);
        assertTrue("Node set to final, not final.", node.isFinal());

        node.setFinal(false);
        assertTrue("Node set to not final, is final.", !node.isFinal());
    }

    public void testSynchronized() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not synchronized.", !node.isSynchronized());

        node.setSynchronized(true);
        assertTrue("Node set to synchronized, not synchronized.", node.isSynchronized());

        node.setSynchronized(false);
        assertTrue("Node set to not synchronized, is synchronized.", !node.isSynchronized());
    }

    public void testVolatile() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not volatile.", !node.isVolatile());

        node.setVolatile(true);
        assertTrue("Node set to volatile, not volatile.", node.isVolatile());

        node.setVolatile(false);
        assertTrue("Node set to not volatile, is volatile.", !node.isVolatile());
    }

    public void testTransient() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not transient.", !node.isTransient());

        node.setTransient(true);
        assertTrue("Node set to transient, not transient.", node.isTransient());

        node.setTransient(false);
        assertTrue("Node set to not transient, is transient.", !node.isTransient());
    }

    public void testNative() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not native.", !node.isNative());

        node.setNative(true);
        assertTrue("Node set to native, not native.", node.isNative());

        node.setNative(false);
        assertTrue("Node set to not native, is native.", !node.isNative());
    }

    public void testInterface() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not interface.", !node.isInterface());

        node.setInterface(true);
        assertTrue("Node set to interface, not interface.", node.isInterface());

        node.setInterface(false);
        assertTrue("Node set to not interface, is interface.", !node.isInterface());
    }

    public void testAbstract() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not abstract.", !node.isAbstract());

        node.setAbstract(true);
        assertTrue("Node set to abstract, not abstract.", node.isAbstract());

        node.setAbstract(false);
        assertTrue("Node set to not abstract, is abstract.", !node.isAbstract());
    }

    public void testStrict() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not strict.", !node.isStrict());

        node.setStrict(true);
        assertTrue("Node set to strict, not strict.", node.isStrict());

        node.setStrict(false);
        assertTrue("Node set to not strict, is strict.", !node.isStrict());
    }

    public void testSuper() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not super.", !node.isSuper());

        node.setSuper(true);
        assertTrue("Node set to super, not super.", node.isSuper());

        node.setSuper(false);
        assertTrue("Node set to not super, is super.", !node.isSuper());
    }
}
