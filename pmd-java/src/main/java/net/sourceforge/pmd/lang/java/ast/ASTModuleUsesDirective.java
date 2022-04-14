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
public final class ASTModuleUsesDirective extends ASTModuleDirective {

    ASTModuleUsesDirective(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the node representing the consumed service.
     */
    public ASTClassOrInterfaceType getService() {
        return firstChild(ASTClassOrInterfaceType.class);
    }

}
