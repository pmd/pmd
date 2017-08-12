/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class SuperClassB extends SuperClassB2 {
    protected SuperClassB bs;
    private String privateShadow;

    public SuperClassB inheritedB() {
        return null;
    }
}
