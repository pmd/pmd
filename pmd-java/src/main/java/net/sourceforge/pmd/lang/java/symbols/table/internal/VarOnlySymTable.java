/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordComponent;
import net.sourceforge.pmd.lang.java.ast.ASTRecordConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ResolveResultImpl.VarResolveResult;

/**
 * Member types of a type declaration, also types declared in a
 * compilation unit.
 */
final class VarOnlySymTable extends AbstractSymbolTable {

    private final Map<String, ResolveResult<JVariableSymbol>> varResults;

    VarOnlySymTable(JSymbolTable parent,
                    SymbolTableHelper helper,
                    NodeStream<ASTVariableDeclaratorId> stream) {
        super(parent, helper);
        this.varResults = stream.collect(varIdCollector());
    }

    /**
     * For formal parameters of a record constructor.
     */
    VarOnlySymTable(JSymbolTable parent,
                    SymbolTableHelper helper,
                    ASTRecordConstructorDeclaration recordCtor) {
        super(parent, helper);

        List<JFormalParamSymbol> formals = recordCtor.getSymbol().getFormalParameters();
        List<ASTRecordComponent> components = recordCtor.getEnclosingType().getRecordComponentList().toList();
        assert components.size() == formals.size()
            : "Mismatched formals " + formals + " for record components " + components;

        this.varResults = new HashMap<>(formals.size());
        for (int i = 0; i < formals.size(); i++) {
            JFormalParamSymbol formalSym = formals.get(i);
            ASTVariableDeclaratorId comp = components.get(i).getVarId();

            assert comp.getVariableName().equals(formalSym.getSimpleName());

            varResults.put(formalSym.getSimpleName(), new VarResolveResult(formalSym, this, comp));
        }
    }

    VarOnlySymTable(JSymbolTable parent,
                    SymbolTableHelper helper,
                    ASTVariableDeclaratorId varId) {
        super(parent, helper);
        this.varResults = Collections.singletonMap(varId.getVariableName(), makeResult(varId));
    }

    @Override
    protected @Nullable ResolveResult<JVariableSymbol> resolveValueNameImpl(String simpleName) {
        return varResults.get(simpleName);
    }

    @Override
    boolean isPrunable() {
        return varResults.isEmpty();
    }


    @NonNull
    protected Collector<@NonNull ASTVariableDeclaratorId, ?, Map<String, ResolveResult<JVariableSymbol>>> varIdCollector() {
        return Collectors.toMap(ASTVariableDeclaratorId::getVariableName, this::makeResult);
    }

    @NonNull
    protected ResolveResultImpl<JVariableSymbol> makeResult(@NonNull ASTVariableDeclaratorId it) {
        return new VarResolveResult(it.getSymbol(), this, it);
    }

    static NodeStream<ASTVariableDeclaratorId> varsOfInit(ASTForStatement node) {
        return NodeStream.of(node.getInit())
                         .filterIs(ASTLocalVariableDeclaration.class)
                         .flatMap(ASTLocalVariableDeclaration::getVarIds);
    }

    static NodeStream<ASTVariableDeclaratorId> formalsOf(ASTLambdaExpression node) {
        return node.getParameters().toStream().map(ASTLambdaParameter::getVarId);
    }

    static List<JFormalParamSymbol> formalsOf(ASTRecordConstructorDeclaration node) {
        JConstructorSymbol symbol = node.getSymbol();
        assert symbol != null : "Null symbol for " + node;
        return symbol.getFormalParameters();
    }

    static NodeStream<ASTVariableDeclaratorId> formalsOf(ASTMethodOrConstructorDeclaration node) {
        return node.getFormalParameters().toStream().map(ASTFormalParameter::getVarId);
    }
}
