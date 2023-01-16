/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReceiverParameter;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstExecSymbol<T extends ASTMethodOrConstructorDeclaration>
    extends AbstractAstTParamOwner<T>
    implements JExecutableSymbol {

    private final JClassSymbol owner;
    private final List<JFormalParamSymbol> formals;

    protected AbstractAstExecSymbol(T node, AstSymFactory factory, JClassSymbol owner) {
        super(node, factory);
        this.owner = owner;

        this.formals = CollectionUtil.map(
            node.getFormalParameters(),
            p -> new AstFormalParamSym(p.getVarId(), factory, this)
        );
    }

    @Override
    public List<JFormalParamSymbol> getFormalParameters() {
        return formals;
    }


    @Override
    public List<JTypeMirror> getFormalParameterTypes(Substitution subst) {
        return CollectionUtil.map(getFormalParameters(), i -> i.getTypeMirror(subst));
    }

    @Override
    public List<JTypeMirror> getThrownExceptionTypes(Substitution subst) {
        return CollectionUtil.map(
            ASTList.orEmpty(node.getThrowsList()),
            t -> t.getTypeMirror().subst(subst)
        );
    }

    @Override
    public @Nullable JTypeMirror getAnnotatedReceiverType(Substitution subst) {
        if (!this.hasReceiver()) {
            return null;
        }
        ASTReceiverParameter receiver = node.getFormalParameters().getReceiverParameter();
        if (receiver == null) {
            return getTypeSystem().declaration(getEnclosingClass()).subst(subst);
        }
        return receiver.getReceiverType().getTypeMirror().subst(subst);
    }

    @Override
    public @NonNull JClassSymbol getEnclosingClass() {
        return owner;
    }


    @Override
    public boolean isVarargs() {
        return node.isVarargs();
    }

    @Override
    public int getArity() {
        return formals.size();
    }

}
