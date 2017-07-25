/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class AnoymousExtendingObject {

    public void foo() {
        System.out.println(new Object() {
            @Override
            public String toString() {
                return "Suprise!";
            }
        });
    }
}
