/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Optional;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;

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
 *                        | (ClassOrInterfaceType | {@link ASTAmbiguousName AmbiguousName}) "." {@link ASTAnnotation Annotation}* &lt;IDENTIFIER&gt; {@link ASTTypeArguments TypeArguments}?
 *
 * </pre>
 */
// @formatter:on
public final class ASTClassOrInterfaceType extends AbstractJavaTypeNode implements ASTReferenceType, LeftRecursiveNode {

    ASTClassOrInterfaceType(ASTAmbiguousName lhs, String image) {
        super(JavaParserTreeConstants.JJTCLASSORINTERFACETYPE);
        this.jjtAddChild(lhs, 0);
        this.setImage(image);
    }


    ASTClassOrInterfaceType(ASTAmbiguousName simpleName) {
        super(JavaParserTreeConstants.JJTCLASSORINTERFACETYPE);
        this.setImage(simpleName.getImage());
    }


    // Just for one usage in Symbol table
    public ASTClassOrInterfaceType(String simpleName) {
        super(JavaParserTreeConstants.JJTCLASSORINTERFACETYPE);
        this.setImage(simpleName);
    }


    ASTClassOrInterfaceType(int id) {
        super(id);
    }


    ASTClassOrInterfaceType(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Gets the owner type of this type if it's not ambiguous. This is a
     * type we know for sure that this type is a member of.
     *
     * @return A type, or null if this is a base type
     */
    @Nullable
    public ASTClassOrInterfaceType getLhsType() {
        return getFirstChildOfType(ASTClassOrInterfaceType.class);
    }


    /**
     * Returns the left-hand side is an ambiguous name that has not been reclassified.
     * The ambiguous name can be a package or type name.
     */
    @Nullable
    public ASTAmbiguousName getAmbiguousLhs() {
        return getFirstChildOfType(ASTAmbiguousName.class);
    }

    /**
     * Returns the type arguments of this segment if some are specified.
     */
    @Nullable
    public ASTTypeArguments getTypeArguments() {
        return getFirstChildOfType(ASTTypeArguments.class);
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
     * Returns the simple name of this type.
     */
    public String getSimpleName() {
        return getImage();
    }

    /**
     * For now this returns the name of the type with all the segments,
     * without annotations or type parameters.
     */
    @Override
    @Experimental
    public String getTypeImage() {
        Supplier<String> ambiguousName =
            () -> Optional.ofNullable(getAmbiguousLhs()).map(s -> s.getName() + ".").orElse("");

        return Optional.ofNullable(getLhsType()).map(s -> s.getTypeImage() + ".").orElseGet(ambiguousName) + getImage();
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
        return jjtGetParent().getFirstChildOfType(ASTClassOrInterfaceBody.class) != null;
    }

}
