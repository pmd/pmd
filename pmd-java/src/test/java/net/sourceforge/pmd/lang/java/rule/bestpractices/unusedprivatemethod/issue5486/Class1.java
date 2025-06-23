/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedprivatemethod.issue5486;

public class Class1 {
    public void publicMethod() {
        new Class2().getClass1().privateMethod();
    }

    private void privateMethod() { // This method is detected as UnusedPrivateMethod (false positive)
        // do stuff
    }

    public void publicMethod2() {
        // Declaring the variable, makes the PMD works fine (workaround)
        Class1 class1 = new Class2().getClass1();
        class1.privateMethod2();
    }

    private void privateMethod2() {
        // do stuff
    }
}
