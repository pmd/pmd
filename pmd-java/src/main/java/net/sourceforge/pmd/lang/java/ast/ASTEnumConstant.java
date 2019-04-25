/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an enum constant declaration within an {@linkplain ASTEnumDeclaration enum declaration}.
 *
 * <pre class="grammar">
 *
 * EnumConstant ::= {@link ASTVariableDeclaratorId VariableDeclaratorId} {@linkplain ASTArgumentList ArgumentList}? {@linkplain ASTAnonymousClassDeclaration AnonymousClassDeclaration}?
 *
 * </pre>
 */
public final class ASTEnumConstant extends AbstractJavaNode {


    ASTEnumConstant(int id) {
        super(id);
    }

    ASTEnumConstant(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public ASTVariableDeclaratorId getId() {
        return (ASTVariableDeclaratorId) jjtGetChild(0);
    }

    @Override
    public String getImage() {
        return getId().getImage();
    }

    /**
     * Returns the name of the enum constant.
     */
    public String getName() {
        return getImage();
    }

    /**
     * Returns true if this enum constant defines a body,
     * which is compiled like an anonymous class.
     */
    public boolean isAnonymousClass() {
        return getFirstChildOfType(ASTAnonymousClassDeclaration.class) != null;
    }

}
