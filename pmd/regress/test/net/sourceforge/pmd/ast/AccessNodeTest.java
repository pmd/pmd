/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.AccessNode;

public class AccessNodeTest extends TestCase {
    public void testStatic() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not static.", !node.isStatic());

        node.setStatic();
        assertTrue("Node set to static, not static.", node.isStatic());
    }

    public void testPublic() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not public.", !node.isPublic());

        node.setPublic();
        assertTrue("Node set to public, not public.", node.isPublic());
    }

    public void testProtected() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not protected.", !node.isProtected());

        node.setProtected();
        assertTrue("Node set to protected, not protected.", node.isProtected());
    }

    public void testPrivate() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not private.", !node.isPrivate());

        node.setPrivate();
        assertTrue("Node set to private, not private.", node.isPrivate());
    }

    public void testFinal() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not final.", !node.isFinal());

        node.setFinal();
        assertTrue("Node set to final, not final.", node.isFinal());
    }

    public void testSynchronized() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not synchronized.", !node.isSynchronized());

        node.setSynchronized();
        assertTrue("Node set to synchronized, not synchronized.", node.isSynchronized());
    }

    public void testVolatile() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not volatile.", !node.isVolatile());

        node.setVolatile();
        assertTrue("Node set to volatile, not volatile.", node.isVolatile());
    }

    public void testTransient() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not transient.", !node.isTransient());

        node.setTransient();
        assertTrue("Node set to transient, not transient.", node.isTransient());
    }

    public void testNative() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not native.", !node.isNative());

        node.setNative();
        assertTrue("Node set to native, not native.", node.isNative());
    }

    public void testInterface() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not interface.", !node.isInterface());

        node.setInterface();
        assertTrue("Node set to interface, not interface.", node.isInterface());
    }

    public void testAbstract() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not abstract.", !node.isAbstract());

        node.setAbstract();
        assertTrue("Node set to abstract, not abstract.", node.isAbstract());
    }

    public void testStrict() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not strict.", !node.isStrict());

        node.setStrict();
        assertTrue("Node set to strict, not strict.", node.isStrict());
    }

    public void testSuper() {
        AccessNode node = new AccessNode(1);

        assertTrue("Node should default to not super.", !node.isSuper());

        node.setSuper();
        assertTrue("Node set to super, not super.", node.isSuper());
    }
}
