/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.uselessoverridingmethod;

public class DirectSubclass2 extends DirectSubclass {
    @Override
    public void doBase() {
        super.doBase();
    }
}
