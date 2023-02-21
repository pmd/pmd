/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.util.AssertionUtil;

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
    // todo rename to ASTClassType

    private JTypeDeclSymbol symbol;

    private String simpleName;

    // Note that this is only populated during disambiguation, if
    // the ambiguous qualifier is resolved to a package name
    private boolean isFqcn;
    private JClassType implicitEnclosing;

    ASTClassOrInterfaceType(ASTAmbiguousName lhs, String simpleName) {
        super(JavaParserImplTreeConstants.JJTCLASSORINTERFACETYPE);
        assert lhs != null : "Null LHS";

        this.addChild(lhs, 0);
        this.simpleName = simpleName;
        assertSimpleNameOk();
    }


    ASTClassOrInterfaceType(ASTAmbiguousName simpleName) {
        super(JavaParserImplTreeConstants.JJTCLASSORINTERFACETYPE);
        this.simpleName = simpleName.getFirstToken().getImage();

        assertSimpleNameOk();
    }

    // Just for one usage in Symbol table
    @Deprecated
    public ASTClassOrInterfaceType(String simpleName) {
        super(JavaParserImplTreeConstants.JJTCLASSORINTERFACETYPE);
        this.simpleName = simpleName;
    }

    ASTClassOrInterfaceType(@Nullable ASTClassOrInterfaceType lhs, boolean isFqcn, JavaccToken firstToken, JavaccToken identifier) {
        super(JavaParserImplTreeConstants.JJTCLASSORINTERFACETYPE);
        this.setImage(identifier.getImage());
        this.isFqcn = isFqcn;
        if (lhs != null) {
            this.addChild(lhs, 0);
        }
        this.setFirstToken(firstToken);
        this.setLastToken(identifier);
    }


    ASTClassOrInterfaceType(int id) {
        super(id);
    }

    @Override
    protected void setImage(String image) {
        this.simpleName = image;
        assertSimpleNameOk();
    }

    @Deprecated
    @Override
    public String getImage() {
        return null;
    }

    private void assertSimpleNameOk() {
        assert this.simpleName != null
            && this.simpleName.indexOf('.') < 0
            && AssertionUtil.isJavaIdentifier(this.simpleName)
            : "Invalid simple name '" + this.simpleName + "'";
    }

    /**
     * Returns true if the type was written with a full package qualification.
     * For example, {@code java.lang.Override}. For nested types, only the
     * leftmost type is considered fully qualified. Eg in {@code p.Outer.Inner},
     * this method will return true for the type corresponding to {@code p.Outer},
     * but false for the enclosing {@code p.Outer.Inner}.
     */
    public boolean isFullyQualified() {
        return isFqcn;
    }

    void setSymbol(JTypeDeclSymbol symbol) {
        this.symbol = symbol;
    }

    // this is just a transitory variable
    void setImplicitEnclosing(JClassType enclosing) {
        implicitEnclosing = enclosing;
    }

    JClassType getImplicitEnclosing() {
        return implicitEnclosing;
    }

    /**
     * Returns the type symbol this type refers to. This is never null
     * after disambiguation has been run. This is also very internal.
     */
    JTypeDeclSymbol getReferencedSym() {
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
     * Returns the simple name of this type. Use the {@linkplain #getReferencedSym() symbol}
     * to get more information.
     */
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * Checks whether the type this node is referring to is declared within the
     * same compilation unit - either a class/interface or a enum type. You want
     * to check this, if {@link #getType()} is null.
     *
     * @return {@code true} if this node referencing a type in the same
     * compilation unit, {@code false} otherwise.
     *
     * @deprecated This may be removed once type resolution is afoot
     */
    @Deprecated
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

    void setFullyQualified() {
        this.isFqcn = true;
    }
}
