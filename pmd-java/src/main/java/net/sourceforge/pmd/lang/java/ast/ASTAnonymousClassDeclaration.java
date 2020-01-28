/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;


/**
 * An anonymous class declaration. This can occur in a {@linkplain ASTConstructorCall class instance creation expression}
 * or in an {@linkplain ASTEnumConstant enum constant declaration}.
 *
 *
 * <pre class="grammar">
 *
 * AnonymousClassDeclaration ::= {@link ASTClassOrInterfaceBody}
 *
 * </pre>
 */
public final class ASTAnonymousClassDeclaration extends AbstractJavaTypeNode implements JavaQualifiableNode {

    private JavaTypeQualifiedName qualifiedName;


    ASTAnonymousClassDeclaration(int id) {
        super(id);
    }


    @Override
    public boolean isFindBoundary() {
        return true;
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * Returns the body of the anonymous class.
     */
    public ASTClassOrInterfaceBody getBody() {
        return (ASTClassOrInterfaceBody) getChild(0);
    }


    /**
     * Returns the qualified name of the anonymous class
     * declared by this node.
     */
    @Override
    public JavaTypeQualifiedName getQualifiedName() {
        return qualifiedName;
    }


    public void setQualifiedName(JavaTypeQualifiedName qname) {
        this.qualifiedName = qname;
    }

}
