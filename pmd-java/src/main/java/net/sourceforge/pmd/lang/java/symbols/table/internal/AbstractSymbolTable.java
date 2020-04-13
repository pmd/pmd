/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;


/**
 * Base implementation.
 *
 * @since 7.0.0
 */
abstract class AbstractSymbolTable implements JSymbolTable {

    /** Additional info about the context. */
    protected final SymbolTableHelper helper;
    private final JSymbolTable parent;

    AbstractSymbolTable(JSymbolTable parent, SymbolTableHelper helper) {
        assert parent != null : "Null parent!";
        assert helper != null : "Null helper!";

        this.parent = parent;
        this.helper = helper;
    }


    @Override
    @NonNull
    public final JSymbolTable getParent() {
        return parent;
    }


    @Nullable
    @Override
    public final ResolveResult<JTypeDeclSymbol> resolveTypeName(String simpleName) {
        @Nullable ResolveResult<JTypeDeclSymbol> result = resolveTypeNameImpl(simpleName);
        return result != null ? result : parent.resolveTypeName(simpleName);
    }


    @Override
    public final @Nullable ResolveResult<JVariableSymbol> resolveValueName(String simpleName) {
        @Nullable ResolveResult<JVariableSymbol> result = resolveValueNameImpl(simpleName);
        return result != null ? result : parent.resolveValueName(simpleName);
    }


    @Override
    public final List<JMethodSymbol> resolveMethodName(String simpleName) {
        // the default implementation always returns the parent results
        // if the table has methods itself, it needs to override this
        List<JMethodSymbol> result = getCachedMethodResults(simpleName);
        if (result != null) {
            return result;
        }
        // Otherwise, create the list from the union
        List<JMethodSymbol> localResult = resolveMethodNamesHere(simpleName);
        List<JMethodSymbol> parentResult = parent.resolveMethodName(simpleName); // recurse on parent
        if (localResult.isEmpty()) {
            result = parentResult; // parent result is already unmodifiable
        } else {
            result = localResult;
            result.addAll(parentResult);
            result = Collections.unmodifiableList(result);
        }
        cacheMethodResult(simpleName, result);
        return result;
    }

    /**
     * Finds the matching methods among the declarations tracked by this
     * table *without asking the parent*. Returns a mutable list.
     *
     * <p>This method is only called if {@link #getCachedMethodResults(String)}
     * returns a null result. For that reason, that method must be overridden
     * if this one is to be used.
     */
    protected /*Mutable*/List<JMethodSymbol> resolveMethodNamesHere(String simpleName) {
        // dead code if getCachedMethodResults is not overridden
        return new ArrayList<>();
    }

    @Nullable
    protected List<JMethodSymbol> getCachedMethodResults(String simpleName) {
        return parent.resolveMethodName(simpleName); // this table is empty
    }

    protected void cacheMethodResult(String simpleName, List<JMethodSymbol> sigs) {
        // do nothing, dead code if getCachedMethodResults is not overridden
    }

    /**
     * Finds a type name among the declarations tracked by this table without asking the parent.
     */
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        return null;
    }


    /** Finds a value among the declarations tracked by this table without asking the parent. */
    protected @Nullable ResolveResult<JVariableSymbol> resolveValueNameImpl(String simpleName) {
        return null;
    }


    /**
     * Tries to load a class and logs it if it is not found.
     *
     * @param anImport Node owning the warning
     * @param fqcn     Binary name of the class to load
     *
     * @return The class, or null if it couldn't be resolved
     */
    @Nullable
    final JClassSymbol loadClassReportFailure(ASTImportDeclaration anImport, String fqcn) {
        JClassSymbol loaded = helper.loadClassOrFail(fqcn);
        if (loaded == null) {
            helper.getLogger().warning(anImport, SemanticChecksLogger.CANNOT_RESOLVE_SYMBOL, fqcn);
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
        return helper.loadClassOrFail(canonicalName);
    }

    /**
     * Returns true if this table doesn't contain any information, and
     * can be eliminated from the stack entirely.
     */
    boolean isPrunable() {
        // TODO would be better to conside the three channels separate.
        //  That way local scopes with no class declaration skip directly to type declaration scope
        return false;
    }
}
