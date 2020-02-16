/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

// @formatter:off
/**
 * Represents an identifier in the context of variable or parameter declarations (not their use in
 * expressions). Such a node declares a name in the scope it's defined in, and can occur in the following
 * contexts:
 *
 * <ul>
 *    <li> Field declarations;
 *    <li> Local variable declarations;
 *    <li> Method, constructor and lambda parameter declarations;
 *    <li> Method and constructor explicit receiver parameter declarations;
 *    <li> Exception parameter declarations occurring in catch clauses;
 *    <li> Resource declarations occurring in try-with-resources statements.
 * </ul>
 *
 * <p>Since this node conventionally represents the declared variable in PMD, our symbol table
 * populates it with a {@link VariableNameDeclaration}, and its usages can be accessed through
 * the method {@link #getUsages()}.
 *
 * <p>Type resolution assigns the type of the variable to this node. See {@link #getType()}'s
 * documentation for the contract of this method.
 */
// @formatter:on
public class ASTVariableDeclaratorId extends AbstractJavaTypeNode implements Dimensionable {

    private int arrayDepth;
    private VariableNameDeclaration nameDeclaration;
    private boolean explicitReceiverParameter = false;

    @InternalApi
    @Deprecated
    public ASTVariableDeclaratorId(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTVariableDeclaratorId(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public VariableNameDeclaration getNameDeclaration() {
        return nameDeclaration;
    }

    @InternalApi
    @Deprecated
    public void setNameDeclaration(VariableNameDeclaration decl) {
        nameDeclaration = decl;
    }

    public List<NameOccurrence> getUsages() {
        return getScope().getDeclarations(VariableNameDeclaration.class).get(nameDeclaration);
    }

    @Deprecated
    public void bumpArrayDepth() {
        arrayDepth++;
    }

    @Override
    @Deprecated
    public int getArrayDepth() {
        return arrayDepth;
    }


    /**
     * Returns true if the declared variable has an array type.
     *
     * @deprecated Use {@link #hasArrayType()}
     */
    @Override
    @Deprecated
    public boolean isArray() {
        return arrayDepth > 0;
    }


    /**
     * Returns true if the declared variable has an array type.
     */
    public boolean hasArrayType() {
        return arrayDepth > 0 || !isTypeInferred() && getTypeNode().isArrayType();
    }


    /**
     * Returns true if this nodes declares an exception parameter in
     * a {@code catch} statement.
     */
    public boolean isExceptionBlockParameter() {
        return getParent().getParent() instanceof ASTCatchStatement;
    }


    /**
     * Returns true if this node declares a formal parameter for a method
     * declaration or a lambda expression. In particular, returns false
     * if the node is a receiver parameter (see {@link #isExplicitReceiverParameter()}).
     */
    public boolean isFormalParameter() {
        return getParent() instanceof ASTFormalParameter && !isExceptionBlockParameter() && !isResourceDeclaration()
            || isLambdaParamWithNoType();
    }


    /**
     * Returns true if this node declares a local variable.
     */
    public boolean isLocalVariable() {
        return getNthParent(2) instanceof ASTLocalVariableDeclaration;
    }


    /**
     * Returns true if this node declares a formal parameter for
     * a lambda expression. In that case, the type of this parameter
     * is not necessarily inferred, see {@link #isTypeInferred()}.
     */
    public boolean isLambdaParameter() {
        return isLambdaParamWithNoType()
            || getParent() instanceof ASTFormalParameter && getNthParent(3) instanceof ASTLambdaExpression;
    }


    private boolean isLambdaParamWithNoType() {
        return getParent() instanceof ASTLambdaExpression;
    }


    /**
     * Returns true if this node declares a field.
     */
    public boolean isField() {
        return getNthParent(2) instanceof ASTFieldDeclaration;
    }


    /**
     * Returns the name of the variable.
     */
    public String getVariableName() {
        return getImage();
    }


    /**
     * Returns true if the variable declared by this node is declared final.
     * Doesn't account for the "effectively-final" nuance. Resource
     * declarations are implicitly final.
     */
    public boolean isFinal() {
        if (isResourceDeclaration()) {
            // this is implicit even if "final" is not explicitly declared.
            return true;
        } else if (isLambdaParamWithNoType()) {
            return false;
        }

        if (getParent() instanceof ASTFormalParameter) {
            // This accounts for exception parameters too for now
            return ((ASTFormalParameter) getParent()).isFinal();
        }

        Node grandpa = getNthParent(2);

        if (grandpa instanceof ASTLocalVariableDeclaration) {
            return ((ASTLocalVariableDeclaration) grandpa).isFinal();
        } else if (grandpa instanceof ASTFieldDeclaration) {
            return ((ASTFieldDeclaration) grandpa).isFinal();
        }

        throw new IllegalStateException("All cases should be handled");
    }


    /**
     * @deprecated Will be made private with 7.0.0
     */
    @InternalApi
    @Deprecated
    public void setExplicitReceiverParameter() {
        explicitReceiverParameter = true;
    }


    /**
     * Returns true if this node is a receiver parameter for a method or constructor
     * declaration. The receiver parameter has the name {@code this}, and must be declared
     * at the beginning of the parameter list. Its only purpose is to annotate
     * the type of the object on which the method call is issued. It was introduced
     * in Java 8.
     */
    public boolean isExplicitReceiverParameter() {
        // TODO this could be inferred from the image tbh
        return explicitReceiverParameter;
    }


    /**
     * Returns true if this declarator id declares a resource in a try-with-resources statement.
     */
    public boolean isResourceDeclaration() {
        return getParent() instanceof ASTResource;
    }


    /**
     * Returns true if the declared variable's type is inferred by
     * the compiler. In Java 8, this can happen if it's in a formal
     * parameter of a lambda with an inferred type (e.g. {@code (a, b) -> a + b}).
     * Since Java 10, the type of local variables can be inferred
     * too, e.g. {@code var i = 2;}.
     *
     * <p>This method returns true for declarator IDs in those contexts,
     * in which case {@link #getTypeNode()} returns {@code null},
     * since the type node is absent.
     */
    public boolean isTypeInferred() {
        return isLambdaParamWithNoType() || isLocalVariableTypeInferred() || isLambdaTypeInferred();
    }


    private boolean isLocalVariableTypeInferred() {
        if (isResourceDeclaration()) {
            // covers "var" in try-with-resources
            return getParent().getFirstChildOfType(ASTType.class) == null;
        } else if (getNthParent(2) instanceof ASTLocalVariableDeclaration) {
            // covers "var" as local variables and in for statements
            return getNthParent(2).getFirstChildOfType(ASTType.class) == null;
        }

        return false;
    }

    private boolean isLambdaTypeInferred() {
        return getNthParent(3) instanceof ASTLambdaExpression
            && getParent().getFirstChildOfType(ASTType.class) == null;
    }

    /**
     * Returns the first child of the node returned by {@link #getTypeNode()}.
     * The image of that node can usually be interpreted as the image of the
     * type.
     */
    // TODO unreliable, not typesafe and not useful, should be deprecated
    public Node getTypeNameNode() {
        ASTType type = getTypeNode();
        return type == null ? null : getTypeNode().getChild(0);
    }


    /**
     * Determines the type node of this variable id, that is, the type node
     * belonging to the variable declaration of this node (either a
     * FormalParameter, LocalVariableDeclaration or FieldDeclaration).
     *
     * <p>The type of the returned node is not necessarily the type of this
     * node. See {@link #getType()} for an explanation.
     *
     * @return the type node, or {@code null} if there is no explicit type,
     *     e.g. if {@link #isTypeInferred()} returns true.
     */
    public ASTType getTypeNode() {
        if (getParent() instanceof ASTFormalParameter) {
            // ASTResource is a subclass of ASTFormal parameter for now but this will change
            // and this will need to be corrected here, see #998
            return ((ASTFormalParameter) getParent()).getTypeNode();
        } else if (isTypeInferred()) {
            // lambda expression with lax types. The type is inferred...
            return null;
        } else {
            Node n = getParent().getParent();
            if (n instanceof ASTLocalVariableDeclaration || n instanceof ASTFieldDeclaration) {
                return n.getFirstChildOfType(ASTType.class);
            }
        }
        return null;
    }

    // @formatter:off
    /**
     * Returns the type of the declared variable. The type of a declarator ID is
     * <ul>
     *   <li>1. not necessarily the same as the type written out at the
     *          start of the declaration, e.g. {@code int a[];}
     *   <li>2. not necessarily the same as the types of other variables
     *          declared in the same statement, e.g. {@code int a[], b;}.
     * </ul>
     *
     * <p>These are consequences of Java's allowing programmers to
     * declare additional pairs of brackets on declarator ids. The type
     * of the node returned by {@link #getTypeNode()} doesn't take into
     * account those additional array dimensions, whereas this node's
     * type takes into account the total number of dimensions, i.e.
     * those declared on this node plus those declared on the type node.
     *
     * <p>The returned type also takes into account whether this variable
     * is a varargs formal parameter.
     *
     * <p>The type of the declarator ID is thus always the real type of
     * the variable.
     */
    // @formatter:on
    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public Class<?> getType() {
        return super.getType();
    }
}
