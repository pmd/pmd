/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * Formal parameter of a {@linkplain ASTCatchClause catch clause}
 * to represent the declared exception variable.
 *
 * <pre class="grammar">
 *
 * CatchParameter ::= {@link ASTModifierList LocalVarModifierList} {@link ASTType Type} {@link ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 */
public final class ASTCatchParameter extends AbstractJavaNode
    implements InternalInterfaces.VariableIdOwner,
               FinalizableNode {

    ASTCatchParameter(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
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
        return getVarId().getName();
    }


    /**
     * Returns the type node of this catch parameter. May be a
     * {@link ASTUnionType UnionType}.
     */
    public ASTType getTypeNode() {
        return (ASTType) getChild(1);
    }


    /**
     * Returns a stream of all declared exception types (expanding a union
     * type if present).
     */
    public NodeStream<ASTClassOrInterfaceType> getAllExceptionTypes() {
        ASTType typeNode = getTypeNode();
        if (typeNode instanceof ASTUnionType) {
            return typeNode.children(ASTClassOrInterfaceType.class);
        } else {
            return NodeStream.of((ASTClassOrInterfaceType) typeNode);
        }
    }

}
