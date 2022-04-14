/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * A "provides" directive of a {@linkplain ASTModuleDeclaration module declaration}.
 *
 * <pre class="grammar">
 *
 * ModuleProvidesDirective ::=
 *     "provides" {@linkplain ASTClassOrInterfaceType ClassType}
 *     "with" {@linkplain ASTClassOrInterfaceType ClassType} ( "," {@linkplain ASTClassOrInterfaceType ClassType} )*
 *     ";"
 *
 * </pre>
 */
public final class ASTModuleProvidesDirective extends ASTModuleDirective {

    ASTModuleProvidesDirective(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTClassOrInterfaceType getProvidedInterface() {
        return firstChild(ASTClassOrInterfaceType.class);
    }

    public NodeStream<ASTClassOrInterfaceType> getImplementations() {
        return children(ASTClassOrInterfaceType.class).drop(1);
    }
}
