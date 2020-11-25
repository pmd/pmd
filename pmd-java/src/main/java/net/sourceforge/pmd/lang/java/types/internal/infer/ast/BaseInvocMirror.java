/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.util.CollectionUtil;

abstract class BaseInvocMirror<T extends InvocationNode> extends BasePolyMirror<T> implements InvocationMirror {

    private MethodCtDecl ctDecl;
    private List<ExprMirror> args;

    BaseInvocMirror(JavaExprMirrors mirrors, T call) {
        super(mirrors, call);
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
            this.args = CollectionUtil.map(ASTList.orEmpty(args), factory::getMirror);
        }
        return args;
    }

    @Override
    public int getArgumentCount() {
        return ASTList.sizeOrZero(myNode.getArguments());
    }

    @Override
    public void setMethodType(MethodCtDecl methodType) {
        if (factory.mayMutateAst()) {
            InternalApiBridge.setOverload(myNode, methodType);
        }
        ctDecl = methodType;
    }


    @Override
    public @Nullable MethodCtDecl getMethodType() {
        return ctDecl;
    }

    @Override
    public @Nullable JTypeMirror getReceiverType() {
        return null;
    }
}
