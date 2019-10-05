/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;

/**
 * @deprecated This node will be removed with 7.0.0. You
 *     can directly use {@link ASTMethodDeclaration#getName()},
 *     {@link ASTMethodDeclaration#getFormalParameters()}, {@link ASTMethodDeclaration#getArity()} instead.
 */
@Deprecated
public class ASTMethodDeclarator extends AbstractJavaNode {

    /**
     * @deprecated Made public for one shady usage in {@link ClassScope}
     */
    @Deprecated
    @InternalApi
    public ASTMethodDeclarator(int id) {
        super(id);
    }

    ASTMethodDeclarator(JavaParser p, int id) {
        super(p, id);
    }

    /**
     * @deprecated Use {@link ASTMethodDeclaration#getArity()}
     */
    @Deprecated
    public int getParameterCount() {
        return getFormalParameters().getParameterCount();
    }

    public ASTFormalParameters getFormalParameters() {
        return (ASTFormalParameters) jjtGetChild(0);
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


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}
