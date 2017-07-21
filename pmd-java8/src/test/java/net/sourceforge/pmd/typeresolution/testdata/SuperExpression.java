/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata;

public class SuperExpression extends SuperClass {
    public void foo() {
        ((Runnable) (() -> {
                        SuperClass a = super.s; }))
                .run();
    }
}

