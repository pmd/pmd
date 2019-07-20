/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Formal parameter of a {@linkplain ASTCatchStatement catch statement}.
 * The type node may be a {@link ASTUnionType union type}, which represents
 * multi-catch clauses.
 *
 * TODO warning suppression
 *
 * <pre class="grammar">
 *
 * CatchParameter ::= ( "final" | {@link ASTAnnotation Annotation} )* {@link ASTType Type} {@link ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 */
public class ASTCatchParameter extends AbstractJavaTypeNode implements Annotatable {

    private boolean isFinal;

    @InternalApi
    @Deprecated
    public ASTCatchParameter(int id) {
        super(id);
    }

    ASTCatchParameter(JavaParser p, int id) {
        super(p, id);
    }


    public boolean isFinal() {
        return isFinal;
    }

    void setFinal(boolean f) {
        isFinal = f;
    }

    public String getName() {
        return getVariableId().getVariableName();
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
     * Returns the declarator ID of this catch parameter.
     */
    public ASTVariableDeclaratorId getVariableId() {
        return (ASTVariableDeclaratorId) getLastChild();
    }

    /**
     * Returns the type node of this formal parameter. This may be
     * a {@linkplain ASTUnionType union type}.
     */
    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }

    /**
     * Returns true if this is a multi-catch node.
     */
    public boolean isMultiCatch() {
        return getTypeNode() instanceof ASTUnionType;
    }
}
