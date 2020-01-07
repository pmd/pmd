/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


/**
 * Formal parameter node. Used in the {@link ASTFormalParameters}
 * production of {@link ASTMethodDeclarator} to represent a
 * method's formal parameter.
 *
 * <pre class="grammar">
 *
 * FormalParameter ::= ( "final" | {@link ASTAnnotation Annotation} )* {@link ASTType Type} [ "..." ] {@link ASTVariableDeclaratorId VariableDeclaratorId}
 * </pre>
 */
public final class ASTFormalParameter extends AbstractJavaAccessTypeNode implements Dimensionable, Annotatable {

    private boolean isVarargs;

    @InternalApi
    @Deprecated
    public ASTFormalParameter(int id) {
        super(id);
    }

    ASTFormalParameter(JavaParser p, int id) {
        super(p, id);
    }


    void setVarargs() {
        isVarargs = true;
    }


    /**
     * Returns true if this node is a varargs parameter.
     */
    public boolean isVarargs() {
        return isVarargs;
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
     * Returns the declarator ID of this formal parameter.
     */
    public ASTVariableDeclaratorId getVariableDeclaratorId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
    }


    /**
     * Returns true if this formal parameter is of an array type.
     * This includes varargs parameters.
     */
    @Override
    @Deprecated
    public boolean isArray() {
        return isVarargs()
                || getTypeNode() != null && getTypeNode().isArrayType()
                || getVariableDeclaratorId().isArray();
    }

    @Override
    @Deprecated
    public int getArrayDepth() {
        if (!isArray()) {
            return 0;
        }
        return getTypeNode().getArrayDepth() + getVariableDeclaratorId().getArrayDepth() + (isVarargs() ? 1 : 0);
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


    /**
     * Returns the type of this formal parameter. That type
     * is exactly that of the variable declarator id,
     * which means that the declarator id's type takes into
     * account whether this parameter is varargs or not.
     */
    @Override
    public Class<?> getType() {
        return getVariableDeclaratorId().getType();
    }


    @Override
    public JavaTypeDefinition getTypeDefinition() {
        return getVariableDeclaratorId().getTypeDefinition();
    }


    /**
     * Noop, the type of this node is defined by the type
     * of the declarator id.
     */
    @InternalApi
    @Deprecated
    @Override
    public void setTypeDefinition(JavaTypeDefinition type) {
        // see javadoc
    }

}
