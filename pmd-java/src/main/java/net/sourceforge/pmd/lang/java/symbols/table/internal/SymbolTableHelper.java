/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;


/**
 * Object passing around config for {@link AbstractSymbolTable}.
 *
 * @since 7.0.0
 */
final class SymbolTableHelper {

    private final String thisPackage;
    private final JavaAstProcessor processor;


    SymbolTableHelper(String thisPackage, JavaAstProcessor processor) {
        assert thisPackage != null;
        assert processor != null;
        this.thisPackage = thisPackage;
        this.processor = processor;
    }


    /** Prepend the package name, handling empty package. */
    String prependPackageName(String name) {
        return thisPackage.isEmpty() ? name : thisPackage + "." + name;
    }


    public JClassSymbol findSymbolCannotFail(String name) {
        JClassSymbol found = processor.getSymResolver().resolveClassFromCanonicalName(name);
        return found == null ? processor.makeUnresolvedReference(name, 0)
                             : found;
    }

    /** @see SymbolResolver#resolveClassFromCanonicalName(String) */
    @Nullable
    JClassSymbol loadClassOrFail(String fqcn) {
        return processor.getSymResolver().resolveClassFromCanonicalName(fqcn);
    }

    SemanticChecksLogger getLogger() {
        return processor.getLogger();
    }

    public String getThisPackage() {
        return thisPackage;
    }
}
