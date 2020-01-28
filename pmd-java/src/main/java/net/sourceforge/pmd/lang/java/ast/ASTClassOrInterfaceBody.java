/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the body of a {@linkplain ASTClassOrInterfaceDeclaration class or interface declaration}.
 * This includes anonymous classes, including those defined within an {@linkplain ASTEnumConstant enum constant}.
 *
 * <pre class="grammar">
 *
 * ClassOrInterfaceBody ::=  "{"  {@linkplain ASTClassOrInterfaceBodyDeclaration ClassOrInterfaceBodyDeclaration}* "}"
 *
 * </pre>
 */
public final class ASTClassOrInterfaceBody extends AbstractJavaNode implements ASTTypeBody {

    ASTClassOrInterfaceBody(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public boolean isAnonymousInnerClass() {
        return getParent() instanceof ASTAllocationExpression;
    }

    public boolean isEnumChild() {
        return getParent() instanceof ASTEnumConstant;
    }
}
