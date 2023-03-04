/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.Chars;

/**
 * @author Clément Fournier
 */
abstract class AbstractLiteral extends AbstractJavaExpr implements ASTLiteral {

    private JavaccToken literalToken;

    AbstractLiteral(int i) {
        super(i);
    }


    /** @deprecated Use {@link #getLiteralText()}. */
    @Override
    @Deprecated
    public String getImage() {
        return null;
    }

    @Override
    public void jjtClose() {
        super.jjtClose();
        // Note that in this method, if the literal is parenthesized,
        // its parentheses have not yet been set yet so the text is
        // just the literal.
        assert getParenthesisDepth() == 0;
        assert getFirstToken() == getLastToken();
        literalToken = getFirstToken();
    }

    @Override
    public final Chars getText() {
        JavaccToken firstToken = getFirstToken();
        // this literal has parentheses, the text includes them
        if (firstToken.kind == JavaTokenKinds.LPAREN) {
            return super.getText();
        }
        return firstToken.getImageCs();
    }

    @Override
    public Chars getLiteralText() {
        return literalToken.getImageCs();
    }


    @Override
    public boolean isCompileTimeConstant() {
        return true; // note: NullLiteral overrides this to false
    }
}
