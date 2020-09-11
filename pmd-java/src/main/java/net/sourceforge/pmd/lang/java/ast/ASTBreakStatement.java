/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * A break statement, that jumps to a named label (or exits the current loop).
 *
 * <pre class="grammar">
 *
 * BreakStatement ::= "break" &lt;IDENTIFIER&gt;? ";"
 *
 * </pre>
 */
public final class ASTBreakStatement extends AbstractStatement {

    private static final Function<JavaNode, ASTStatement> BREAK_TARGET_MAPPER =
        NodeStream.asInstanceOf(ASTLoopStatement.class, ASTSwitchStatement.class);

    ASTBreakStatement(int id) {
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
     * Returns the statement that is the target of this break. This is
     * most commonly a loop, or a switch statement (but any statement
     * may be labeled). This may return null if the code is invalid.
     */
    public ASTStatement getTarget() {
        String myLabel = this.getLabel();
        if (myLabel == null) {
            return ancestors().map(BREAK_TARGET_MAPPER).first();
        }
        return ancestors(ASTLabeledStatement.class)
            .filter(it -> it.getLabel().equals(myLabel))
            .map(ASTLabeledStatement::getStatement)
            .first();
    }

}
