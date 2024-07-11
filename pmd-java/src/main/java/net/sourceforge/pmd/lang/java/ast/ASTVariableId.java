/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

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
 * <p>Since this node conventionally represents the declared variable in PMD,
 * it owns a {@link JVariableSymbol} and can provide access to
 * {@linkplain #getLocalUsages() variable usages}.
 *
 * <pre class="grammar">
 *
 * VariableId ::= &lt;IDENTIFIER&gt; {@link ASTArrayDimensions ArrayDimensions}?
 *
 * </pre>
 *
 * <p>Note: This node has been called ASTVariableDeclaratorId in PMD 6.
 */
// @formatter:on
public final class ASTVariableId extends AbstractTypedSymbolDeclarator<JVariableSymbol> implements ModifierOwner {

    private String name;
    private List<ASTNamedReferenceExpr> usages = Collections.emptyList();

    ASTVariableId(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns an unmodifiable list of the usages of this variable that
     * are made in this file. Note that for a record component, this returns
     * usages both for the formal parameter symbol and its field counterpart.
     *
     * <p>Note that a variable initializer is not part of the usages
     * (though this should be evident from the return type).
     */
    public List<ASTNamedReferenceExpr> getLocalUsages() {
        return usages;
    }

    void addUsage(ASTNamedReferenceExpr usage) {
        if (usages.isEmpty()) {
            usages = new ArrayList<>(4); //make modifiable
        }
        usages.add(usage);
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
        // delegates modifiers
        return getModifierOwnerParent().getModifiers();
    }

    /**
     * Return true if the declared variable is static.
     * There may not be an explicit final modifier, e.g. for enum constants.
     */
    public boolean isFinal() {
        return hasModifiers(JModifier.FINAL);
    }

    /**
     * Return true if the declared variable is static.
     * There may not be an explicit static modifier, e.g. for enum constants.
     *
     * @since 7.1.0
     */
    public boolean isStatic() {
        return hasModifiers(JModifier.STATIC);
    }

    @Override
    public Visibility getVisibility() {
        return isPatternBinding() ? Visibility.V_LOCAL
                                  : getModifierOwnerParent().getVisibility();
    }


    private ModifierOwner getModifierOwnerParent() {
        JavaNode parent = getParent();
        if (parent instanceof ASTVariableDeclarator) {
            return (ModifierOwner) parent.getParent();
        }
        return (ModifierOwner) parent;
    }

    /**
     * Return true if this variable has no name. The name is then equal to {@code "_"}.
     * A variable declaration with this name does not actually declare a variable in
     * the current scope, since Java 22. In Java 9 to 21, the identifier {@code _} is
     * restricted and cannot be used to name a variable. Before Java 9, it is a regular
     * identifier.
     */
    public boolean isUnnamed() {
        return "_".equals(name) && getLanguageVersion().compareToVersion("21") >= 0;
    }

    /** Returns the name of the variable. */
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
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
     * Returns true if this node declares a local variable from within
     * a regular {@link ASTLocalVariableDeclaration}.
     */
    public boolean isLocalVariable() {
        return ancestors().get(1) instanceof ASTLocalVariableDeclaration
            && !isResourceDeclaration()
            && !isForeachVariable();
    }

    /**
     * Returns true if this node is a variable declared in a
     * {@linkplain ASTForeachStatement foreach loop}.
     */
    public boolean isForeachVariable() {
        // Foreach/LocalVarDecl/VarDeclarator/VarDeclId
        return ancestors().get(2) instanceof ASTForeachStatement;
    }

    /**
     * Returns true if this node is a variable declared in the init clause
     * of a {@linkplain ASTForStatement for loop}.
     */
    public boolean isForLoopVariable() {
        // For/ForInit/LocalVarDecl/VarDeclarator/VarDeclId
        return ancestors().get(2) instanceof ASTForInit;
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
     * Returns true if this node declares a field from a regular
     * {@link ASTFieldDeclaration}. This returns false for enum
     * constants (use {@link JVariableSymbol#isField() getSymbol().isField()}
     * if you want that).
     */
    public boolean isField() {
        return ancestors().get(1) instanceof ASTFieldDeclaration;
    }

    /**
     * Returns true if this node declares an enum constant.
     */
    public boolean isEnumConstant() {
        return getParent() instanceof ASTEnumConstant;
    }


    /**
     * Returns true if this declarator id declares a resource in a try-with-resources statement.
     */
    public boolean isResourceDeclaration() {
        // Resource/LocalVariableDeclaration/VariableDeclarator
        return getParent().getParent().getParent() instanceof ASTResource;
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
     * node. See {@link #getTypeMirror()} for an explanation.
     *
     * @return the type node, or {@code null} if there is no explicit type,
     *     e.g. if {@link #isTypeInferred()} returns true.
     */
    public @Nullable ASTType getTypeNode() {
        ModifierOwner parent = getModifierOwnerParent();
        return parent.firstChild(ASTType.class);
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
    public @NonNull JTypeMirror getTypeMirror() {
        return super.getTypeMirror();
    }
}
