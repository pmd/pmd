/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata;

public class NestedClasses {


    void foo() {
        class InMethod {
            class IInMethod {

            }
        }
    }

    public class Inner {

        public Inner() {

        }

        public <T> Inner(T t) {

        }

        class IInner {

        }
    }
}
