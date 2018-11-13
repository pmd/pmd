/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

public class ConcreteClassArrayParams extends AbstractClass {
    @Override
    Object fun(String s) {
        return null;
    }


    @Override
    public void arrayParams(String dflt, int[] keys, StringBuilder[] labels) {
        super.arrayParams(dflt, keys, labels);
    }
}
