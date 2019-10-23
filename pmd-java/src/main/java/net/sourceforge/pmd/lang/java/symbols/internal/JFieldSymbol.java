/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

/**
 * Represents a field declaration.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JFieldSymbol extends JAccessibleElementSymbol, JValueSymbol {
    /** Returns true if this field is volatile. */
    boolean isVolatile();


    /** Returns true if this field is transient. */
    boolean isTransient();


    /** Returns true if this field is static. */
    boolean isStatic();
}
