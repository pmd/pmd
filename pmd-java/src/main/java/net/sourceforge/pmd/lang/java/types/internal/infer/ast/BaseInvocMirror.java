/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.MethodCallSite;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors.MirrorMaker;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.CollectionUtil;

abstract class BaseInvocMirror<T extends InvocationNode> extends BasePolyMirror<T> implements InvocationMirror {

    private MethodCtDecl ctDecl;
    private List<ExprMirror> args;

    BaseInvocMirror(JavaExprMirrors mirrors, T call, @Nullable ExprMirror parent, MirrorMaker subexprMaker) {
        super(mirrors, call, parent, subexprMaker);
    }

    @Override
    public boolean isEquivalentToUnderlyingAst() {
        MethodCtDecl ctDecl = getCtDecl();
        AssertionUtil.validateState(ctDecl != null, "overload resolution is not complete");
        if (ctDecl.isFailed()) {
            return false; // be conservative
        }
        if (!myNode.getMethodType().getSymbol().equals(ctDecl.getMethodType().getSymbol())) {
            return false;
        } else if (myNode instanceof ASTConstructorCall && ((ASTConstructorCall) myNode).isAnonymousClass()
            && !((ASTConstructorCall) myNode).getTypeNode().getTypeMirror().equals(getInferredType())) {
            // check anon class has same type args
            return false;
        } else if (myNode.getParent() instanceof ASTVariableDeclarator) {
            ASTVariableDeclaratorId varId = ((ASTVariableDeclarator) myNode.getParent()).getVarId();
            if (varId.isTypeInferred() && !getInferredType().equals(varId.getTypeMirror())) {
                return false;
            }
        }

        return CollectionUtil.all(this.getArgumentExpressions(), ExprMirror::isEquivalentToUnderlyingAst);
    }

    protected MethodCtDecl getStandaloneCtdecl() {
        MethodCallSite site = factory.infer.newCallSite(this, null);
        // this is cached for later anyway
        return factory.infer.getCompileTimeDecl(site);
    }

    @Override
    public List<JTypeMirror> getExplicitTypeArguments() {
        return ASTList.orEmptyStream(myNode.getExplicitTypeArguments())
                      .toStream()
                      .map(TypeNode::getTypeMirror)
                      .collect(Collectors.toList());
    }

    @Override
    public JavaNode getExplicitTargLoc(int i) {
        return ASTList.orEmptyStream(myNode.getExplicitTypeArguments()).get(i);
    }

    @Override
    public List<ExprMirror> getArgumentExpressions() {
        if (this.args == null) {
            ASTArgumentList args = myNode.getArguments();
            this.args = CollectionUtil.map(ASTList.orEmpty(args), this::createSubexpression);
        }
        return args;
    }

    @Override
    public int getArgumentCount() {
        return ASTList.sizeOrZero(myNode.getArguments());
    }

    @Override
    public void setCtDecl(MethodCtDecl methodType) {
        ctDecl = methodType;
        if (mayMutateAst()) {
            InternalApiBridge.setOverload(myNode, methodType);
        }
    }


    @Override
    public @Nullable MethodCtDecl getCtDecl() {
        return ctDecl;
    }

    @Override
    public @Nullable JTypeMirror getReceiverType() {
        return null;
    }
}
