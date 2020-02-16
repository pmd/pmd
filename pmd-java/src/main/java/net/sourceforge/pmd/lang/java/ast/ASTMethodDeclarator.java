/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * @deprecated This node will be removed with 7.0.0. You
 *     can directly use {@link ASTMethodDeclaration#getName()},
 *     {@link ASTMethodDeclaration#getFormalParameters()}, {@link ASTMethodDeclaration#getArity()} instead.
 */
@Deprecated
public class ASTMethodDeclarator extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTMethodDeclarator(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTMethodDeclarator(JavaParser p, int id) {
        super(p, id);
    }

    /**
     * @deprecated Use {@link ASTMethodDeclaration#getArity()}
     */
    @Deprecated
    public int getParameterCount() {
        return getFirstChildOfType(ASTFormalParameters.class).size();
    }

    /**
     * @deprecated Use {@link ASTMethodDeclaration#getName()}
     */
    @Deprecated
    @Override
    public String getImage() {
        return super.getImage();
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
