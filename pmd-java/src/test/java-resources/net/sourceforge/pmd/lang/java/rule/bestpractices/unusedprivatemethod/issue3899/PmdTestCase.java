/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedprivatemethod.issue3899;

public class PmdTestCase {
    public void check() {
        Child child = new Child();

        checkChild(child);
        checkParent(child);
    }

    // OK
    private void checkChild(Child child) {
    }

    // This method results in "UnusedPrivateMethod: Avoid unused private methods such as 'checkParent(Parent)'"
    private void checkParent(Parent parent) { // false positive
    }
}
