/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.rule.xpath.DeprecatedAttribute;

/**
 * Child of an {@link ASTMethodDeclaration}.
 *
 * <p>
 *
 * MethodDeclarator ::=  &lt;IDENTIFIER&gt; {@link ASTFormalParameters FormalParameters} ( "[" "]" )*
 *
 * </p>
 *
 * @deprecated Removed, former children are direct children of {@link ASTMethodDeclaration}.
 * This is because the node is not even shared with {@link ASTAnnotationMethodDeclaration} and is
 * really not useful, mostly worked around everywhere.
 */
@Deprecated
public final class ASTMethodDeclarator extends AbstractJavaNode {

    ASTMethodDeclarator(int id) {
        super(id);
    }

    /**
     * @deprecated Use {@link ASTMethodDeclaration#getArity()}
     */
    @DeprecatedAttribute(replaceWith = "MethodDeclaration/@Arity")
    @Deprecated
    public int getParameterCount() {
        return getFormalParameters().size();
    }

    public ASTFormalParameters getFormalParameters() {
        return (ASTFormalParameters) getChild(0);
    }

    /**
     * @deprecated Use {@link ASTMethodDeclaration#getName()}
     */
    @Deprecated
    @DeprecatedAttribute(replaceWith = "MethodDeclaration/@Name")
    @Override
    public String getImage() {
        return super.getImage();
    }

    @Override
    public ASTMethodDeclaration getParent() {
        return (ASTMethodDeclaration) super.getParent();
    }

    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }
}
