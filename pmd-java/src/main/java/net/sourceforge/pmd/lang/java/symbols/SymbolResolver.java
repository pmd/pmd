/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Resolves symbols from their global name. This abstracts over whether
 * we're looking on a classpath, in a file tree, in a serialized index, etc.
 *
 * @author Clément Fournier
 */
public interface SymbolResolver {

    /**
     * Resolves a class symbol from its canonical name. Periods ('.') may
     * be interpreted as nested-class separators.
     */
    @Nullable
    JClassSymbol resolveClassFromCanonicalName(@NonNull String canonicalName);


    /**
     * Loads the class like {@link #resolveClassFromCanonicalName(String)},
     * but if this fails, returns an {@link JClassSymbol#isUnresolved() unresolved symbol}.
     */
    @NonNull
    JClassSymbol resolveClassOrDefault(@NonNull String canonicalName);

}
