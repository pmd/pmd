/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

/**
 * @author Clément Fournier
 * @since 6.2.0
 */
public abstract class AbstractClass {

    Object fun(String s) {
        return new Object();
    }


    public void arrayParams(String dflt, int[] keys, StringBuilder[] labels) {
    }


    public <T, R> R generic(T t, R r) {
        return r;
    }

}
