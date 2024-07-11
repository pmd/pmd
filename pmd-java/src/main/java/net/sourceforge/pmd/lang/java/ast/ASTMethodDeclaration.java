/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;


/**
 * A method declaration, in a class or interface declaration. Since 7.0,
 * this also represents annotation methods. Annotation methods have a
 * much more restricted grammar though, in particular:
 * <ul>
 * <li>They can't declare a {@linkplain #getThrowsList() throws clause}
 * <li>They can't declare {@linkplain #getTypeParameters() type parameters}
 * <li>Their {@linkplain #getFormalParameters() formal parameters} must be empty
 * <li>They can't be declared void
 * <li>They must be abstract
 * </ul>
 * They can however declare a {@link #getDefaultClause() default value}.
 *
 * <pre class="grammar">
 *
 * MethodDeclaration ::= {@link ASTModifierList ModifierList}
 *                       {@link ASTTypeParameters TypeParameters}?
 *                       {@link ASTType Type}
 *                       &lt;IDENTIFIER&gt;
 *                       {@link ASTFormalParameters FormalParameters}
 *                       {@link ASTArrayDimensions ArrayDimensions}?
 *                       {@link ASTThrowsList ThrowsList}?
 *                       ({@link ASTBlock Block} | ";" )
 *
 * </pre>
 */
public final class ASTMethodDeclaration extends AbstractExecutableDeclaration<JMethodSymbol> {

    private String name;

    /**
     * Populated by {@link OverrideResolutionPass}.
     */
    private JMethodSig overriddenMethod = null;

    ASTMethodDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns true if this method is overridden.
     */
    public boolean isOverridden() {
        return overriddenMethod != null;
    }

    /**
     * Returns the signature of the method this method overrides in a
     * supertype. Note that this method may be implementing several methods
     * of super-interfaces at once, in that case, an arbitrary one is returned.
     *
     * <p>If the method has an {@link Override} annotation, but we couldn't
     * resolve any method that is actually implemented, this will return
     * {@link TypeSystem#UNRESOLVED_METHOD}.
     */
    public JMethodSig getOverriddenMethod() {
        return overriddenMethod;
    }

    void setOverriddenMethod(JMethodSig overriddenMethod) {
        this.overriddenMethod = overriddenMethod;
    }

    @Override
    public FileLocation getReportLocation() {
        // the method identifier
        JavaccToken ident = TokenUtils.nthPrevious(getModifiers().getLastToken(), getFormalParameters().getFirstToken(), 1);
        return ident.getReportLocation();
    }

    /** Returns the simple name of the method. */
    @Override
    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    /**
     * If this method declaration is an explicit record component accessor,
     * returns the corresponding record component. Otherwise returns null.
     */
    public @Nullable ASTRecordComponent getAccessedRecordComponent() {
        if (getArity() != 0) {
            return null;
        }
        ASTRecordComponentList components = getEnclosingType().getRecordComponents();
        if (components == null) {
            return null;
        }

        return components.toStream().first(it -> it.getVarId().getName().equals(this.getName()));
    }


    /**
     * Returns true if the result type of this method is {@code void}.
     */
    public boolean isVoid() {
        return getResultTypeNode().isVoid();
    }

    /**
     * Returns the default clause, if this is an annotation method declaration
     * that features one. Otherwise returns null.
     */
    @Nullable
    public ASTDefaultValue getDefaultClause() {
        return AstImplUtil.getChildAs(this, getNumChildren() - 1, ASTDefaultValue.class);
    }

    /**
     * Returns the result type node of the method. This may be a {@link ASTVoidType}.
     */
    public @NonNull ASTType getResultTypeNode() { // TODO rename to getResultType()
        return firstChild(ASTType.class);
    }

    /**
     * Returns the extra array dimensions that may be after the
     * formal parameters.
     */
    @Nullable
    public ASTArrayDimensions getExtraDimensions() {
        return children(ASTArrayDimensions.class).first();
    }

    /**
     * Returns whether this is a main method declaration.
     */
    public boolean isMainMethod() {
        return this.hasModifiers(JModifier.PUBLIC, JModifier.STATIC)
            && "main".equals(this.getName())
            && this.isVoid()
            && this.getArity() == 1
            && TypeTestUtil.isExactlyA(String[].class, this.getFormalParameters().get(0))
            || isMainMethodInImplicitlyDeclaredClass();
    }

    /**
     * With JEP 445/463/477 (Java 23 Preview) the main method does not need to be static anymore and
     * does not need to be public or have a formal parameter.
     */
    private boolean isMainMethodInImplicitlyDeclaredClass() {
        return this.getRoot().isImplicitlyDeclaredClass()
                && "main".equals(this.getName())
                && !this.hasModifiers(JModifier.PRIVATE)
                && this.isVoid()
                && (this.getArity() == 0
                    || this.getArity() == 1 && TypeTestUtil.isExactlyA(String[].class, this.getFormalParameters().get(0)));
    }
}
