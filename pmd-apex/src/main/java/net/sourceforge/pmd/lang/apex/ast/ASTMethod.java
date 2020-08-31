/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSignature;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.SignedNode;

import apex.jorje.semantic.ast.member.Method;

public final class ASTMethod extends AbstractApexNode<Method> implements ApexQualifiableNode,
                                                                         SignedNode<ASTMethod>, Node {

    ASTMethod(Method method) {
        super(method);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getMethodInfo().getName();
    }

    public String getCanonicalName() {
        return node.getMethodInfo().getCanonicalName();
    }

    @Override
    public ApexQualifiedName getQualifiedName() {
        return ApexQualifiedName.ofMethod(this);
    }


    @Override
    public ApexOperationSignature getSignature() {
        return ApexOperationSignature.of(this);
    }

    /**
     * Returns true if this is a synthetic class initializer, inserted
     * by the parser.
     */
    public boolean isSynthetic() {
        return getImage().matches("<clinit>|<init>|clone");
    }

    public boolean isConstructor() {
        return node.getMethodInfo().isConstructor();
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getReturnType() {
        return node.getMethodInfo().getEmitSignature().getReturnType().getApexName();
    }

    public int getArity() {
        return node.getMethodInfo().getParameterTypes().size();
    }
}
