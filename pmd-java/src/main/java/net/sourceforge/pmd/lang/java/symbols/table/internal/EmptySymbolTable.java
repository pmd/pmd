/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;


/**
 * Dummy empty scope representing the top of all symbol table stacks.
 *
 * @since 7.0.0
 */
final class EmptySymbolTable implements JSymbolTable {


    private static final EmptySymbolTable INSTANCE = new EmptySymbolTable();


    private EmptySymbolTable() {

    }

    @Override
    public JSymbolTable getParent() {
        return null;
    }


    @Override
    public @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeName(String simpleName) {
        return ResolveResultImpl.failed();
    }


    @Override
    public @Nullable ResolveResult<JVariableSymbol> resolveValueName(String simpleName) {
        return ResolveResultImpl.failed();
    }


    @Override
    public List<JMethodSymbol> resolveMethodName(String simpleName) {
        return Collections.emptyList();
    }


    /** Returns the shared instance. */
    public static EmptySymbolTable getInstance() {
        return INSTANCE;
    }
}
