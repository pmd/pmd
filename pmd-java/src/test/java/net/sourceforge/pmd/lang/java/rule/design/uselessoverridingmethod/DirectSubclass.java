/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.uselessoverridingmethod;

public class DirectSubclass extends BaseClass {

    // overrides to make the methods public

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
