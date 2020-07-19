/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttribute;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

// @formatter:off
/**
 * Represents an identifier in the context of variable or parameter declarations (not their use in
 * expressions). Such a node declares a name in the scope it's defined in, and can occur in the following
 * contexts:
 *
 * <ul>
 *    <li> Field and enum constant declarations;
 *    <li> Local variable declarations;
 *    <li> Method, constructor and lambda parameter declarations;
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
 *
 *
 * <pre class="grammar">
 *
 * VariableDeclaratorId ::= &lt;IDENTIFIER&gt; {@link ASTArrayDimensions ArrayDimensions}?
 *
 * </pre>
 *
 */
// @formatter:on
public final class ASTVariableDeclaratorId extends AbstractTypedSymbolDeclarator<JVariableSymbol> implements AccessNode, SymbolDeclaratorNode {

    private VariableNameDeclaration nameDeclaration;

    ASTVariableDeclaratorId(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Note: this might be <code>null</code> in certain cases.
     */
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

    /**
     * Returns the extra array dimensions associated with this variable.
     * For example in the declaration {@code int a[]}, {@link #getTypeNode()}
     * returns {@code int}, and this method returns the dimensions that follow
     * the variable ID. Returns null if there are no such dimensions.
     */
    @Nullable
    public ASTArrayDimensions getExtraDimensions() {
        return children(ASTArrayDimensions.class).first();
    }

    @NonNull
    @Override
    public ASTModifierList getModifiers() {
        if (isPatternBinding()) {
            JavaNode firstChild = getFirstChild();
            assert firstChild != null : "Binding variable has no modifiers!";
            return (ASTModifierList) firstChild;
        }

        // delegates modifiers
        return getModifierOwnerParent().getModifiers();
    }

    private AccessNode getModifierOwnerParent() {
        JavaNode parent = getParent();
        if (parent instanceof ASTVariableDeclarator) {
            return (AccessNode) parent.getParent();
        }
        return (AccessNode) parent;
    }

    /**
     * @deprecated Use {@link #getName()}
     * @return
     */
    @Override
    @DeprecatedAttribute(replaceWith = "@Name")
    @Deprecated
    public String getImage() {
        return getName();
    }

    /** Returns the name of the variable. */
    public String getName() {
        return super.getImage();
    }

    /**
     * Returns true if the declared variable has an array type.
     */
    public boolean hasArrayType() {
        return getExtraDimensions() != null || getTypeNode() instanceof ASTArrayType;
    }


    /**
     * Returns true if this nodes declares an exception parameter in
     * a {@code catch} statement.
     */
    public boolean isExceptionBlockParameter() {
        return getParent() instanceof ASTCatchParameter;
    }


    /**
     * Returns true if this node declares a formal parameter for a method
     * declaration or a lambda expression.
     */
    public boolean isFormalParameter() {
        return getParent() instanceof ASTFormalParameter || isLambdaParameter();
    }

    /**
     * Returns true if this node declares a record component. The symbol
     * born by this node is the symbol of the corresponding field (not the
     * formal parameter of the record constructor).
     */
    public boolean isRecordComponent() {
        return getParent() instanceof ASTRecordComponent;
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
        return getParent() instanceof ASTLambdaParameter;
    }


    /**
     * Returns true if this node declares a field.
     * TODO should this return true if this is an enum constant?
     */
    public boolean isField() {
        return getNthParent(2) instanceof ASTFieldDeclaration;
    }

    /**
     * Returns true if this node declares an enum constant.
     */
    public boolean isEnumConstant() {
        return getParent() instanceof ASTEnumConstant;
    }

    /**
     * Returns the name of the variable.
     *
     * @deprecated Use {@link #getName()}
     */
    @Deprecated
    @DeprecatedAttribute(replaceWith = "@Name")
    public String getVariableName() {
        return getName();
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
        return getTypeNode() == null;
    }

    /**
     * Returns true if this is a binding variable in a
     * {@linkplain ASTPattern pattern}.
     */
    @Experimental
    public boolean isPatternBinding() {
        return getParent() instanceof ASTPattern;
    }


    /**
     * Returns the initializer of the variable, or null if it doesn't exist.
     */
    @Nullable
    public ASTExpression getInitializer() {
        if (getParent() instanceof ASTVariableDeclarator) {
            return ((ASTVariableDeclarator) getParent()).getInitializer();
        }
        return null;
    }

    /**
     * Returns the first child of the node returned by {@link #getTypeNode()}.
     * The image of that node can usually be interpreted as the image of the
     * type.
     */
    // TODO unreliable, not typesafe and not useful, should be deprecated
    @Nullable
    public Node getTypeNameNode() {
        return getTypeNode();
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
    @Nullable
    public ASTType getTypeNode() {
        AccessNode parent = getModifierOwnerParent();
        return parent.children(ASTType.class).first();
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
