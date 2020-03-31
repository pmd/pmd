/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;

abstract class ResolveResultImpl<T> implements ResolveResult<T> {


    protected final AbstractSymbolTable symbolTable;
    private final JavaNode contributor;
    private final T sym;

    ResolveResultImpl(T sym,
                      AbstractSymbolTable table,
                      JavaNode contributor) {
        assert sym != null : "Null symbol";
        assert table != null : "Null symbol table";
        assert contributor != null : "Null contributor node";
        this.symbolTable = table;
        this.contributor = contributor;
        this.sym = sym;
    }


    @NonNull
    @Override
    public T getResult() {
        return sym;
    }


    static @Nullable <T> ResolveResult<T> failed() {
        return null;
    }


    static class VarResolveResult extends ResolveResultImpl<JVariableSymbol> {

        VarResolveResult(JVariableSymbol sym, AbstractSymbolTable symbolTable, JavaNode contributor) {
            super(sym, symbolTable, contributor);
        }

    }


    static class ClassResolveResult extends ResolveResultImpl<JTypeDeclSymbol> {

        ClassResolveResult(JTypeDeclSymbol sym, AbstractSymbolTable symbolTable, JavaNode contributor) {
            super(sym, symbolTable, contributor);
        }

    }


}
