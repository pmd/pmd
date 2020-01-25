/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;

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

    /**
     * @deprecated Made public for one shady usage in {@link ClassScope}
     */
    @Deprecated
    @InternalApi
    public ASTMethodDeclarator(int id) {
        super(id);
    }

    /**
     * @deprecated Use {@link ASTMethodDeclaration#getArity()}
     */
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
