/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class MethodGenericExplicit {
    public <T> T foo() {
        return null;
    }

    public void test() {
        String s = this.<String>foo();
    }
}
