/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;


/**
 * Formal parameter of a {@linkplain ASTCatchClause catch clause}
 * to represent the declared exception variable.
 *
 * <pre class="grammar">
 *
 * CatchParameter ::= ( "final" | {@link ASTAnnotation Annotation} )* {@link ASTType Type} {@link ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 */
public final class ASTCatchParameter extends AbstractJavaAccessNode implements InternalInterfaces.VariableIdOwner {

    ASTCatchParameter(int id) {
        super(id);
    }

    ASTCatchParameter(JavaParser p, int id) {
        super(p, id);
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
     * Returns true if this is a multi-catch parameter,
     * that is, it catches several unrelated exception types
     * at the same time. For example:
     *
     * <pre>catch (IllegalStateException | IllegalArgumentException e) {}</pre>
     */
    public boolean isMulticatch() {
        return getTypeNode() instanceof ASTUnionType;
    }

    @Override
    @NonNull
    public ASTVariableDeclaratorId getVarId() {
        return (ASTVariableDeclaratorId) getLastChild();
    }

    /** Returns the name of this parameter. */
    public String getName() {
        return getVarId().getVariableName();
    }


    /**
     * Returns the type node of this catch parameter. May be a
     * {@link ASTUnionType UnionType}.
     */
    public ASTType getTypeNode() {
        return children(ASTType.class).first();
    }

}
