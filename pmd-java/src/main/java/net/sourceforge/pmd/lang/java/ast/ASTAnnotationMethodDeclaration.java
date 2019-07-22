/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class ASTAnnotationMethodDeclaration extends AbstractMethodOrConstructorDeclaration {

    ASTAnnotationMethodDeclaration(int id) {
        super(id);
    }

    ASTAnnotationMethodDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public String getName() {
        return getImage();
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

    /**
     * Returns the default clause.
     */
    @NonNull
    public ASTDefaultValue getDefaultClause() {
        return getFirstChildOfType(ASTDefaultValue.class);
    }

    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.METHOD;
    }
}
