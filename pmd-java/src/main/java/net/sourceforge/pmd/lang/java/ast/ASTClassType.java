/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
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
 * ClassType ::= {@link ASTAnnotation Annotation}* &lt;IDENTIFIER&gt; {@link ASTTypeArguments TypeArguments}?
 *                        | ClassType "." {@link ASTAnnotation Annotation}* &lt;IDENTIFIER&gt; {@link ASTTypeArguments TypeArguments}?
 *
 * </pre>
 *
 * <p>Note: This node was called ASTClassOrInterfaceType in PMD 6.
 *
 * @implNote
 * The parser may produce an AmbiguousName for the qualifier.
 * This is systematically removed by the disambiguation phase.
 */
// @formatter:on
public final class ASTClassType extends AbstractJavaTypeNode implements ASTReferenceType {
    private JTypeDeclSymbol symbol;

    private String simpleName;

    // Note that this is only populated during disambiguation, if
    // the ambiguous qualifier is resolved to a package name
    private boolean isFqcn;
    private JClassType implicitEnclosing;

    ASTClassType(ASTAmbiguousName lhs, String simpleName) {
        super(JavaParserImplTreeConstants.JJTCLASSTYPE);
        assert lhs != null : "Null LHS";

        this.addChild(lhs, 0);
        this.setSimpleName(simpleName);
    }


    ASTClassType(ASTAmbiguousName simpleName) {
        super(JavaParserImplTreeConstants.JJTCLASSTYPE);
        this.setSimpleName(simpleName.getFirstToken().getImage());
    }

    ASTClassType(@Nullable ASTClassType lhs, boolean isFqcn, JavaccToken firstToken, JavaccToken identifier) {
        super(JavaParserImplTreeConstants.JJTCLASSTYPE);
        this.setSimpleName(identifier.getImage());
        this.isFqcn = isFqcn;
        if (lhs != null) {
            this.addChild(lhs, 0);
        }
        this.setFirstToken(firstToken);
        this.setLastToken(identifier);
    }


    ASTClassType(int id) {
        super(id);
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
    public ASTClassType getQualifier() {
        return firstChild(ASTClassType.class);
    }

    /**
     * Returns the type arguments of this segment if some are specified.
     */
    @Nullable
    public ASTTypeArguments getTypeArguments() {
        return firstChild(ASTTypeArguments.class);
    }


    /**
     * Return the package qualifier, if this is a fully qualified name.
     * Note that this will only be the case if we could resolve the
     * qualifier to a package name during disambiguation. In other words,
     * if the auxclasspath is not complete, and the qualifier could not
     * be disambiguated, this method will return null (and an AmbiguousName
     * will stay in the tree).
     *
     * <p>If a nested type is package-qualified, this method
     * will only return a non-null result for the leftmost (=innermost)
     * ASTClassType node. For instance in {@code java.util.Map.Entry},
     * the node {@code java.util.Map} has package qualifier
     * {@code "java.util"}, while the node for {@code Entry}
     * has package qualifier {@code null}. Note that the
     * {@linkplain JClassSymbol#getPackageName() package name}
     * of the symbol and type mirror will reflect that package
     * name anyway, on both nodes.
     */
    public @Nullable String getPackageQualifier() {
        if (isFullyQualified()) {
            assert symbol != null : "Symbol should be non-null if isFullyQualified returns true";
            return symbol.getPackageName();
        }
        return null;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
        assertSimpleNameOk();
    }

    private void assertSimpleNameOk() {
        assert this.simpleName != null
                && this.simpleName.indexOf('.') < 0
                && AssertionUtil.isJavaIdentifier(this.simpleName)
                : "Invalid simple name '" + this.simpleName + "'";
    }

    /**
     * Returns the simple name of this type. Use the {@linkplain #getReferencedSym() symbol}
     * to get more information.
     */
    public String getSimpleName() {
        return simpleName;
    }

    void setFullyQualified() {
        this.isFqcn = true;
    }
}
