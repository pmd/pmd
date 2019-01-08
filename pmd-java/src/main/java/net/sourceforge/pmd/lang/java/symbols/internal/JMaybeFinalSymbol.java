/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

/**
 * Represents a declaration that may be final.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JMaybeFinalSymbol {

    /**
     * Returns true if this declaration is declared final.
     */
    boolean isFinal();

}
