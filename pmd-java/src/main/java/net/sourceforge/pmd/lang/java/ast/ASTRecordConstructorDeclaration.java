/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;

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
public final class ASTRecordConstructorDeclaration extends AbstractJavaNode implements ASTBodyDeclaration, SymbolDeclaratorNode, AccessNode {

    private JConstructorSymbol symbol;

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

    @Override
    public ASTRecordDeclaration getEnclosingType() {
        return (ASTRecordDeclaration) super.getEnclosingType();
    }

    @Override
    public JConstructorSymbol getSymbol() {
        return symbol;
    }

    void setSymbol(JConstructorSymbol symbol) {
        this.symbol = symbol;
    }
}
