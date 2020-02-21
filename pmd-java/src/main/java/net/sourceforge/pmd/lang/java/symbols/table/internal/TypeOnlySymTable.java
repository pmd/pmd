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
final class TypeOnlySymTable extends AbstractSymbolTable {

    private final Map<String, ResolveResult<JTypeDeclSymbol>> typeResults;


    private TypeOnlySymTable(JSymbolTable parent,
                             SymbolTableHelper helper,
                             NodeStream<ASTAnyTypeDeclaration> stream) {
        super(parent, helper);
        this.typeResults = stream.collect(typeDeclCollector());
    }

    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        return typeResults.get(simpleName);
    }

    @Override
    boolean isPrunable() {
        return typeResults.isEmpty();
    }

    static TypeOnlySymTable forFile(JSymbolTable parent, SymbolTableHelper helper, ASTCompilationUnit node) {
        return new TypeOnlySymTable(parent, helper, node.getTypeDeclarations());
    }

    static TypeOnlySymTable forNestedClasses(JSymbolTable parent, SymbolTableHelper helper, ASTAnyTypeDeclaration node) {
        NodeStream<ASTAnyTypeDeclaration> nestedTypes = node.getDeclarations()
                                                            .map(ASTAnyTypeBodyDeclaration::getDeclarationNode)
                                                            .filterIs(ASTAnyTypeDeclaration.class);
        return new TypeOnlySymTable(parent, helper, nestedTypes);
    }

    static TypeOnlySymTable forSelfType(JSymbolTable parent, SymbolTableHelper helper, ASTAnyTypeDeclaration node) {
        return new TypeOnlySymTable(parent, helper, NodeStream.of(node));
    }
}
