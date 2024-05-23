/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;

/**
 * This defines a compact constructor for a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 16 feature).
 * Compact constructors implicitly declares formal parameters corresponding to the record component list. These can be
 * fetched from {@link #getSymbol()}.
 *
 * <p>Compact record constructors must be declared "public".
 *
 * <pre class="grammar">
 *
 * CompactConstructorDeclaration ::=  {@link ASTModifierList Modifiers}
 *                                   &lt;IDENTIFIER&gt;
 *                                   {@link ASTBlock Block}
 *
 * </pre>
 */
// TODO make implicit formal parameter node and implement ASTExecutableDeclaration.
// This might help UnusedAssignmentRule / DataflowPass.ReachingDefsVisitor, see also #4603
public final class ASTCompactConstructorDeclaration extends AbstractJavaNode implements ASTBodyDeclaration, SymbolDeclaratorNode, ModifierOwner, JavadocCommentOwner {

    private JavaccToken identToken;

    ASTCompactConstructorDeclaration(int id) {
        super(id);
    }


    @Override
    public FileLocation getReportLocation() {
        return identToken.getReportLocation();
    }

    void setIdentToken(JavaccToken identToken) {
        this.identToken = identToken;
    }

    @Override
    public String getImage() {
        return identToken.getImage();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTBlock getBody() {
        return firstChild(ASTBlock.class);
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
