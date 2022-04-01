/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A "uses" directive of a {@linkplain ASTModuleDeclaration module declaration}.
 *
 * <pre class="grammar">
 *
 * ModuleUsesDirective ::= "uses" &lt;PACKAGE_NAME&gt; ";"
 *
 * </pre>
 */
public final class ASTModuleUsesDirective extends AbstractPackageNameModuleDirective {

    ASTModuleUsesDirective(int id) {
        super(id, DirectiveType.USES);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
