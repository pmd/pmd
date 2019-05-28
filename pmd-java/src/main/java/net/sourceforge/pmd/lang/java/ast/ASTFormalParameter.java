/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


/**
 * Formal parameter node. Used in the {@link ASTFormalParameters}
 * production of {@link ASTMethodDeclarator} to represent a
 * method's formal parameter. Also used in the {@link ASTCatchStatement}
 * production to represent the declared exception variable.
 * Also used in LambdaExpressions for the LambdaParameters.
 * <pre>
 *      ( "final" | Annotation )* Type ( "|" Type )* [ "..." ] VariableDeclaratorId
 * </pre>
 */
public class ASTFormalParameter extends AbstractJavaAccessTypeNode implements Dimensionable, CanSuppressWarnings {

    private boolean isVarargs;

    @InternalApi
    @Deprecated
    public ASTFormalParameter(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTFormalParameter(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * @deprecated Will be made private in 7.0.0
     */
    @InternalApi
    @Deprecated
    public void setVarargs() {
        isVarargs = true;
    }


    /**
     * Returns true if this node is a varargs parameter.
     */
    public boolean isVarargs() {
        return isVarargs;
    }


    /**
     * Returns true if this node is the explicit receiver parameter,
     * e.g. in
     *
     * <pre>
     * class Foo {
     *   abstract void foo(@Bar Foo this);
     * }
     * </pre>
     */
    public boolean isExplicitReceiverParameter() {
        return getVariableDeclaratorId().isExplicitReceiverParameter();
    }

    /**
     * If true, this formal parameter represents one without explit types.
     * This can appear as part of a lambda expression with java11 using "var".
     *
     * @see ASTVariableDeclaratorId#isTypeInferred()
     */
    public boolean isTypeInferred() {
        return getTypeNode() == null;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the declarator ID of this formal parameter.
     */
    public ASTVariableDeclaratorId getVariableDeclaratorId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        for (ASTAnnotation a : findChildrenOfType(ASTAnnotation.class)) {
            if (a.suppresses(rule)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns true if this formal parameter is of an array type.
     * This includes varargs parameters.
     */
    @Override
    @Deprecated
    public boolean isArray() {
        return isVarargs()
            || getTypeNode() != null && getTypeNode().isArray()
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
     * @deprecated use {@link #getVariableDeclaratorId()}
     */
    @Deprecated
    protected ASTVariableDeclaratorId getDecl() {
        return getVariableDeclaratorId();
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

    /**
     * Noop, the type of this node is defined by the type
     * of the declarator id.
     */
    @InternalApi
    @Deprecated
    @Override
    public void setType(Class<?> type) {
        // see javadoc
    }
}
