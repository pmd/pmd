/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

/**
 * @author Clément Fournier
 * @see ASTLiteral#getLiteralText()
 * @see #getLiteralText()
 */
// Note: This class must not implement ASTLiteral, see comment on #getLiteralText()
abstract class AbstractLiteral extends AbstractJavaExpr {

    private JavaccToken literalToken;

    AbstractLiteral(int i) {
        super(i);
    }

    @Override
    public void jjtClose() {
        super.jjtClose();
        // Note that in this method, if the literal is parenthesized,
        // its parentheses have not yet been set yet so the text is
        // just the literal.
        assert getParenthesisDepth() == 0;
        assert getFirstToken() == getLastToken(); // NOPMD
        literalToken = getFirstToken();
    }

    @Override
    @NoAttribute
    public final Chars getText() {
        JavaccToken firstToken = getFirstToken();
        // this literal has parentheses, the text includes them
        if (firstToken.kind == JavaTokenKinds.LPAREN) {
            return super.getText();
        }
        return firstToken.getImageCs();
    }

    // This method represents ASTLiteral#getLiteralText().
    // However, since this class is package private, this method is not reliably accessible
    // via reflection/method handles (see https://github.com/pmd/pmd/issues/4885).
    // Subclasses of this class need to implement ASTLiteral and override this method
    // as public.
    Chars getLiteralText() {
        assert literalToken.getImageCs() != null;
        return literalToken.getImageCs();
    }

    @Override
    public boolean isCompileTimeConstant() {
        return true; // note: NullLiteral overrides this to false
    }
}
