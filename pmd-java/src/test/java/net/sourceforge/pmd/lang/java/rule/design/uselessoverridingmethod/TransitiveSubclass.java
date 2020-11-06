/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.uselessoverridingmethod;

public class TransitiveSubclass extends OtherSubclass {

    @Override
    public void doBase() {
        super.doBase();
    }
}
