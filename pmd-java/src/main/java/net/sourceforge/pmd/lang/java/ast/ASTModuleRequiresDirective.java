/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A "requires"  directive of a {@linkplain ASTModuleDeclaration module declaration}.
 *
 * <pre class="grammar">
 *
 * ModuleRequiresDirective ::=
 *     "requires" ( "transitive" | "static" )? {@linkplain ASTModuleName ModuleName} ";"
 *
 * </pre>
 */
public final class ASTModuleRequiresDirective extends ASTModuleDirective {

    private boolean isStatic;
    private boolean isTransitive;

    ASTModuleRequiresDirective(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the name of the required module.
     */
    public @NonNull ASTModuleName getRequiredModule() {
        return Objects.requireNonNull(firstChild(ASTModuleName.class));
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isTransitive() {
        return isTransitive;
    }

    void setTransitive() {
        isTransitive = true;
    }

    void setStatic() {
        isStatic = true;
    }
}
