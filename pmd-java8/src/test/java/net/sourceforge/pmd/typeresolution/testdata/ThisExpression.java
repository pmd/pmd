/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata;

public class ThisExpression {

    public void foo() {
        ((Runnable) (() -> {
                        ThisExpression b = this; }))
                .run();
    }

    public interface PrimaryThisInterface {
        default void foo() {
            PrimaryThisInterface a = this;
        }
    }
}

