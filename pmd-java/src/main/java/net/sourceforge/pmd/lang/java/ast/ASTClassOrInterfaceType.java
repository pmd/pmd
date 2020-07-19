/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;

// @formatter:off
/**
 * Represents a class or interface type, possibly parameterised with type arguments.
 *
 * <p>This node corresponds to the JLS' <a href="https://docs.oracle.com/javase/specs/jls/se11/html/jls-4.html#jls-ClassOrInterfaceType">ClassOrInterfaceType</a>,
 * and also to the related productions TypeIdentifier and TypeName. Merging those allow
 * to treat them uniformly.
 *
 * <pre class="grammar">
 *
 * ClassOrInterfaceType ::= {@link ASTAnnotation Annotation}* &lt;IDENTIFIER&gt; {@link ASTTypeArguments TypeArguments}?
 *                        | ClassOrInterfaceType "." {@link ASTAnnotation Annotation}* &lt;IDENTIFIER&gt; {@link ASTTypeArguments TypeArguments}?
 *
 * </pre>
 *
 * @implNote
 * The parser may produce an AmbiguousName for the qualifier.
 * This is systematically removed by the disambiguation phase.
 */
// @formatter:on
public final class ASTClassOrInterfaceType extends AbstractJavaTypeNode implements ASTReferenceType {

    private JTypeDeclSymbol symbol;

    ASTClassOrInterfaceType(ASTAmbiguousName lhs, String image) {
        super(JavaParserImplTreeConstants.JJTCLASSORINTERFACETYPE);
        this.addChild(lhs, 0);
        this.setImage(image);
    }


    ASTClassOrInterfaceType(ASTAmbiguousName simpleName) {
        super(JavaParserImplTreeConstants.JJTCLASSORINTERFACETYPE);
        this.setImage(simpleName.getImage());
    }


    // Just for one usage in Symbol table
    public ASTClassOrInterfaceType(String simpleName) {
        super(JavaParserImplTreeConstants.JJTCLASSORINTERFACETYPE);
        this.setImage(simpleName);
    }

    ASTClassOrInterfaceType(ASTClassOrInterfaceType lhs, String image, JavaccToken firstToken, JavaccToken identifier) {
        super(JavaParserImplTreeConstants.JJTCLASSORINTERFACETYPE);
        this.setImage(image);
        if (lhs != null) {
            this.addChild(lhs, 0);
        }
        this.setFirstToken(firstToken);
        this.setLastToken(identifier);
    }


    ASTClassOrInterfaceType(int id) {
        super(id);
    }


    void setSymbol(JTypeDeclSymbol symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the type symbol this type refers to. This is never null
     * after disambiguation has been run.
     */
    public JTypeDeclSymbol getReferencedSym() {
        // this is a crutch for now, can be replaced with getTypeDefinition later
        return symbol;
    }

    /**
     * Gets the owner type of this type if it's not ambiguous. This is a
     * type we know for sure that this type is a member of.
     *
     * @return A type, or null if this is a base type
     */
    @Nullable
    public ASTClassOrInterfaceType getQualifier() {
        return getFirstChildOfType(ASTClassOrInterfaceType.class);
    }

    /**
     * Returns the type arguments of this segment if some are specified.
     */
    @Nullable
    public ASTTypeArguments getTypeArguments() {
        return getFirstChildOfType(ASTTypeArguments.class);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the simple name of this type.
     */
    public String getSimpleName() {
        return AstImplUtil.getLastSegment(getImage(), '.');
    }

    /**
     * For now this returns the name of the type with all the segments,
     * without annotations or type parameters.
     */
    @Override
    @Experimental
    public String getTypeImage() {
        return children(ASTType.class).firstOpt().map(s -> s.getTypeImage() + ".").orElse("") + getImage();
    }

    /**
     * Checks whether the type this node is referring to is declared within the
     * same compilation unit - either a class/interface or a enum type. You want
     * to check this, if {@link #getType()} is null.
     *
     * @return {@code true} if this node referencing a type in the same
     * compilation unit, {@code false} otherwise.
     */
    public boolean isReferenceToClassSameCompilationUnit() {
        ASTCompilationUnit root = getFirstParentOfType(ASTCompilationUnit.class);
        for (ASTClassOrInterfaceDeclaration c : root.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class, true)) {
            if (c.hasImageEqualTo(getImage())) {
                return true;
            }
        }
        for (ASTEnumDeclaration e : root.findDescendantsOfType(ASTEnumDeclaration.class, true)) {
            if (e.hasImageEqualTo(getImage())) {
                return true;
            }
        }
        return false;
    }


    public boolean isAnonymousClass() {
        return getParent().getFirstChildOfType(ASTClassOrInterfaceBody.class) != null;
    }

}
