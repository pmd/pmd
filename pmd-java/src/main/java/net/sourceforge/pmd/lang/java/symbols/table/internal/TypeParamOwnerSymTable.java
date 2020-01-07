/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.logging.Logger;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JValueSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


final class TypeParamOwnerSymTable extends AbstractSymbolTable {

    private static final Logger LOG = Logger.getLogger(TypeParamOwnerSymTable.class.getName());
    private final JTypeParameterOwnerSymbol symbol;


    public TypeParamOwnerSymTable(JSymbolTable parent,
                                  SymbolTableResolveHelper helper,
                                  JTypeParameterOwnerSymbol symbol) {
        super(parent, helper);
        assert symbol != null : "Null symbol?";
        this.symbol = symbol;
    }


    @Override
    protected Stream<JMethodSymbol> resolveMethodNameImpl(String simpleName) {
        return Stream.empty();
    }

    @Override
    protected @Nullable JTypeDeclSymbol resolveTypeNameImpl(String simpleName) {
        return symbol.getLexicalScope().get(simpleName);
    }

    @Override
    protected @Nullable JValueSymbol resolveValueNameImpl(String simpleName) {
        return null;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }


}
