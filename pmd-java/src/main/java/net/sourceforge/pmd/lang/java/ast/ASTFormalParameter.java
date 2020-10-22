/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.VariableIdOwner;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;


/**
 * Formal parameter node for a {@linkplain ASTFormalParameters formal parameter list}.
 * This is distinct from {@linkplain ASTLambdaParameter lambda parameters}.
 *
 * <p>The varargs ellipsis {@code "..."} is parsed as an {@linkplain ASTArrayTypeDim array dimension}
 * in the type node.
 *
 * <pre class="grammar">
 *
 * FormalParameter ::= {@link ASTModifierList LocalVarModifierList} {@link ASTType Type} {@link ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 */
public final class ASTFormalParameter extends AbstractJavaNode
    implements FinalizableNode,
               TypeNode,
               Annotatable,
               VariableIdOwner {

    ASTFormalParameter(int id) {
        super(id);
    }

    @Override
    public Visibility getVisibility() {
        return Visibility.V_LOCAL;
    }


    /**
     * Returns the list of formal parameters containing this param.
     */
    public ASTFormalParameters getOwnerList() {
        return (ASTFormalParameters) getParent();
    }

    /**
     * Returns true if this node is a varargs parameter. Then, the type
     * node is an {@link ASTArrayType ArrayType}, and its last dimension
     * {@linkplain ASTArrayTypeDim#isVarargs() is varargs}.
     */
    public boolean isVarargs() {
        ASTType tn = getTypeNode();
        return tn instanceof ASTArrayType
            && ((ASTArrayType) tn).getDimensions().getLastChild().isVarargs();
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the declarator ID of this formal parameter.
     */
    @Override
    @NonNull
    public ASTVariableDeclaratorId getVarId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
    }


    /**
     * Returns the type node of this formal parameter.
     * The type of that node is not necessarily the type
     * of the parameter itself, see {@link ASTVariableDeclaratorId#getType()}.
     *
     * <p>In particular, the type of the returned node
     * doesn't take into account whether this formal
     * parameter is varargs or not.
     */
    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }

    // Honestly FormalParameter shouldn't be a TypeNode.
    // The node that represents the variable is the variable ID.

    @Override
    public @NonNull JTypeMirror getTypeMirror() {
        return getVarId().getTypeMirror();
    }
}
