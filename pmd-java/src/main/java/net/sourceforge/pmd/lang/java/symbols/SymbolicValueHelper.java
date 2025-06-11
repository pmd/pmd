/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

/**
 * Private helper for {@link SymbolicValue} and implementations.
 *
 * @author Clément Fournier
 */
final class SymbolicValueHelper {

    private SymbolicValueHelper() {
        // utility class
    }

    static boolean equalsModuloWrapper(SymbolicValue sv, Object other) {
        if (other instanceof SymbolicValue) {
            return sv.equals(other);
        }
        else {
            return sv.valueEquals(other);
        }
    }

}
