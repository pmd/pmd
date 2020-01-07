/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JValueSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Dummy empty scope representing the top of all scope stacks.
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
    public @Nullable JTypeDeclSymbol resolveTypeName(String simpleName) {
        return null;
    }


    @Override
    public @Nullable JValueSymbol resolveValueName(String simpleName) {
        return null;
    }


    @Override
    public Stream<JMethodSymbol> resolveMethodName(String simpleName) {
        return Stream.empty();
    }


    /** Returns the shared instance. */
    public static EmptySymbolTable getInstance() {
        return INSTANCE;
    }
}
