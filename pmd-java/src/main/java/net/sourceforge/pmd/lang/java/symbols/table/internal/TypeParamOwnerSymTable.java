/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.ast.TypeParamOwnerNode;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.ClassResolveResult;


/**
 * Sym table for a type parameter section. Type params are in scope
 * through the type param section itself, and the whole construct (modulo
 * shadowing).
 */
final class TypeParamOwnerSymTable extends AbstractSymbolTable {

    private final Map<String, ResolveResult<JTypeDeclSymbol>> scope;


    public TypeParamOwnerSymTable(JSymbolTable parent,
                                  SymbolTableHelper helper,
                                  TypeParamOwnerNode node) {
        super(parent, helper);
        this.scope = new HashMap<>();
        for (ASTTypeParameter tparam : ASTList.orEmpty(node.getTypeParameters())) {
            scope.put(tparam.getParameterName(), new ClassResolveResult(tparam.getSymbol(), this, tparam));
        }

    }

    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        return scope.get(simpleName);
    }

}
