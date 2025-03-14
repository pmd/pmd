/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;


/**
 * Represents a field declaration in the body of a type declaration.
 *
 * <p>This declaration may define several variables, possibly of different
 * types. The nodes corresponding to the declared variables are accessible
 * through {@link #iterator()}.
 *
 * <pre class="grammar">
 *
 * FieldDeclaration ::= {@link ASTModifierList ModifierList} {@linkplain ASTType Type} {@linkplain ASTVariableDeclarator VariableDeclarator} ( "," {@linkplain ASTVariableDeclarator VariableDeclarator} )* ";"
 *
 * </pre>
 */
public final class ASTFieldDeclaration extends AbstractJavaNode
    implements LeftRecursiveNode,
               ASTBodyDeclaration,
               InternalInterfaces.MultiVariableIdOwner,
               JavadocCommentOwner {


    ASTFieldDeclaration(int id) {
        super(id);
    }


    @Override
    public FileLocation getReportLocation() {
        // report on the identifier and not the annotations
        return getVarIds().firstOrThrow().getFirstToken().getReportLocation();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the variable name of this field. This method searches the first
     * VariableId node and returns its image or <code>null</code> if
     * the child node is not found.
     *
     * @return a String representing the name of the variable
     *
     * @deprecated FieldDeclaration may declare several variables, so this is not exhaustive
     *     Iterate on the {@linkplain ASTVariableId VariableIds} instead
     */
    @Deprecated
    @DeprecatedAttribute(replaceWith = "VariableId/@Name")
    public String getVariableName() {
        return getVarIds().firstOrThrow().getName();
    }


    /**
     * Returns the type node at the beginning of this field declaration.
     * The type of this node is not necessarily the type of the variables,
     * see {@link ASTVariableId#getTypeNode()}.
     */
    @Override
    public ASTType getTypeNode() {
        return firstChild(ASTType.class);
    }

    /**
     * Returns true if this field is static.
     */
    public boolean isStatic() {
        return hasModifiers(JModifier.STATIC);
    }
}
