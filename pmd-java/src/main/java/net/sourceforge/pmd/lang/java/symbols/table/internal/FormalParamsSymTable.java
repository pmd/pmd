/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;

/**
 * Formal parameters of a method, ctor, or lambda.
 */
final class FormalParamsSymTable extends AbstractSymbolTable {

    private final Map<String, ResolveResult<JVariableSymbol>> formals;

    FormalParamsSymTable(JSymbolTable parent,
                         SymbolTableHelper helper,
                         ASTMethodOrConstructorDeclaration node) {

        super(parent, helper);
        assert node != null : "Null block?";

        formals = node.getFormalParameters()
                      .children(ASTFormalParameter.class)
                      .map(ASTFormalParameter::getVarId)
                      .collect(varIdCollector());
    }

    FormalParamsSymTable(JSymbolTable parent,
                         SymbolTableHelper helper,
                         ASTLambdaExpression node) {

        super(parent, helper);
        assert node != null : "Null block?";

        formals = node.getParameters()
                      .children(ASTLambdaParameter.class)
                      .map(ASTLambdaParameter::getVarId)
                      .collect(varIdCollector());

    }

    @Override
    protected @Nullable ResolveResult<JVariableSymbol> resolveValueNameImpl(String simpleName) {
        return formals.get(simpleName);
    }

    @Override
    boolean isPrunable() {
        return formals.isEmpty();
    }
}
