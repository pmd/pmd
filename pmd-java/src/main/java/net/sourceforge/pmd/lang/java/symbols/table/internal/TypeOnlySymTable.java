/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTLocalClassStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.ClassResolveResult;

/**
 * Member types of a type declaration, also types declared in a
 * compilation unit.
 */
final class TypeOnlySymTable extends AbstractSymbolTable {

    private final Map<String, ResolveResult<JTypeDeclSymbol>> typeResults;


    TypeOnlySymTable(JSymbolTable parent,
                     SymbolTableHelper helper,
                     NodeStream<ASTAnyTypeDeclaration> stream) {
        super(parent, helper);
        this.typeResults = stream.collect(typeDeclCollector());
    }

    TypeOnlySymTable(JSymbolTable parent,
                     SymbolTableHelper helper,
                     ASTAnyTypeDeclaration node) {
        super(parent, helper);
        this.typeResults = Collections.singletonMap(node.getSimpleName(), makeResult(node));
    }

    /** Type parameters of a class. */
    TypeOnlySymTable(JSymbolTable parent,
                     SymbolTableHelper helper,
                     @Nullable ASTTypeParameters parameterList) {
        super(parent, helper);
        this.typeResults = new HashMap<>();
        for (ASTTypeParameter tparam : ASTList.orEmpty(parameterList)) {
            typeResults.put(tparam.getParameterName(), new ClassResolveResult(tparam.getSymbol(), this, tparam));
        }
    }

    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        return typeResults.get(simpleName);
    }

    @Override
    boolean isPrunable() {
        return typeResults.isEmpty();
    }


    @NonNull
    protected Collector<ASTAnyTypeDeclaration, ?, Map<String, ResolveResult<JTypeDeclSymbol>>> typeDeclCollector() {
        return Collectors.toMap(ASTAnyTypeDeclaration::getSimpleName, this::makeResult);
    }

    @NonNull
    protected ClassResolveResult makeResult(ASTAnyTypeDeclaration it) {
        return new ClassResolveResult(it.getSymbol(), this, it);
    }

    static NodeStream<ASTAnyTypeDeclaration> nestedClassesOf(ASTAnyTypeDeclaration node) {
        return node.getDeclarations()
                   .map(ASTAnyTypeBodyDeclaration::getDeclarationNode)
                   .filterIs(ASTAnyTypeDeclaration.class);
    }

    static NodeStream<ASTAnyTypeDeclaration> localClassesOf(ASTBlock node) {
        return node.children(ASTLocalClassStatement.class).map(ASTLocalClassStatement::getDeclaration);
    }
}
