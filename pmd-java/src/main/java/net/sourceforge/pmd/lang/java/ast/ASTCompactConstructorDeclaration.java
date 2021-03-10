/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;

/**
 * This defines a compact constructor for a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 16 feature).
 * Compact constructors implicitly declares formal parameters corresponding to the record component list. These can be
 * fetched from {@link #getSymbol()}.
 *
 * <p>Compact record constructors must be declared "public".
 *
 * TODO make implicit formal parameter node and implement ASTMethodOrConstructorDeclaration.
 *
 * <pre class="grammar">
 *
 * CompactConstructorDeclaration ::=  {@link ASTModifierList Modifiers}
 *                                   &lt;IDENTIFIER&gt;
 *                                   {@link ASTBlock Block}
 *
 * </pre>
 */
public final class ASTCompactConstructorDeclaration extends AbstractJavaNode implements ASTBodyDeclaration, SymbolDeclaratorNode, AccessNode {

    ASTCompactConstructorDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTBlock getBody() {
        return getFirstChildOfType(ASTBlock.class);
    }

    public ASTCompactConstructorDeclaration getDeclarationNode() {
        return this;
    }

    @Override
    public ASTRecordDeclaration getEnclosingType() {
        return (ASTRecordDeclaration) super.getEnclosingType();
    }

    @Override
    public JConstructorSymbol getSymbol() {
        return getEnclosingType().getRecordComponents().getSymbol();
    }
}
