/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * Defines a single component of a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 14 preview feature).
 *
 * <pre class="grammar">
 *
 * RecordComponent ::= ({@linkplain ASTAnnotation Annotation})*
 *                     {@linkplain ASTType Type}
 *                     ( "..." )?
 *                     {@linkplain ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 */
@Experimental
public final class ASTRecordComponent extends AbstractJavaAnnotatableNode {
    private boolean varargs;

    ASTRecordComponent(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean isVarargs() {
        return varargs;
    }

    void setVarargs() {
        varargs = true;
    }

    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }

    public ASTVariableDeclaratorId getVarId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
    }
}
