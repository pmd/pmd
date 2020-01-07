/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;


/**
 * Object passing around config for {@link AbstractSymbolTable}.
 *
 * @since 7.0.0
 */
final class SymbolTableResolveHelper {

    private final String thisPackage;
    private final SymbolResolver symbolResolver;
    private final int jdkVersion;


    SymbolTableResolveHelper(String thisPackage,
                             SymbolResolver symbolResolver,
                             int jdkVersion) {

        this.thisPackage = thisPackage;
        this.symbolResolver = symbolResolver;
        this.jdkVersion = jdkVersion;

        assert symbolResolver != null;
        assert thisPackage != null;
    }


    /** Analysed language version. */
    int getJdkVersion() {
        return jdkVersion;
    }

    /** Prepend the package name, handling empty package. */
    String prependPackageName(String name) {
        return thisPackage.isEmpty() ? name : thisPackage + "." + name;
    }


    /** @see  SymbolResolver#resolveClassOrDefault(String)  */
    public JClassSymbol findSymbolCannotFail(String name) {
        return symbolResolver.resolveClassOrDefault(name);
    }

    /** @see  SymbolResolver#resolveClassFromCanonicalName(String) */
    @Nullable
    JClassSymbol loadClassOrFail(String fqcn) {
        return symbolResolver.resolveClassFromCanonicalName(fqcn);
    }

    public String getThisPackage() {
        return thisPackage;
    }
}
