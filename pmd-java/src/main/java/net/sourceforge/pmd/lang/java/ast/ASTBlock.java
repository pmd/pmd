/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

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
public final class ASTBlock extends AbstractStatement implements Iterable<ASTStatement> {

    private boolean containsComment;

    ASTBlock(int id) {
        super(id);
    }

    ASTBlock(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public boolean containsComment() {
        return this.containsComment;
    }

    void setContainsComment() {
        this.containsComment = true;
    }

    @Override
    public Iterator<ASTStatement> iterator() {
        return children(ASTStatement.class).iterator();
    }


    @Override
    public ASTStatement jjtGetChild(int index) {
        return (ASTStatement) super.jjtGetChild(index);
    }
}
