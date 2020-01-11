/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JValueSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;

abstract class ResolveResultImpl<T> implements ResolveResult<T> {


    @SuppressWarnings( {"rawtypes"})
    private static final ResolveResult UNRESOLVED =
        new ResolveResult() {
            @Override
            public @Nullable JSymbolTable getSymbolTable() {
                return null;
            }

            @Nullable
            @Override
            public Object getResult() {
                return null;
            }

            @Override
            public JavaNode getContributor() {
                return null;
            }
        };

    protected final AbstractSymbolTable symbolTable;
    private final JavaNode contributor;
    private final T sym;

    public ResolveResultImpl(T sym,
                             AbstractSymbolTable symbolTable,
                             JavaNode contributor) {
        this.symbolTable = symbolTable;
        this.contributor = contributor;
        this.sym = sym;
    }


    @Override
    public T getResult() {
        return sym;
    }

    @Override
    public JavaNode getContributor() {
        return contributor;
    }


    @Override
    public JSymbolTable getSymbolTable() {
        return symbolTable;
    }


    @SuppressWarnings("unchecked")
    static <T> ResolveResult<T> failed() {
        return (ResolveResult<T>) UNRESOLVED;
    }


    static class ValueResolveResult extends ResolveResultImpl<JValueSymbol> {

        ValueResolveResult(JValueSymbol sym, AbstractSymbolTable symbolTable, JavaNode contributor) {
            super(sym, symbolTable, contributor);
        }

    }


    static class ClassResolveResult extends ResolveResultImpl<JTypeDeclSymbol> {

        ClassResolveResult(JTypeDeclSymbol sym, AbstractSymbolTable symbolTable, JavaNode contributor) {
            super(sym, symbolTable, contributor);
        }

    }


}
