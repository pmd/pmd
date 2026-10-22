/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @internalApi None of this is published API, and compatibility can be broken anytime! Use this only at your own risk.
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static boolean isSameTypeInInference(JTypeMirror t, JTypeMirror s) {
        return TypeOps.isSameTypeInInference(t, s);
    }

    public static boolean isSubClassOfNoInterface(JClassSymbol sub, JClassSymbol sup) {
        return TypeOps.isSubClassOfNoInterface(sub, sup);
    }
}
