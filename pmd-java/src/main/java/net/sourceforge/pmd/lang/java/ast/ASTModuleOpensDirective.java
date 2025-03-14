/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * An "opens" directive of a {@linkplain ASTModuleDeclaration module declaration}.
 *
 * <pre class="grammar">
 *
 * ModuleOpensDirective ::=
 *     "opens" &lt;PACKAGE_NAME&gt;
 *     ( "to" {@linkplain ASTModuleName ModuleName} ( "," {@linkplain ASTModuleName ModuleName})* )?
 *     ";"
 *
 * </pre>
 */
public final class ASTModuleOpensDirective extends AbstractPackageNameModuleDirective {

    ASTModuleOpensDirective(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns a stream of the module names that are found after the "to" keyword.
     * May be empty
     */
    public NodeStream<ASTModuleName> getTargetModules() {
        return children(ASTModuleName.class);
    }

}
