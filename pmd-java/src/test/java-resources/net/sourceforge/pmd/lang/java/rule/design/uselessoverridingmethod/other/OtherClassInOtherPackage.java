/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.uselessoverridingmethod.other;

public class OtherClassInOtherPackage {

    public void foo() {
        DirectSubclassInOtherPackage instance = new DirectSubclassInOtherPackage();
        // the following calls are only possible, because DirectSubclassInOtherPackage makes this
        // method available in this package as well.
        instance.doBase();
        instance.doBaseWithArg("a");
        instance.doBaseWithArgs("a", 1);
    }
}
