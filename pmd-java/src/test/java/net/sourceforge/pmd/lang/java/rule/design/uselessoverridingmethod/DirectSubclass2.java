/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.uselessoverridingmethod;

public class DirectSubclass2 extends DirectSubclass {
    // useless overrides - it's already public
    @Override
    public void doBase() {
        super.doBase();
    }
 
    @Override
    public void doBaseWithArg(String foo) {
        super.doBaseWithArg(foo);
    }

    @Override
    public void doBaseWithArgs(String foo, int bar) {
        super.doBaseWithArgs(foo, bar);
    }
}
