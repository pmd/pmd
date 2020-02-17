/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalClassStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;

/**
 * Local variables declared in a block.
 */
final class LocalSymTable extends AbstractSymbolTable {

    private final Map<String, ResolveResult<JTypeDeclSymbol>> localClasses;
    private final Map<String, ResolveResult<JVariableSymbol>> localVars;

    LocalSymTable(JSymbolTable parent,
                  SymbolTableHelper helper,
                  ASTForeachStatement node) {
        super(parent, helper);
        assert node != null : "null foreach statement?";

        localClasses = Collections.emptyMap();
        ASTVariableDeclaratorId varId = node.getVarId();
        localVars = Collections.singletonMap(varId.getVariableName(), makeResult(varId));
    }

    LocalSymTable(JSymbolTable parent,
                  SymbolTableHelper helper,
                  ASTForStatement node) {
        super(parent, helper);
        assert node != null : "null for statement?";

        localClasses = Collections.emptyMap();
        localVars = NodeStream.of(node.getInit())
                              .filterIs(ASTLocalVariableDeclaration.class)
                              .flatMap(ASTLocalVariableDeclaration::getVarIds)
                              .collect(varIdCollector());
    }

    // TODO should report CT error when self-reference detected (in initializer)
    LocalSymTable(JSymbolTable parent,
                  SymbolTableHelper helper,
                  ASTBlock node) {

        super(parent, helper);
        assert node != null : "Null block?";

        localVars = node.children(ASTLocalVariableDeclaration.class)
                        .flatMap(ASTLocalVariableDeclaration::getVarIds)
                        .collect(varIdCollector());

        localClasses = node.children(ASTLocalClassStatement.class)
                           .map(ASTLocalClassStatement::getDeclaration)
                           .collect(typeDeclCollector());
    }


    @Override
    protected @Nullable ResolveResult<JTypeDeclSymbol> resolveTypeNameImpl(String simpleName) {
        return localClasses.get(simpleName);
    }

    @Override
    protected @Nullable ResolveResult<JVariableSymbol> resolveValueNameImpl(String simpleName) {
        return localVars.get(simpleName);
    }

    @Override
    boolean isPrunable() {
        return localClasses.isEmpty() && localVars.isEmpty();
    }
}
