/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

/**
 * @author Clément Fournier
 * @since 6.4.0
 */
interface CloneableInterface extends Cloneable {
    // nothing to report, the method is not inherited
    // https://docs.oracle.com/javase/specs/jls/se7/html/jls-9.html#jls-9.6.3.4
    CloneableInterface clone() throws CloneNotSupportedException;
}
