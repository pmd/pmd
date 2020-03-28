/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * This defines a compact constructor for a {@link ASTRecordDeclaration RecordDeclaration}
 * (JDK 14 preview feature).
 *
 * TODO make implicit formal parameter node and implement ASTMethodOrConstructorDeclaration.
 *
 * <pre class="grammar">
 *
 * RecordConstructorDeclaration ::=  {@link ASTModifierList Modifiers}
 *                                   &lt;IDENTIFIER&gt;
 *                                   {@link ASTBlock Block}
 *
 * </pre>
 */
@Experimental
public final class ASTRecordConstructorDeclaration extends AbstractJavaNode implements ASTBodyDeclaration {

    ASTRecordConstructorDeclaration(int id) {
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

    public ASTBlock getBody() {
        return getFirstChildOfType(ASTBlock.class);
    }
}
