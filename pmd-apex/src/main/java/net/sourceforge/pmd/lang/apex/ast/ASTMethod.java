/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.member.Method;

public final class ASTMethod extends AbstractApexNode<Method> implements ApexQualifiableNode {

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
