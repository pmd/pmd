/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JValueSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Base implementation.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
abstract class AbstractSymbolTable implements JSymbolTable {

    /** Additional info about the context. */
    final SymbolTableResolveHelper myResolveHelper;
    private final JSymbolTable myParent;


    /**
     * Constructor with just the parent table.
     *
     * @param parent Parent table
     * @param helper Resolve helper
     */
    AbstractSymbolTable(JSymbolTable parent, SymbolTableResolveHelper helper) {
        this.myParent = parent;
        this.myResolveHelper = helper;
    }


    @Override
    public final JSymbolTable getParent() {
        return myParent;
    }


    @Override
    public final @Nullable JTypeDeclSymbol resolveTypeName(String simpleName) {
        @Nullable JTypeDeclSymbol result = resolveTypeNameImpl(simpleName);
        return result != null ? result : myParent.resolveTypeName(simpleName);
    }


    @Override
    public final @Nullable JValueSymbol resolveValueName(String simpleName) {
        @Nullable JValueSymbol result = resolveValueNameImpl(simpleName);
        return result != null ? result : myParent.resolveValueName(simpleName);
    }


    @Override
    public final Stream<JMethodSymbol> resolveMethodName(String simpleName) {
        // This allows the stream contributed by the parent to be resolved lazily,
        // ie not evaluated unless the stream contributed by this table runs out of values,
        // a behaviour that Stream.concat can't provide
        return Stream.<Supplier<Stream<JMethodSymbol>>>of(
            () -> resolveMethodNameImpl(simpleName),
            () -> myParent.resolveMethodName(simpleName)
        ).flatMap(Supplier::get);
    }


    /** Finds the matching methods among the declarations tracked by this table without asking the parent. */
    protected abstract Stream<JMethodSymbol> resolveMethodNameImpl(String simpleName);

    // We could internally avoid using Optional to reduce the number of created optionals as an optimisation


    /** Finds a type name among the declarations tracked by this table without asking the parent. */
    protected abstract @Nullable JTypeDeclSymbol resolveTypeNameImpl(String simpleName);


    /** Finds a value among the declarations tracked by this table without asking the parent. */
    protected abstract @Nullable JValueSymbol resolveValueNameImpl(String simpleName);


    /** Gets a logger, used to have a different logger for different scopes. */
    protected abstract Logger getLogger();


    /**
     * Tries to load a class and logs it if it is not found.
     *
     * @param fqcn Binary name of the class to load
     *
     * @return The class, or null if it couldn't be resolved
     */
    @Nullable
    final JClassSymbol loadClassReportFailure(String fqcn) {
        JClassSymbol loaded = myResolveHelper.loadClassOrFail(fqcn);
        if (loaded == null) {
            getLogger().log(Level.FINE, () -> "Failed loading class " + fqcn + "with an incomplete classpath.");
        }

        return loaded;
    }


    /**
     * Tries to load a class, not logging failure.
     *
     * @param canonicalName Canonical name of the class to load
     *
     * @return The class, or null if it couldn't be resolved
     */
    @Nullable
    final JClassSymbol loadClassIgnoreFailure(String canonicalName) {
        return myResolveHelper.loadClassOrFail(canonicalName);
    }

    /**
     * Returns true if this table doesn't contain any information, and
     * can be eliminated from the stack entirely.
     */
    boolean isPrunable() {
        return false;
    }

}
