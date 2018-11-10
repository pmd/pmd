/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
/* Generated By:JJTree: Do not edit this line. ASTClassOrInterfaceBody.java */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the body of a {@linkplain ASTClassOrInterfaceDeclaration class or interface declaration}.
 * This includes anonymous classes, including those defined within an {@linkplain ASTEnumConstant enum constant}.
 *
 * <pre>
 *
 * ClassOrInterfaceBody ::=  "{"  {@linkplain ASTClassOrInterfaceBodyDeclaration ClassOrInterfaceBodyDeclaration}* "}"
 *
 * </pre>
 */
public class ASTClassOrInterfaceBody extends AbstractJavaNode {
    public ASTClassOrInterfaceBody(int id) {
        super(id);
    }

    public ASTClassOrInterfaceBody(JavaParser p, int id) {
        super(p, id);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public boolean isAnonymousInnerClass() {
        return jjtGetParent() instanceof ASTAllocationExpression;
    }

    public boolean isEnumChild() {
        return jjtGetParent() instanceof ASTEnumConstant;
    }
}
