/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle.lambdacanbemethodreference.other;

/**
 * Has protected methods for a subclass in another package.
 */
public class ProtectedSuper {

    public void m(int x) { }

    protected void m(Object o) { }

    public static void u(Object o) { }

    protected void u() { }
}
