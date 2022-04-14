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

    /**
     * Returns the node representing the provided interface.
     */
    public ASTClassOrInterfaceType getService() {
        return firstChild(ASTClassOrInterfaceType.class);
    }

    /**
     * Returns the nodes representing the service providers, that is,
     * the service implementations.
     */
    public NodeStream<ASTClassOrInterfaceType> getServiceProviders() {
        return children(ASTClassOrInterfaceType.class).drop(1);
    }
}
