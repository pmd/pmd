/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.uselessoverridingmethod.other;

import net.sourceforge.pmd.lang.java.rule.design.uselessoverridingmethod.BaseClass;

public class DirectSubclassInOtherPackage extends BaseClass {

    // overrides to make the method available in this package

    @Override
    protected void doBase() {
        super.doBase();
    }

    @Override
    protected void doBaseWithArg(String foo) {
        super.doBaseWithArg(foo);
    }

    @Override
    protected void doBaseWithArgs(String foo, int bar) {
        super.doBaseWithArgs(foo, bar);
    }
}
