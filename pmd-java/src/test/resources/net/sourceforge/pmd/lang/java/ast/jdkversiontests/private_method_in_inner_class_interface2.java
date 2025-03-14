/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.testdata;

/**
 * Prior to Java9, private methods are not possible.
 * Make sure, they are detected.
 *
 * @see https://github.com/pmd/pmd/issues/793
 */
public class PrivateMethodsInInterface2 {

    public interface Interface1 {
        Object FOO = new Object() {
            private void privateMethod() { }
        };
        private void privateMethodInInterface1() { } // note: this will be a parser error!
    }

    public interface Interface2 {
        class InnerClass {
            private void privateMethod() { }
        }
    }

    public interface Interface3 {
        enum InnerEnum {
            VALUE;
            private void privateMethod() { }
        }
    }
}
