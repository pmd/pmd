/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

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
public final class ASTBlock extends AbstractStatement implements Iterable<ASTStatement>, ASTSwitchArrowRHS {

    ASTBlock(int id) {
        super(id);
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

    @Override
    public Iterator<ASTStatement> iterator() {
        return children(ASTStatement.class).iterator();
    }


    @Override
    public ASTStatement getChild(int index) {
        return (ASTStatement) super.getChild(index);
    }
}
