/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

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
public final class ASTCompactConstructorDeclaration extends AbstractJavaNode implements ASTBodyDeclaration, SymbolDeclaratorNode, ModifierOwner, JavadocCommentOwner, ReturnScopeNode {

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

    @Override
    public @NonNull ASTBlock getBody() {
        return Objects.requireNonNull(firstChild(ASTBlock.class));
    }

    /**
     * @deprecated Since 7.14.0. This method just returns `this` and isn't useful.
     */
    @Deprecated
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
