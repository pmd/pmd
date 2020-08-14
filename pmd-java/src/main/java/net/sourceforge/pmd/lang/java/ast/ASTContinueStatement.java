/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * A continue statement, that jumps to the next iteration of an enclosing loop.
 *
 * <pre class="grammar">
 *
 * ContinueStatement ::= "continue" &lt;IDENTIFIER&gt;? ";"
 *
 * </pre>
 */
public final class ASTContinueStatement extends AbstractStatement {

    private static final Function<JavaNode, ASTLoopStatement> CONTINUE_TARGET_MAPPER =
        NodeStream.asInstanceOf(ASTLoopStatement.class);

    ASTContinueStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the label, or null if there is none.
     */
    public @Nullable String getLabel() {
        return getImage();
    }

    /**
     * Returns the statement that is the target of this break. This must
     * be a loop.
     */
    public ASTLoopStatement getTarget() {
        String myLabel = this.getLabel();
        if (myLabel == null) {
            return ancestors().map(CONTINUE_TARGET_MAPPER).first();
        }
        return (ASTLoopStatement) ancestors(ASTLabeledStatement.class)
            .filter(it -> it.getLabel().equals(myLabel))
            .map(ASTLabeledStatement::getStatement)
            .first();
    }

}
