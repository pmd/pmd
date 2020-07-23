/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.MethodRefMirror;
import net.sourceforge.pmd.util.CollectionUtil;

class MethodRefMirrorImpl extends BasePolyMirror<ASTMethodReference> implements MethodRefMirror {

    private JMethodSig exactMethod;

    MethodRefMirrorImpl(JavaExprMirrors mirrors, ASTMethodReference lambda) {
        super(mirrors, lambda);
        exactMethod = mirrors.ts.UNRESOLVED_METHOD;
    }

    @Override
    public boolean isConstructorRef() {
        return myNode.isConstructorReference();
    }

    @Override
    public JTypeMirror getLhsIfType() {
        // TODO simplify when we have disambiguation
        ASTExpression lhsType = myNode.getQualifier();
        return lhsType instanceof ASTTypeExpression
                   || lhsType instanceof ASTAmbiguousName
               ? lhsType.getTypeMirror()
               : null;
    }

    @Override
    public JTypeMirror getTypeToSearch() {
        return myNode.getLhs().getTypeMirror();
    }

    @Override
    public String getMethodName() {
        if (myNode.isConstructorReference()) {
            return JConstructorSymbol.CTOR_NAME;
        } else {
            return myNode.getMethodName();
        }
    }

    @Override
    public void setFunctionalMethod(JMethodSig methodType) {
        InternalApiBridge.setFunctionalMethod(myNode, methodType);
    }

    @Override
    public void setCompileTimeDecl(JMethodSig methodType) {
        InternalApiBridge.setCompileTimeDecl(myNode, methodType);
    }

    @Override
    public List<JTypeMirror> getExplicitTypeArguments() {
        return CollectionUtil.map(
            ASTList.orEmpty(myNode.getExplicitTypeArguments()),
            TypeNode::getTypeMirror
        );
    }

    @Override
    public JMethodSig getCachedExactMethod() {
        return exactMethod;
    }

    @Override
    public void setCachedExactMethod(@Nullable JMethodSig sig) {
        exactMethod = sig;
    }
}
