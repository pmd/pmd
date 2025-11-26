/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-12.html#jls-12.1.4">12.1.4. Invoke a main Method</a>
     */
    public boolean isMainMethod() {
        if (!isMainMethodCandidate()) {
            return false;
        }
        if (isStatic()) {
            return getArity() == 1 || hasNoOtherMainMethodSibling(
                m -> m.isStatic() && m.getArity() == 1);
        }
        if (getArity() == 1) {
            return hasNoOtherMainMethodSibling(ASTExecutableDeclaration::isStatic);
        }
        return hasNoOtherMainMethodSibling(any -> true)
            && !ancestors(ASTClassDeclaration.class).firstOpt()
                    .map(this::hasParametrizedMain).orElse(false);
    }

    private boolean hasNoOtherMainMethodSibling(Predicate<ASTMethodDeclaration> check) {
        return getParent().children(ASTMethodDeclaration.class).toStream().filter(check)
            .noneMatch(m -> m != this && m.isMainMethodCandidate());
    }

    private boolean isMainMethodCandidate() {
        return "main".equals(this.getName())
            && !this.hasModifiers(JModifier.PRIVATE)
            && this.isVoid()
            && (this.getArity() == 0
            || this.getArity() == 1 && TypeTestUtil.isExactlyA(String[].class, this.getFormalParameters().get(0)));
    }

    private boolean hasParametrizedMain(ASTClassDeclaration astClassType) {
        return astClassType.getTypeMirror().streamMethods(
            m -> "main".equals(m.getSimpleName()) && m.getArity() == 1 && !m.isStatic())
            .anyMatch(m -> TypeTestUtil.isExactlyA(String[].class, m.getFormalParameters().get(0)));
    }
}
