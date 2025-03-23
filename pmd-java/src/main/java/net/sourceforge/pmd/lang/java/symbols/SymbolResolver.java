/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Resolves symbols from their global name. This abstracts over whether
 * we're looking on a classpath, in a file tree, in a serialized index, etc.
 */
public interface SymbolResolver {

    /**
     * Resolves a class symbol from its canonical name. Periods ('.') will
     * not be interpreted as nested-class separators, so this performs at
     * most one classloader lookup. Note that external symbol resolvers
     * do not need to implement lookup for primitive types, for local
     * and anonymous classes, or for array classes. This is handled by
     * the AST implementation or by the type system. Looking up such symbols
     * is undefined behaviour.
     */
    @Nullable
    JClassSymbol resolveClassFromBinaryName(@NonNull String binaryName);

    /**
     * @since 7.5.0
     */
    @Nullable
    JModuleSymbol resolveModule(@NonNull String moduleName);

    /**
     * Resolves a class symbol from its canonical name. Periods ('.') may
     * be interpreted as nested-class separators, so for n segments, this
     * performs at most n classloader lookups.
     */
    @Nullable
    default JClassSymbol resolveClassFromCanonicalName(@NonNull String canonicalName) {
        JClassSymbol symbol = resolveClassFromBinaryName(canonicalName);
        if (symbol != null) {
            return symbol;
        }
        int lastDotIdx = canonicalName.lastIndexOf('.');
        if (lastDotIdx < 0) {
            return null;
        } else {
            JClassSymbol outer = resolveClassFromCanonicalName(canonicalName.substring(0, lastDotIdx));
            if (outer != null) {
                String innerName = canonicalName.substring(lastDotIdx + 1);
                return outer.getDeclaredClass(innerName);
            }
        }

        return null;
    }


    /**
     * Produce a symbol resolver that asks the given resolvers in order.
     *
     * @param first  First resolver
     * @param others Rest of the resolvers
     */
    static SymbolResolver layer(SymbolResolver first, SymbolResolver... others) {
        assert first != null : "Null first table";
        assert others != null : "Null array";
        assert !ArrayUtils.contains(others, null) : "Null component";

        return new SymbolResolver() {
            private final List<SymbolResolver> stack = listOf(first, others);

            @Override
            public @Nullable JClassSymbol resolveClassFromBinaryName(@NonNull String binaryName) {
                for (SymbolResolver resolver : stack) {
                    JClassSymbol sym = resolver.resolveClassFromBinaryName(binaryName);
                    if (sym != null) {
                        return sym;
                    }
                }
                return null;
            }

            @Override
            public @Nullable JModuleSymbol resolveModule(@NonNull String moduleName) {
                for (SymbolResolver resolver : stack) {
                    JModuleSymbol symbol = resolver.resolveModule(moduleName);
                    if (symbol != null) {
                        return symbol;
                    }
                }
                return null;
            }

            @Override
            public void logStats() {
                stack.forEach(SymbolResolver::logStats);
            }
        };
    }

    /**
     * Called at the end of the analysis in order to log out statistics of the resolved symbols.
     */
    void logStats();
}
