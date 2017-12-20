/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.testdata;

/**
 * With Java9, private methods in interfaces are possible.
 * But they must not be confused with private methods in inner classes of interfaces
 * when using older java version.
 *
 * @see https://github.com/pmd/pmd/issues/793
 */
public interface InterfaceWithInnerClass {

    Object FOO = new Object() {
        private void privateMethod() { }
    };

    class InnerClass {
        private void privateMethod() { }
    }

    enum InnerEnum {
        VALUE;
        private void privateMethod() { }
    }
}
