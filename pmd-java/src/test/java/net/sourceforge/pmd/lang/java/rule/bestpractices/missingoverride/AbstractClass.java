/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

/**
 * @author Clément Fournier
 * @since 6.1.0
 */
public abstract class AbstractClass {

    abstract Object fun(String s);


    public void arrayParams(String dflt, int[] keys, StringBuilder[] labels) {
    }

}
