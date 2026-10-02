/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryboxing;

public class ProtectedMembers {

    public ProtectedMembers(int value) {
    }

    protected ProtectedMembers(Object value) {
    }

    public void m(int value) {
    }

    protected void m(Object value) {
    }
}
