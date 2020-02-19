/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;

/**
 * Member types of a type declaration, also types declared in a
 * compilation unit.
 */
final class SelfTypeSymTable extends AbstractSymbolTable {

    private final ResolveResult<JTypeDeclSymbol> decl;
    private final String name;


    SelfTypeSymTable(JSymbolTable parent,
                     SymbolTableHelper helper,
                     ASTAnyTypeDeclaration node) {
        super(parent, helper);
        assert node != null : "Null type decl?";
        name = node.getSimpleName();
        decl = makeResult(node);
    }

    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        return simpleName.equals(name) ? decl : null;
    }

}
