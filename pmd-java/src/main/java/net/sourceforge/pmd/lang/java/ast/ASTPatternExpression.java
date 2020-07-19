/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.AtLeastOneChild;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

/**
 * Wraps a {@link ASTPattern} node but presents the interface of {@link ASTExpression}.
 * This is only used in the following contexts:
 * <ul>
 * <li>As the right-hand side of {@link BinaryOp#INSTANCEOF instanceof expressions}.
 * </ul>
 *
 * <pre class="grammar">
 *
 * PatternExpression ::= {@link ASTPattern Pattern}
 *
 * </pre>
 */
public final class ASTPatternExpression extends AbstractJavaNode implements ASTPrimaryExpression, AtLeastOneChild, LeftRecursiveNode {

    ASTPatternExpression(int id) {
        super(id);
    }

    ASTPatternExpression(ASTPattern wrapped) {
        this(JavaParserImplTreeConstants.JJTPATTERNEXPRESSION);
        this.addChild((AbstractJavaNode) wrapped, 0);
        copyTextCoordinates((AbstractJavaNode) wrapped);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /** Gets the wrapped type node. */
    public ASTPattern getPattern() {
        return (ASTPattern) getChild(0);
    }


    /** Returns 0, patterns can never be parenthesized. */
    @Override
    public int getParenthesisDepth() {
        return 0;
    }

    /** Returns false, patterns can never be parenthesized. */
    @Override
    public boolean isParenthesized() {
        return false;
    }

    @Override
    public @Nullable JavaTypeDefinition getTypeDefinition() {
        ASTPattern pattern = getPattern();
        if (pattern instanceof ASTTypeTestPattern) {
            return ((ASTTypeTestPattern) pattern).getTypeNode().getTypeDefinition();
        }
        return null;
    }
}
