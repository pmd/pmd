/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;

/**
 * Child of an {@link ASTMethodDeclaration}.
 *
 * TODO This is not useful, remove
 *
 * <p>
 *
 * MethodDeclarator ::=  &lt;IDENTIFIER&gt; {@link ASTFormalParameters FormalParameters} ( "[" "]" )*
 *
 * </p>
 *
 */
public final class ASTMethodDeclarator extends AbstractJavaNode {

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

    public int getParameterCount() {
        return getFormalParameters().getParameterCount();
    }

    public ASTFormalParameters getFormalParameters() {
        return (ASTFormalParameters) jjtGetChild(0);
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
