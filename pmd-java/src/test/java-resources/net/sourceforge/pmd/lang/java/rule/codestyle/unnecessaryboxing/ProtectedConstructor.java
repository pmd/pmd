/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryboxing;

public class ProtectedConstructor {

    public ProtectedConstructor(int value) {
    }

    protected ProtectedConstructor(Object value) {
    }
}
