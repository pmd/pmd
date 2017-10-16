/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class SubType extends SuperType {

    @Override
    public void myMethod() {
        super.myMethod();
    }
}
