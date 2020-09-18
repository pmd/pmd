/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;

/**
 * This defines a compact constructor for a {@link ASTRecordDeclaration RecordDeclaration}
 * (JDK 14 and JDK 15 preview feature). Compact constructors implicitly declares formal
 * parameters corresponding to the record component list. These can be
 * fetched from {@link #getSymbol()}.
 *
 * <p>Compact record constructors must be declared "public".
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

    ASTRecordConstructorDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTBlock getBody() {
        return getFirstChildOfType(ASTBlock.class);
    }

    public ASTRecordConstructorDeclaration getDeclarationNode() {
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
