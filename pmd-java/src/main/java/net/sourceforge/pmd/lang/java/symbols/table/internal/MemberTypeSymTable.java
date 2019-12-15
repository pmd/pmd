/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JValueSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;

/**
 * Member types of a type declaration, also types declared in a
 * compilation unit.
 */
final class MemberTypeSymTable extends AbstractSymbolTable {

    private static final Logger LOG = Logger.getLogger(MemberTypeSymTable.class.getName());
    private final Map<String, JClassSymbol> map;


    public MemberTypeSymTable(JSymbolTable parent,
                              SymbolTableResolveHelper helper,
                              ASTAnyTypeDeclaration node) {
        super(parent, helper);
        assert node != null : "Null type decl?";
        map = node.getDeclarations().stream()
                  .flatMap(it -> it.getLastChild() instanceof ASTAnyTypeDeclaration
                                 ? Stream.of((ASTAnyTypeDeclaration) it.getLastChild())
                                 : Stream.empty())
                  .map(ASTAnyTypeDeclaration::getSymbol)
                  .collect(Collectors.toMap(JTypeDeclSymbol::getSimpleName, it -> it));
    }

    public MemberTypeSymTable(JSymbolTable parent,
                              SymbolTableResolveHelper helper,
                              ASTCompilationUnit node) {
        super(parent, helper);
        assert node != null : "Null type decl?";
        map = node.getTypeDeclarations()
                  .stream()
                  .map(ASTAnyTypeDeclaration::getSymbol)
                  .collect(Collectors.toMap(JTypeDeclSymbol::getSimpleName, it -> it));
    }


    @Override
    protected Stream<JMethodSymbol> resolveMethodNameImpl(String simpleName) {
        return Stream.empty();
    }

    @Override
    protected @Nullable JTypeDeclSymbol resolveTypeNameImpl(String simpleName) {
        return map.get(simpleName);
    }

    @Override
    protected @Nullable JValueSymbol resolveValueNameImpl(String simpleName) {
        return null;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    boolean isPrunable() {
        return map.isEmpty();
    }
}
