/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

/**
 * Represents a declaration that may be static.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JMaybeStaticSymbol {

    boolean isStatic();

}
