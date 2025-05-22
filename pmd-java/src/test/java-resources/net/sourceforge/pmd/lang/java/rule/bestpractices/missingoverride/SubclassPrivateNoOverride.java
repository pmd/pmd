/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

/**
 * @author Clément Fournier
 * @since 6.4.0
 */
public class SubclassPrivateNoOverride extends SuperclassWithPrivate {

    // No override
    public void foo() {

    }
}
