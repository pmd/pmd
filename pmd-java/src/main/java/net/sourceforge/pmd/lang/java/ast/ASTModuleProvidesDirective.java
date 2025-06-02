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
 *     "provides" {@linkplain ASTClassType ClassType}
 *     "with" {@linkplain ASTClassType ClassType} ( "," {@linkplain ASTClassType ClassType} )*
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
    public ASTClassType getService() {
        return firstChild(ASTClassType.class);
    }

    /**
     * Returns the nodes representing the service providers, that is,
     * the service implementations.
     */
    public NodeStream<ASTClassType> getServiceProviders() {
        return children(ASTClassType.class).drop(1);
    }
}
