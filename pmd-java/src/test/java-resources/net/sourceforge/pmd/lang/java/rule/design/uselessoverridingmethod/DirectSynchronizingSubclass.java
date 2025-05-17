/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.uselessoverridingmethod;

public class DirectSynchronizingSubclass extends BaseClass {

    @Override
    protected synchronized void doBase() {
        // overriding for synchronized
        super.doBase();
    }
}
