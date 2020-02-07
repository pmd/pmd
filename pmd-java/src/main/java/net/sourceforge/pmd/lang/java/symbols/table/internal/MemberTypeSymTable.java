/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;

/**
 * Member types of a type declaration, also types declared in a
 * compilation unit.
 */
final class MemberTypeSymTable extends AbstractSymbolTable {

    private final Map<String, ResolveResult<JTypeDeclSymbol>> map;


    public MemberTypeSymTable(JSymbolTable parent,
                              SymbolTableHelper helper,
                              ASTAnyTypeDeclaration node) {
        super(parent, helper);
        assert node != null : "Null type decl?";
        map = NodeStream.fromIterable(node.getDeclarations())
                        .map(ASTAnyTypeBodyDeclaration::getDeclarationNode)
                        .filterIs(ASTAnyTypeDeclaration.class)
                        .collect(typeDeclCollector());
    }

    public MemberTypeSymTable(JSymbolTable parent,
                              SymbolTableHelper helper,
                              ASTCompilationUnit node) {
        super(parent, helper);
        assert node != null : "Null type decl?";
        map = node.getTypeDeclarations()
                  .stream()
                  .collect(typeDeclCollector());
    }

    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        return map.get(simpleName);
    }

    @Override
    boolean isPrunable() {
        return map.isEmpty();
    }
}
