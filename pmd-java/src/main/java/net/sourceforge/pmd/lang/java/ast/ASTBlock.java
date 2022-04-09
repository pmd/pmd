/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTList.ASTMaybeEmptyListOf;

/**
 * A block of code. This is a {@linkplain ASTStatement statement} that
 * contains other statements.
 *
 * <pre class="grammar">
 *
 * Block ::=  "{" {@link ASTStatement Statement}* "}"
 *
 * </pre>
 */
public final class ASTBlock extends ASTMaybeEmptyListOf<ASTStatement> implements Iterable<ASTStatement>, ASTSwitchArrowRHS, ASTStatement {

    ASTBlock(int id) {
        super(id, ASTStatement.class);
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public boolean containsComment() {
        JavaccToken t = getLastToken().getPreviousComment();
        while (t != null) {
            if (JavaTokenDocument.isComment(t)) {
                return true;
            }
            t = t.getPreviousComment();
        }

        return false;
    }
}
